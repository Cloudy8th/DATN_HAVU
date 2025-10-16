package com.project.hmartweb.domain.dtos;

import com.project.hmartweb.domain.enums.RoleId;
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
