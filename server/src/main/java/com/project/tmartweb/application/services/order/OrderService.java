package com.project.tmartweb.application.services.order;

import com.project.tmartweb.application.constant.MailTemplate;
import com.project.tmartweb.application.repositories.*;
import com.project.tmartweb.application.responses.Statistical;
import com.project.tmartweb.application.responses.VNPayResponse;
import com.project.tmartweb.application.services.cart.CartService;
import com.project.tmartweb.application.services.coupon.CouponService;
import com.project.tmartweb.application.services.email.IEmailService;
import com.project.tmartweb.application.services.payment.VNPayService;
import com.project.tmartweb.application.services.product.ProductService;
import com.project.tmartweb.application.services.user.UserService;
import com.project.tmartweb.config.exceptions.InvalidParamException;
import com.project.tmartweb.config.exceptions.NotFoundException;
import com.project.tmartweb.config.helpers.Calculator;
import com.project.tmartweb.domain.dtos.CartDTO;
import com.project.tmartweb.domain.dtos.OrderDTO;
import com.project.tmartweb.domain.entities.*;
import com.project.tmartweb.domain.enums.OrderStatus;
import com.project.tmartweb.domain.paginate.BasePagination;
import com.project.tmartweb.domain.paginate.PaginationDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;
    private final OrderDetailRepository orderDetailRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final ModelMapper mapper;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final NotificationRepository notificationRepository;
    private final VNPayService vnpayService;
    private final IEmailService emailService;

    @Value("${link.order-details}")
    private String linkOrderDetails;

    @Override
    @Transactional
    public Order insert(OrderDTO orderDTO) {
        Order order = mapper.map(orderDTO, Order.class);
        User user = userService.getById(orderDTO.getUserId());
        Coupon coupon = orderDTO.getCouponId() == null ? null : couponService.useCoupon(orderDTO.getCouponId());
        if (coupon != null) {
            coupon.setQuantity(coupon.getQuantity() - 1);
            couponRepository.save(coupon);
        }
        double discount = coupon == null ? 0 : coupon.getDiscount();
        order.setCreatedAt(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
        orderRepository.save(order);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartDTO cartDTO : orderDTO.getCartItems()) {
            Product product = productService.getById(cartDTO.getProductId());
            if (product.getQuantity() < cartDTO.getQuantity()) {
                throw new InvalidParamException("Số lượng sản phẩm trong kho không đủ",
                                                "Quantity not enough");
            }
            OrderDetail orderDetail = mapper.map(cartDTO, OrderDetail.class);
            orderDetail.setProduct(product);
            orderDetail.setOrder(order);
            orderDetail.setPrice(product.getSalePrice());
            orderDetail.setTotalMoney(
                    Calculator.totalMoney(
                            product.getSalePrice(), cartDTO.getQuantity()));
            orderDetails.add(orderDetailRepository.save(orderDetail));
            Cart cart = cartService.getById(cartDTO.getId());
            cartService.delete(cart);
            product.setQuantity(product.getQuantity() - cartDTO.getQuantity());
            productRepository.save(product);
        }
        order.setCoupon(coupon);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        Notification notification = new Notification();
        notification.setOrder(order);
        notification.setTitle("Đơn hàng đã tạo thành công");
        notification.setContent("Đơn hàng " + order.getId() + " đã được tạo thành công. " +
                                        " Bạn có thể theo dõi đơn hàng tại đây.");
        notification.setUser(user);
        notificationRepository.save(notification);
        order.setTotalMoney(Calculator.totalMoneyOrder(orderDetails, discount));
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order update(UUID id, OrderDTO orderDTO) {
        try {
            Order order = getById(id);
            order.setStatus(orderDTO.getStatus());
            Notification notification = new Notification();
            notification.setUser(order.getUser());
            notification.setOrder(order);
            if (orderDTO.getStatus() == OrderStatus.PROCESSED) {
                notification.setTitle("Đơn hàng đã xử lý thành công.");
                notification.setContent("Đơn hàng của bạn đã được xử lý thành công. " +
                                                " Chúng tôi sẽ giao cho đơn vị vận chuyển trong thời gian sớm nhất.");
            }
            if (orderDTO.getStatus() == OrderStatus.SHIPPING) {
                notification.setTitle("Đơn hàng đang được giao.");
                notification.setContent("Đơn hàng của bạn đã được giao cho đơn vị vận chuyển. " +
                                                " Hãy chú ý điện thoại nhé, đơn hàng sẽ được giao tới bạn trong thời gian sớm nhất có thể.");
            }
            if (orderDTO.getStatus() == OrderStatus.SHIPPED) {
                notification.setTitle("Đơn hàng đã giao thành công.");
                notification.setContent("Đơn hàng của bạn đã được giao thành công. " +
                                                " Hãy đánh trải nghiệm, đánh giá sản phẩm và nếu có lỗi gì hãy liên hệ với chúng tôi ngay nhé.");
                this.sendMailShippedOrder(order);
            }
            if (orderDTO.getStatus() == OrderStatus.CANCELLED) {
                notification.setTitle("Đơn hàng đã hủy thành công.");
                notification.setContent("Đơn hàng của bạn đã được hủy thành công. ");
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                    Product product = orderDetail.getProduct();
                    product.setQuantity(product.getQuantity() + orderDetail.getQuantity());
                    productRepository.save(product);
                }
            }
            notificationRepository.save(notification);
            if (orderDTO.getAddress() != null) {
                order.setAddress(orderDTO.getAddress());
            }
            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Order order) {

    }

    @Override
    public PaginationDTO<Order> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(orderRepository.findAll(
                    Sort.by("createdAt").descending()),
                                       null);
        }
        BasePagination<Order, OrderRepository> basePagination = new BasePagination<>(orderRepository);
        return basePagination.paginate(page, perPage);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Đơn hàng không tôn tại!", "Order not found"));
    }

    @Override
    public List<Order> findByUserId(UUID userId, OrderStatus status, String keyword) {
        return orderRepository.findByUserId(userId, status, keyword);
    }

    @Override
    public double totalMoneyOrder(List<Cart> carts, double discount) {
        double total = 0;
        for (Cart orderItem : carts) {
            total += Calculator.totalMoney(
                    orderItem.getProduct().getSalePrice(),
                    orderItem.getQuantity()
            );
        }
        total = total * (100 - discount) / 100;
        return total;
    }

    @Override
    public void FeedbackOrder(UUID orderId) {
        Order order = getById(orderId);
        order.setFeedback(true);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public VNPayResponse createOrder(OrderDTO orderDTO, HttpServletRequest request) {
        Order order = this.insert(orderDTO);
        String urlPayment = "";
        try {
            this.sendMailCreateOrder(order);
            if (order.getPaymentMethod().equals("VNPAY")) {
                urlPayment =
                        vnpayService.createOrder((int) order.getTotalMoney(), String.valueOf(order.getId()), request);
                order.setStatus(OrderStatus.UNPAID);
                orderRepository.save(order);
            }
            return new VNPayResponse("VNPay", urlPayment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int orderReturn(HttpServletRequest request) {
        int code = vnpayService.orderReturn(request);
        Order order = getById(UUID.fromString(request.getParameter("vnp_OrderInfo")));
        if (code == 1) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.UNPAID);
        }
        orderRepository.save(order);
        return code;
    }

    @Override
    public List<Statistical> statisticals(int year) {
        if (year == 0) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        List<Statistical> statistical = orderRepository.statistical(year);
        List<Statistical> result = new ArrayList<>();
        int currentMonthIndex = 0;
        for (int month = 1; month <= 12; month++) {
            if (currentMonthIndex < statistical.size() && statistical.get(currentMonthIndex).getMonth() == month) {
                result.add(statistical.get(currentMonthIndex));
                currentMonthIndex++;
            } else {
                result.add(new Statistical(month, 0.0));
            }
        }
        return result;
    }

    @Override
    public void sendMailCreateOrder(Order order) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String mailTo = order.getUser().getEmail();
        String subject = "Tạo đơn hàng hàng thành công";
        Map<String, Object> context = new HashMap<>();
        Timestamp orderDate = order.getCreatedAt();
        LocalDateTime dateTime = Instant.ofEpochMilli(orderDate.getTime())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime();
        String userReceiveName = order.getFullName();
        String phoneNumber = order.getPhoneNumber();
        String address = order.getAddress();
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(order.getId());
        double totalMoney = order.getTotalMoney();
        double totalMoneyNotDiscount = totalMoney;
        String linkOrder = linkOrderDetails + order.getId();
        context.put("CUSTOMER_NAME", order.getUser().getFullName());
        context.put("ORDER_DATE", dateTime.format(dateTimeFormatter));
        context.put("CUSTOMER_RECEIVE_NAME", userReceiveName);
        context.put("CUSTOMER_EMAIL", mailTo);
        context.put("CUSTOMER_PHONE", phoneNumber);
        context.put("CUSTOMER_ADDRESS", address);
        context.put("ORDER_ITEMS", orderDetails);
        context.put("ORDER_TOTAL", decimalFormat.format(totalMoney));
        context.put("ORDER_LINK", linkOrder);
        Coupon coupon = order.getCoupon();
        if (!ObjectUtils.isEmpty(coupon)) {
            String couponCode = coupon.getCode();
            double discount = coupon.getDiscount();
            if (discount > 0) {
                double totalMoneyDiscount = totalMoney / (100 - discount) * 100;
                double discountPrice = totalMoney - totalMoneyDiscount;
                context.put("ORDER_DISCOUNT_PRICE", decimalFormat.format(discountPrice));
                totalMoneyNotDiscount = totalMoney + (-1 * discountPrice);
            }
            context.put("ORDER_COUPON_CODE", couponCode);
        }
        context.put("ORDER_TOTAL_NOT_DISCOUNT", decimalFormat.format(totalMoneyNotDiscount));
        emailService.sendTemplateMail(mailTo, subject, MailTemplate.ORDER, context);
    }

    @Override
    public void sendMailShippedOrder(Order order) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String mailTo = order.getUser().getEmail();
        String subject = "Giao hàng thành công";
        Map<String, Object> context = new HashMap<>();
        Timestamp orderDate = order.getUpdatedAt();
        LocalDateTime dateTime = Instant.ofEpochMilli(orderDate.getTime())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime();
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderId(order.getId());
        double totalMoney = order.getTotalMoney();
        String linkOrder = linkOrderDetails + order.getId();
        double totalMoneyNotDiscount = totalMoney;
        context.put("CUSTOMER_NAME", order.getUser().getFullName());
        context.put("SHIPPED_DATE", dateTime.format(dateTimeFormatter));
        context.put("ORDER_ITEMS", orderDetails);
        context.put("ORDER_TOTAL", decimalFormat.format(totalMoney));
        context.put("ORDER_LINK", linkOrder);
        Coupon coupon = order.getCoupon();
        if (!ObjectUtils.isEmpty(coupon)) {
            String couponCode = coupon.getCode();
            double discount = coupon.getDiscount();
            if (discount > 0) {
                double totalMoneyDiscount = totalMoney / (100 - discount) * 100;
                double discountPrice = totalMoney - totalMoneyDiscount;
                context.put("ORDER_DISCOUNT_PRICE", decimalFormat.format(discountPrice));
                totalMoneyNotDiscount = totalMoney + (-1 * discountPrice);
            }
            context.put("ORDER_COUPON_CODE", couponCode);
        }
        context.put("ORDER_TOTAL_NOT_DISCOUNT", decimalFormat.format(totalMoneyNotDiscount));
        emailService.sendTemplateMail(mailTo, subject, MailTemplate.SHIPPED, context);
    }

    @Override
    public PaginationDTO<Order> getAllByFilter(Timestamp startDate,
                                               Timestamp endDate,
                                               OrderStatus status,
                                               Integer page,
                                               Integer perPage) {
        if (startDate != null && endDate == null) {
            endDate = new Timestamp(System.currentTimeMillis());
        }
        Page<Order> orders = orderRepository.findAllByFilter(
                startDate, endDate, status, PageRequest.of(page, perPage));
        BasePagination<Order, OrderRepository> pagination = new BasePagination<>();
        return pagination.paginate(page, perPage, orders);
    }
}
