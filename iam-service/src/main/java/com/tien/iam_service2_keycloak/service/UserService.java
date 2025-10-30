package com.tien.iam_service2_keycloak.service;

import com.tien.iam_service2_keycloak.dto.request.CreateUserRequest;
import com.tien.iam_service2_keycloak.dto.request.UpdateRequest;
import com.tien.iam_service2_keycloak.dto.request.UserRoleRequest;
import com.tien.iam_service2_keycloak.dto.response.CreateUserResponse;
import com.tien.iam_service2_keycloak.dto.response.UserInformResponse;
import com.tien.iam_service2_keycloak.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest createUserRequest);

    CreateUserResponse updateUser(UpdateRequest updateRequest, Long userId);

    Boolean deleteUser(Long userId);

    Boolean blockUser(Long userId);

    Boolean unBlockUser(Long userId);

    Page<UserInformResponse> getAllUsers(int page, int size);

    CreateUserResponse userDetail(Long userId);

    CreateUserResponse updateRoleForUser(Long id, UserRoleRequest userRoleRequest);

    CreateUserResponse addRoleUser(Long id, UserRoleRequest userRoleRequest);

    List<User> filter(String firstName, String lastName);
}
