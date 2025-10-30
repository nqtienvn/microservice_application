package com.tien.iam_service2_keycloak.role_permission_enum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public enum Permission_System {
    USER_CREATE,
    USER_EDIT,
    USER_DELETE,
    USER_VIEW,
    USER_PROFILE,
    ROLE_CREATE,
    ROLE_EDIT,
    ROLE_DELETE,
    ROLE_VIEW,
    PERMISSION_VIEW,
    PERMISSION_DELETE,
    PERMISSION_CREATE,
    PERMISSION_UPDATE,
    ROLE_PERMISSION_UPDATE,
    ROLE_PERMISSION_ADD,
    USER_ROLE_ADD,
    USER_ROLE_UPDATE
    ;
}
