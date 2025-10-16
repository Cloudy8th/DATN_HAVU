package com.project.hmartweb.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserChangePassword {

    @NotBlank(message = "Mật khẩu cũ không được để trống!")
    private String oldPassword;
    @NotBlank(message = "Mật khẩu mới không được để trống!")
    private String newPassword;
    private String confirmPassword;
}
