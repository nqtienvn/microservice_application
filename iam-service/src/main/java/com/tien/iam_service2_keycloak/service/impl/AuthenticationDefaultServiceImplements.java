package com.tien.iam_service2_keycloak.service.impl;

import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.iam_service2_keycloak.dto.request.AuthenticationRequest;
import com.tien.iam_service2_keycloak.dto.response.AuthenticationResponse;
import com.tien.iam_service2_keycloak.repository.UserRepository;
import com.tien.iam_service2_keycloak.service.AuthenticationDefaultService;
import com.tien.iam_service2_keycloak.service.BaseRedisV2Service;
import com.tien.iam_service2_keycloak.service.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Auth_service_implements")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationDefaultServiceImplements implements AuthenticationDefaultService {
    AuthenticationManager authenticationManager;
    CustomUserDetailService customerUserDetailService;
    JwtService jwtService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    BaseRedisV2Service baseRedisV2Service;

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest
                        .getUsername(), authenticationRequest.getPass()));
        if (auth.isAuthenticated()) {
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setCheckLogin(true);
            authenticationResponse.setAcessToken(jwtService
                    .generateAccessToken(customerUserDetailService
                            .loadUserByUsername(authenticationRequest
                                    .getUsername())));
            String refreshToken = jwtService
                    .generateRefreshToken(customerUserDetailService
                            .loadUserByUsername(authenticationRequest
                                    .getUsername()));
            authenticationResponse.setRefreshToken(refreshToken);
            log.info(jwtService.extracId(refreshToken));
            baseRedisV2Service.set("refreshToken", jwtService.extracId(refreshToken));
            baseRedisV2Service.setTimeToLive("refreshToken", 60 * 60 * 24 * 7);
            return authenticationResponse;
        }
        throw new AppException(ErrorCode.INVALID_LOGIN);
    }

    @Override
    public AuthenticationResponse logout(String token) {
        String tokenId = jwtService.extracId(token);
        if (jwtService.isTokenExpired(token)) { //neu token het han
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setCheckLogin(false);
            return authenticationResponse;
        }
        //chua lam xoa refresh token
        baseRedisV2Service.delete("refreshToken");

        //lam ban them acces token vao redis
        baseRedisV2Service.set(tokenId, "accessToken");
        baseRedisV2Service.setTimeToLive(tokenId, jwtService
                .extractExpiration(token).getTime() - System.currentTimeMillis());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setCheckLogin(true);
        return authenticationResponse;
    }
}

