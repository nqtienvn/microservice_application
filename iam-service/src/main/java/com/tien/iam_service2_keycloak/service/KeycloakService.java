package com.tien.iam_service2_keycloak.service;

import com.tien.iam_service2_keycloak.dto.request.CreateUserRequest;
import com.tien.iam_service2_keycloak.dto.request.RegisterRequest;
import com.tien.iam_service2_keycloak.dto.request.UpdateRequest;
import com.tien.iam_service2_keycloak.dto.response.RegisterResponse;

public interface KeycloakService {
    RegisterResponse register(RegisterRequest registerRequest);

    String refreshNewToken(String refreshToken);

    String logout(String refreshToken);

    String createUser(CreateUserRequest createUserRequest);

    void updateUser(UpdateRequest updateRequest, String keycloakUserId);

    void softDelete(String keycloakUserId);

    void blockUser(String keycloakUserId);

    void unblockUser(String keycloakUserId);
}