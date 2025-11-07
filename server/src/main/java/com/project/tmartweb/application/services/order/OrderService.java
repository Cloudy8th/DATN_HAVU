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
import com.project.tmartweb.application.responses.RevenueByDate;
import com.project.tmartweb.application.responses.RevenueByWeek;
import com.project.tmartweb.application.responses.ProductSalesStatistical;
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
                throw new InvalidParamException("SoÌ‚Ì luÌ›oÌ›Ì£ng saÌ‰n phaÌ‚Ì‰m trong kho khoÌ‚ng Ä‘uÌ‰",
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
        notification.setTitle("ÄÆ¡n hÃ ng Ä‘Ã£ táº¡o thÃ nh cÃ´ng");
        notification.setContent("ÄÆ¡n hÃ ng " + order.getId() + " Ä‘Ã£ Ä‘Æ°á»£c táº¡o thÃ nh cÃ´ng. " +
                " Báº¡n cÃ³ thá»ƒ theo dÃµi Ä‘Æ¡n hÃ ng táº¡i Ä‘Ã¢y.");
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
                notification.setTitle("ÄÆ¡n hÃ ng Ä‘Ã£ xá»­ lÃ½ thÃ nh cÃ´ng.");
                notification.setContent("ÄÆ¡n hÃ ng cá»§a báº¡n Ä‘Ã£ Ä‘Æ°á»£c xá»­ lÃ½ thÃ nh cÃ´ng. " +
                        " ChÃºng tÃ´i sáº½ giao cho Ä‘Æ¡n vá»‹ váº­n chuyá»ƒn trong thá»i gian sá»›m nháº¥t.");
            }
            if (orderDTO.getStatus() == OrderStatus.SHIPPING) {
                notification.setTitle("ÄÆ¡n hÃ ng Ä‘ang Ä‘Æ°á»£c giao.");
                notification.setContent("ÄÆ¡n hÃ ng cá»§a báº¡n Ä‘Ã£ Ä‘Æ°á»£c giao cho Ä‘Æ¡n vá»‹ váº­n chuyá»ƒn. " +
                        " HÃ£y chÃº Ã½ Ä‘iá»‡n thoáº¡i nhÃ©, Ä‘Æ¡n hÃ ng sáº½ Ä‘Æ°á»£c giao tá»›i báº¡n trong thá»i gian sá»›m nháº¥t cÃ³ thá»ƒ.");
            }
            if (orderDTO.getStatus() == OrderStatus.SHIPPED) {
                notification.setTitle("ÄÆ¡n hÃ ng Ä‘Ã£ giao thÃ nh cÃ´ng.");
                notification.setContent("ÄÆ¡n hÃ ng cá»§a báº¡n Ä‘Ã£ Ä‘Æ°á»£c giao thÃ nh cÃ´ng. " +
                        " HÃ£y Ä‘Ã¡nh tráº£i nghiá»‡m, Ä‘Ã¡nh giÃ¡ sáº£n pháº©m vÃ  náº¿u cÃ³ lá»—i gÃ¬ hÃ£y liÃªn há»‡ vá»›i chÃºng tÃ´i ngay nhÃ©.");
                this.sendMailShippedOrder(order);
            }
            if (orderDTO.getStatus() == OrderStatus.CANCELLED) {
                notification.setTitle("ÄÆ¡n hÃ ng Ä‘Ã£ há»§y thÃ nh cÃ´ng.");
                notification.setContent("ÄÆ¡n hÃ ng cá»§a báº¡n Ä‘Ã£ Ä‘Æ°á»£c há»§y thÃ nh cÃ´ng. ");
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
        return findById(id).orElseThrow(() -> new NotFoundException("ÄÆ¡n hÃ ng khÃ´ng tÃ´n táº¡i!", "Order not found"));
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

        // Táº¡o ngÃ y báº¯t Ä‘áº§u vÃ  káº¿t thÃºc cho nÄƒm
        Calendar cal = Calendar.getInstance();
        cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        Timestamp startDate = new Timestamp(cal.getTimeInMillis());

        cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        Timestamp endDate = new Timestamp(cal.getTimeInMillis());

        // Gá»i phÆ°Æ¡ng thá»©c repository Má»šI
        List<Statistical> statistical = orderRepository.statisticalByMonth(startDate, endDate);
        List<Statistical> result = new ArrayList<>();

        int currentMonthIndex = 0;
        for (int month = 1; month <= 12; month++) {
            if (currentMonthIndex < statistical.size() && statistical.get(currentMonthIndex).getMonth() == month) {
                // ThÃªm nÄƒm vÃ o Ä‘á»‘i tÆ°á»£ng tráº£ vá»
                Statistical s = statistical.get(currentMonthIndex);
                s.setYear(year);
                result.add(s);
                currentMonthIndex++;
            } else {
                // ThÃªm thá»‘ng kÃª (nÄƒm, thÃ¡ng, 0.0) cho cÃ¡c thÃ¡ng khÃ´ng cÃ³ doanh thu
                result.add(new Statistical(year, month, 0.0));
            }
        }
        return result;
    }

    // TRIá»‚N KHAI CÃC PHÆ¯Æ NG THá»¨C Má»šI

    @Override
    public List<Statistical> getMonthlyStats(Timestamp startDate, Timestamp endDate) {
        // Cáº§n logic láº¥p Ä‘áº§y cÃ¡c thÃ¡ng bá»‹ thiáº¿u tÆ°Æ¡ng tá»± nhÆ° hÃ m statisticals(int year) náº¿u muá»‘n
        return orderRepository.statisticalByMonth(startDate, endDate);
    }

    @Override
    public List<RevenueByDate> getDailyStats(Timestamp startDate, Timestamp endDate) {
        return orderRepository.statisticalByDay(startDate, endDate);
    }

    @Override
    public List<RevenueByWeek> getWeeklyStats(Timestamp startDate, Timestamp endDate) {
        return orderRepository.statisticalByWeek(startDate, endDate);
    }

    @Override
    public List<ProductSalesStatistical> getProductSalesStats(Timestamp startDate, Timestamp endDate) {
        // PhÆ°Æ¡ng thá»©c nÃ y náº±m á»Ÿ OrderDetailRepository
        return orderDetailRepository.statisticalByProduct(startDate, endDate);
    }

    @Override
    public void sendMailCreateOrder(Order order) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String mailTo = order.getUser().getEmail();
        String subject = "Táº¡o Ä‘Æ¡n hÃ ng hÃ ng thÃ nh cÃ´ng";
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
        String subject = "Giao hÃ ng thÃ nh cÃ´ng";
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