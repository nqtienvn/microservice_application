package com.tien.iam_service2_keycloak.controller;

import com.tien.common.dto.response.ApiResponse;
import com.tien.iam_service2_keycloak.dto.request.AuthenticationRequest;
import com.tien.iam_service2_keycloak.dto.response.AuthenticationResponse;
import com.tien.iam_service2_keycloak.service.impl.TurnOnKeycloak;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final TurnOnKeycloak turnOnKeycloak;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody(required = false) AuthenticationRequest userLogin) {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("success")
                .result(turnOnKeycloak.login(userLogin))
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<String> refresh(@RequestParam String refreshToken) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Refresh Successfully!")
                .result(turnOnKeycloak.refreshToken(refreshToken))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<AuthenticationResponse> logout(@RequestParam(name = "refresh_token", required = false) String refreshToken,
                                                      @RequestHeader(value = "Authorization", required = false) String bearertoken) {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("success")
                .result(turnOnKeycloak.logout(refreshToken, bearertoken))
                .build();
    }

    @PostMapping("/admin-token")
    public ApiResponse<String> getAdminToken() {
        return ApiResponse.<String>builder()
                .code(200)
                .message("get admin token Successfully!")
                .result(turnOnKeycloak.getAdminToken())
                .build();
    }
}
