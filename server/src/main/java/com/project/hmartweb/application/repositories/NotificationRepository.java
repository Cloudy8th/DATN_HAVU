package com.project.hmartweb.application.repositories;

import com.project.hmartweb.domain.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findAllByUserId(UUID userId, Pageable pageable);

    List<Notification> findAllByUserId(UUID userId);

    List<Notification> findAllByUserIdAndRead(UUID userId, boolean read, Sort sort);
}
