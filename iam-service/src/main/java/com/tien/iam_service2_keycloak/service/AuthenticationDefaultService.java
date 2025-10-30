package com.tien.iam_service2_keycloak.service;


import com.tien.iam_service2_keycloak.dto.request.AuthenticationRequest;
import com.tien.iam_service2_keycloak.dto.response.AuthenticationResponse;

public interface AuthenticationDefaultService {
    AuthenticationResponse login(AuthenticationRequest authenticationRequest);

    AuthenticationResponse logout(String token);
}
