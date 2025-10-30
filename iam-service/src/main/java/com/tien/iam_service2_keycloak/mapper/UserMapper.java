package com.tien.iam_service2_keycloak.mapper;

import com.tien.iam_service2_keycloak.dto.request.CreateUserRequest;
import com.tien.iam_service2_keycloak.dto.request.RegisterRequest;
import com.tien.iam_service2_keycloak.dto.response.CreateUserResponse;
import com.tien.iam_service2_keycloak.dto.response.RegisterResponse;
import com.tien.iam_service2_keycloak.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequest registerRequest);
    RegisterResponse toRegisterResponse(User user);
    CreateUserResponse toCreateUserResponse(User user);
    User toUser(CreateUserRequest createUserRequest);
}
