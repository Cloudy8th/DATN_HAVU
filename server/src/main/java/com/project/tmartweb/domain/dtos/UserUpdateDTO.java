package com.project.tmartweb.domain.dtos;

import com.project.tmartweb.domain.enums.RoleId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDTO {
    private String userName;

    private String fullName;

    private String phoneNumber;

    private String email;

    private Date dateOfBirth;

    private String address;

    private RoleId roleId;

    private Boolean deleted;
}
