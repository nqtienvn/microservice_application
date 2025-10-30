package com.tien.iam_service2_keycloak.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuthenticationResponse {
    String acessToken;
    String refreshToken;
    boolean checkLogin;
    String uri;
}
