package com.project.tmartweb.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserLoginDTO {
    private String userName;

    @NotBlank(message = "Mật khẩu không được để trống!")
    private String password;

    private String phoneNumber;

    private String email;
}
