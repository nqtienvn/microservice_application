package com.tien.iam_service2_keycloak.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRequest {
    String email;
    String firstName;
    String lastName;
}
