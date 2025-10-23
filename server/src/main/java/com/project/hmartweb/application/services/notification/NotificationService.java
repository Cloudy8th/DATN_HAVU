package com.project.hmartweb.application.services.notification;

import com.project.hmartweb.application.repositories.NotificationRepository;
import com.project.hmartweb.application.services.order.IOrderService;
import com.project.hmartweb.application.services.user.IUserService;
import com.project.hmartweb.config.exceptions.NotFoundException;
import com.project.hmartweb.domain.dtos.NotificationDTO;
import com.project.hmartweb.domain.entities.Notification;
import com.project.hmartweb.domain.entities.Order;
import com.project.hmartweb.domain.entities.User;
import com.project.hmartweb.domain.paginate.BasePagination;
import com.project.hmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final IUserService userService;
    private final IOrderService orderService;
    private final ModelMapper mapper;

    @Override
    public Notification insert(NotificationDTO notificationDTO) {
        Notification notification = mapper.map(notificationDTO, Notification.class);
        User user = userService.getById(notificationDTO.getUserId());
        Order order = orderService.getById(notificationDTO.getOrderId());
        notification.setUser(user);
        notification.setOrder(order);
        return notificationRepository.save(notification);
    }

    @Override
    public Notification update(UUID id, NotificationDTO notificationDTO) {
        Notification notification = getById(id);
        mapper.map(notificationDTO, notification);
        User user = userService.getById(notificationDTO.getUserId());
        Order order = orderService.getById(notificationDTO.getOrderId());
        notification.setUser(user);
        notification.setOrder(order);
        return notificationRepository.save(notification);
    }

    @Override
    public void delete(Notification notification) {
        notificationRepository.delete(notification);
    }

    @Override
    public PaginationDTO<Notification> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(notificationRepository.findAll(), null);
        }
        BasePagination<Notification, NotificationRepository> basePagination = new BasePagination<>(notificationRepository);
        return basePagination.paginate(page, perPage);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return notificationRepository.findById(id);
    }

    @Override
    public Notification getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Thông báo không tồn tại!", "Notification not found!"));
    }

    @Override
    public Notification readNotification(UUID id) {
        Notification notification = getById(id);
        notification.setRead(Boolean.TRUE);
        return notificationRepository.save(notification);
    }

    @Override
    public PaginationDTO<Notification> getAllByUser(UUID userId, Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(notificationRepository.findAllByUserId(userId), null);
        }
        BasePagination<Notification, NotificationRepository> basePagination = new BasePagination<>(notificationRepository);
        int currentPage = page != null ? page : 0;
        int currentPerPage = perPage != null ? perPage : 10;
        Page<Notification> pageNotifications = notificationRepository.findAllByUserId(
                userId,
                PageRequest.of(currentPage, currentPerPage, Sort.by("createdAt").descending())
        );
        return basePagination.paginate(page, perPage, pageNotifications);
    }

    @Override
    public List<Notification> getAllByUserAndRead(UUID userId) {
        return notificationRepository.findAllByUserIdAndRead(userId, false, Sort.by("createdAt").descending());
    }

    @Override
    public void readAllNotifications(UUID userId) {
        List<Notification> notifications = notificationRepository.findAll();
        User user = userService.getById(userId);
        for (Notification notification : notifications) {
            if (notification.getUser().equals(user)) {
                notification.setRead(Boolean.TRUE);
                notificationRepository.save(notification);
            }
        }
    }
}
