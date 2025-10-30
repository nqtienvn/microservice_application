package com.tien.iam_service2_keycloak.dto.response;

import com.tien.iam_service2_keycloak.entity.Permission;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RoleResponse {
    Long id;
    String name;
    String description;
    Set<Permission> permissions;
}
