package com.project.tmartweb.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEditProfileDTO {

    private String userName;

    private String fullName;

    private String phoneNumber;

    private String address;

    private String email;

    private String dateOfBirth;
}
