package com.tien.iam_service2_keycloak.dto.request;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    private String username;
    private String email;
    private String pass;
    private String firstName;
    private String lastName;
    @Nullable
    private Set<String> roleName;
}
