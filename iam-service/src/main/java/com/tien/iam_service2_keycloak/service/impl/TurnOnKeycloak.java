package com.tien.iam_service2_keycloak.service.impl;

import com.tien.iam_service2_keycloak.dto.request.AuthenticationRequest;
import com.tien.iam_service2_keycloak.dto.response.AuthenticationResponse;
import com.tien.iam_service2_keycloak.service.AuthenticationDefaultService;
import com.tien.iam_service2_keycloak.service.JwtService;
import com.tien.iam_service2_keycloak.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TurnOnKeycloak {
    private final AuthenticationDefaultService authenticationDefaultService;
    private final KeycloakService keycloakService;
    private final JwtService jwtService;
    private final KeyCloakServiceImplements keycloakServiceImplements;
    @Value("${iam.keycloak.realm}")
    private String realm;
    @Value("${iam.keycloak.client-id}")
    private String clientId;
    @Value("${iam.keycloak.auth-server-url}")
    private String baseUrl;
    @Value("${iam.use-keycloak:false}")
    private boolean useKeycloak;

    public AuthenticationResponse login(AuthenticationRequest userLogin) {
        if (useKeycloak) {
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setUri(baseUrl + "/realms/" + realm + "/protocol/openid-connect/auth" + "?client_id=" + clientId + "&response_type=code" + "&redirect_uri=http://localhost:8080/authentication");
            return authenticationResponse;
        }
        return authenticationDefaultService.login(userLogin);
    }

    public String refreshToken(String refreshToken) {
        if (useKeycloak) {
            return keycloakService.refreshNewToken(refreshToken);
        }
        return jwtService.refreshAcessToken(refreshToken);
    }

    public AuthenticationResponse logout(String refreshToken, String bearertoken) {
        if (useKeycloak) {
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setRefreshToken(keycloakService.logout(refreshToken));
            return authenticationResponse;
        }
        String token = bearertoken.substring(7);
        return authenticationDefaultService.logout(token);
    }

    public String getAdminToken() {
        if (useKeycloak) {
            return keycloakServiceImplements.getAdminToken();
        }
        return "khong lay duoc admin token o che do default";
    }
}
