package com.tien.iam_service2_keycloak.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisterResponse {
     Long id;
     String username;
     String email;
     String firstName;
     String lastName;
     boolean enabled;
}
