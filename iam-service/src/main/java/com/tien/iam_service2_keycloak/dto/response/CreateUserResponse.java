package com.tien.iam_service2_keycloak.dto.response;

import com.tien.iam_service2_keycloak.entity.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateUserResponse {
    Long id;
    String username;
    String email;
    String firstName;
    String lastName;
    boolean enabled;
    boolean deleted;
    String keycloakUserId;
    Set<Role> roles;
}
