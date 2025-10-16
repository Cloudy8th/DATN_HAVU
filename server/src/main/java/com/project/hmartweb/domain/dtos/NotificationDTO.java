package com.project.hmartweb.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationDTO {
    private String title;

    @NotBlank(message = "Nội dung thông báo không được để trống!")
    private String content;

    private Boolean read = Boolean.FALSE;

    private UUID userId;

    private UUID orderId;
}
