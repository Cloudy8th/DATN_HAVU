package com.project.tmartweb.domain.enums;

import lombok.Getter;

@Getter
public enum RoleId {
    ADMIN("admin"),
    USER("user"),
    SHIPPER("shipper");
    private final String roleName;

    RoleId(String roleName) {
        this.roleName = roleName;
    }
}
