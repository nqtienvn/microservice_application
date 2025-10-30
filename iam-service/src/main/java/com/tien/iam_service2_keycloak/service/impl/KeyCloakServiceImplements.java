package com.tien.iam_service2_keycloak.service.impl;

import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.iam_service2_keycloak.dto.request.CreateUserRequest;
import com.tien.iam_service2_keycloak.dto.request.RegisterRequest;
import com.tien.iam_service2_keycloak.dto.request.UpdateRequest;
import com.tien.iam_service2_keycloak.dto.response.RegisterResponse;
import com.tien.iam_service2_keycloak.entity.User;
import com.tien.iam_service2_keycloak.mapper.UserMapper;
import com.tien.iam_service2_keycloak.repository.RoleRepository;
import com.tien.iam_service2_keycloak.repository.UserRepository;
import com.tien.iam_service2_keycloak.role_permission_enum.Role_System;
import com.tien.iam_service2_keycloak.service.KeycloakService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Anh_TIEN_keycloak_service")
public class KeyCloakServiceImplements implements KeycloakService {
    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final RoleRepository roleRepository;
    @Value("${iam.keycloak.realm}")
    private String realm;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        //create user in KeyCloak
        UserRepresentation user = getUserRepresentation(registerRequest);
        Response response = keycloak.realm(realm).users().create(user);
        log.info("realm: {}", realm);
        if (response.getStatus() != 201) {
            log.debug("create status: {}", response.getStatus());
            throw new AppException(ErrorCode.ERROR_KEYCLOAK_USER);
        }
        User userSql = userMapper.toUser(registerRequest);
        userSql.setEnabled(true);
        userSql.setRoles(Set.of(roleRepository.findByName(Role_System.USER.toString()).orElseThrow()));
        return userMapper.toRegisterResponse(userRepository.save(userSql));
    }


    private static UserRepresentation getUserRepresentation(RegisterRequest registerRequest) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEnabled(true);
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registerRequest.getPassword());
        user.setCredentials(List.of(credential));
        return user;
    }

    @Value("${iam.keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${iam.keycloak.client-id}")
    private String clientId;

    @Value("${iam.keycloak.client-secret}")
    private String clientSecret;

    @Override
    public String refreshNewToken(String refreshToken) {
        String url = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        return restTemplate.postForObject(url, request, String.class);
    }

    @Override
    public String logout(String refreshToken) {
        String url = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        restTemplate.postForObject(url, request, String.class); //da log out xong
        String urlIntrospect = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token/introspect";
        HttpHeaders headerIntrospect = new HttpHeaders();
        headerIntrospect.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> formDataIntrospect = new LinkedMultiValueMap<>();
        formDataIntrospect.add("client_id", clientId);
        formDataIntrospect.add("client_secret", clientSecret);
        formDataIntrospect.add("token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> requestIntrospect = new HttpEntity<>(formDataIntrospect, headerIntrospect);
        return restTemplate.postForObject(urlIntrospect, requestIntrospect, String.class);
        //da log out xong
    }

    public String getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "admin-cli");
        formData.add("username", "admin");
        formData.add("password", "admin");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(keycloakServerUrl + "/realms/master/protocol/openid-connect/token", request, Map.class);
            Map body = response.getBody();
            assert body != null;
            return (String) body.get("access_token");
        } catch (Exception e) {
            log.error("Lỗi khi lấy admin token: {}", e.getMessage());
            throw new AppException(ErrorCode.ADMIN_TOKEN_NOT_FOUND);
        }
    }

    @Override
    public String createUser(CreateUserRequest createUserRequest) {
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        String adminToken = getAdminToken();
        header.setBearerAuth(adminToken);
        Map<String, Object> dataRequest = new HashMap<>();
        dataRequest.put("username", createUserRequest.getUsername());
        dataRequest.put("email", createUserRequest.getEmail());
        dataRequest.put("firstName", createUserRequest.getFirstName());
        dataRequest.put("lastName", createUserRequest.getLastName());
        dataRequest.put("enabled", true);
        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", createUserRequest.getPass());
        credential.put("temporary", false);
        dataRequest.put("credentials", List.of(credential));
        log.info("infor_requets_create_user: {}", dataRequest);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(dataRequest, header);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class
            );

            String location = Objects.requireNonNull(response.getHeaders().getLocation()).toString();
            String userId = location.substring(location.lastIndexOf('/') + 1);

            log.info("created user in Keycloak, KeyCloak id is: {}", userId);
            return userId;

        } catch (HttpClientErrorException e) {
            log.error("Lỗi khi tạo user trong Keycloak: {}", e.getResponseBodyAsString());
            throw new AppException(ErrorCode.CREATED_USER_KEYCLOAK_ERROR);
        }
    }

    @Override
    public void updateUser(UpdateRequest updateRequest, String keycloakUserId) {
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId;
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        String adminToken = getAdminToken();
        header.setBearerAuth(adminToken);
        Map<String, Object> dataRequest = new HashMap<>();
        dataRequest.put("email", updateRequest.getEmail());
        dataRequest.put("firstName", updateRequest.getFirstName());
        dataRequest.put("lastName", updateRequest.getLastName());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(dataRequest, header);
        restTemplate.put(url, request, String.class);
    }

    @Override
    public void softDelete(String keycloakUserId) {
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId;
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        String adminToken = getAdminToken();
        header.setBearerAuth(adminToken);
        Map<String, Object> dataRequest = new HashMap<>();
        dataRequest.put("enabled", false);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(dataRequest, header);
        restTemplate.put(url, request, String.class);
    }

    @Override
    public void blockUser(String keycloakUserId) {
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId;
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        String adminToken = getAdminToken();
        header.setBearerAuth(adminToken);
        Map<String, Object> dataRequest = new HashMap<>();
        dataRequest.put("enabled", false);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(dataRequest, header);
        restTemplate.put(url, request, String.class);
    }

    @Override
    public void unblockUser(String keycloakUserId) {
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId;
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        String adminToken = getAdminToken();
        header.setBearerAuth(adminToken);
        Map<String, Object> dataRequest = new HashMap<>();
        dataRequest.put("enabled", true);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(dataRequest, header);
        restTemplate.put(url, request, String.class);
    }
}