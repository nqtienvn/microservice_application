package com.tien.iam_service2_keycloak.dto.response;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class UserInformResponse {
    String username;
    String email;
    String firstName;
    String lastName;
    boolean enabled;
    boolean deleted;
    String createdBy;
    Instant createdDate;
    String modifiedBy;
    Instant modifiedDate;
}
