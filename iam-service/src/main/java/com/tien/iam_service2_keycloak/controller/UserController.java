package com.tien.iam_service2_keycloak.controller;

import com.tien.common.dto.response.ApiResponse;
import com.tien.iam_service2_keycloak.dto.request.CreateUserRequest;
import com.tien.iam_service2_keycloak.dto.request.UpdateRequest;
import com.tien.iam_service2_keycloak.dto.request.UserRoleRequest;
import com.tien.iam_service2_keycloak.dto.response.CreateUserResponse;
import com.tien.iam_service2_keycloak.dto.response.UserInformResponse;
import com.tien.iam_service2_keycloak.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/iam-service/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "manage user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "register new user",
            description = "API create new user") //thong tin endpoint do
    @PostMapping()
    public ApiResponse<CreateUserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return ApiResponse.<CreateUserResponse>builder()
                .code(200)
                .message("success")
                .result(userService.createUser(createUserRequest))
                .build();
    }

    @Operation(summary = "update user",
            description = "API update user")
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ApiResponse<CreateUserResponse> updateUser(@RequestBody UpdateRequest updateRequest, @PathVariable Long userId) {
        return ApiResponse.<CreateUserResponse>builder()
                .code(200)
                .message("success")
                .result(userService.updateUser(updateRequest, userId))
                .build();
    }

    @Operation(summary = "soft delete user",
            description = "API to soft delete user")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ApiResponse<Boolean> softDelete(@PathVariable Long userId) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message("success")
                .result(userService.deleteUser(userId))
                .build();
    }

    @Operation(summary = "get all user",
            description = "API to get all user")
    @GetMapping()
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public Page<UserInformResponse> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size
    ) {
        return userService.getAllUsers(page, size);
    }

    @Operation(summary = "block user",
            description = "API to block user")
    @PostMapping("/lock/{userId}")
    @PreAuthorize("hasAuthority('USER_LOCK')")
    public ApiResponse<Boolean> blockUser(@PathVariable Long userId) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message("success")
                .result(userService.blockUser(userId))
                .build();
    }

    @Operation(summary = "un block user",
            description = "API to unblock user")
    @PostMapping("/un-lock/{userId}")
    @PreAuthorize("hasAuthority('USER_UNLOCK')")
    public ApiResponse<Boolean> unBlockUser(@PathVariable Long userId) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message("success")
                .result(userService.unBlockUser(userId))
                .build();
    }

    @Operation(summary = "view profile of user",
            description = "API to view profile of user")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_PROFILE')")
    public ApiResponse<CreateUserResponse> getUserDetail(@PathVariable Long userId) {
        return ApiResponse.<CreateUserResponse>builder()
                .code(200)
                .message("success")
                .result(userService.userDetail(userId))
                .build();
    }

    @Operation(summary = "update role user",
            description = "API update role of user")
    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('USER_ROLE_UPDATE')")
    public ApiResponse<CreateUserResponse> updateRole(@RequestBody UserRoleRequest userRoleRequest, @PathVariable(name = "id") Long id) {
        return ApiResponse.<CreateUserResponse>builder()
                .code(200)
                .message("success")
                .result(userService.updateRoleForUser(id, userRoleRequest))
                .build();
    }

    @Operation(summary = "add more role user",
            description = "API to add role of user")
    @PostMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('USER_ROLE_ADD')")
    public ApiResponse<CreateUserResponse> addRole(@RequestBody UserRoleRequest userRoleRequest, @PathVariable(name = "id") Long id) {
        return ApiResponse.<CreateUserResponse>builder()
                .code(200)
                .message("success")
                .result(userService.addRoleUser(id, userRoleRequest))
                .build();
    }
}
