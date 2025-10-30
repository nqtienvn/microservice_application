package com.tien.iam_service2_keycloak.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    boolean validateToken(String token, UserDetails useDetails);

    String extractEmail(String token);

    String extracId(String token);

    boolean isTokenExpired(String token);

    Date extractExpiration(String token);

    String refreshAcessToken(String refreshToken);
}