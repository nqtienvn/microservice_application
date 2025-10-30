package com.tien.iam_service2_keycloak.role_permission_enum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public enum Role_System {
    ADMIN, //like admin
    SYSTEM_ADMIN,
    USER;
}
