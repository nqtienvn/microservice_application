package com.tien.iam_service2_keycloak.controller;


import com.tien.common.dto.response.ApiResponse;
import com.tien.iam_service2_keycloak.dto.request.AddPermissionForRoleRequest;
import com.tien.iam_service2_keycloak.dto.request.RoleRequest;
import com.tien.iam_service2_keycloak.dto.response.RoleResponse;
import com.tien.iam_service2_keycloak.service.PermissionService;
import com.tien.iam_service2_keycloak.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/iam-service/roles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;
    private final PermissionService permissionService;

    @PostMapping()
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .code(200)
                .message("tạo role thành công")
                .result(roleService.createRole(roleRequest))
                .build();
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ApiResponse<Page<RoleResponse>> getAllRole(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "2") int size) {
        return ApiResponse.<Page<RoleResponse>>builder()
                .code(200)
                .message("get all role thành công")
                .result(roleService.listRole(page, size))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("xóa role thành công")
                .build();
    }

    @PutMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_UPDATE')")
    public ApiResponse<RoleResponse> updateRole(@RequestBody AddPermissionForRoleRequest addPermissionForRoleRequest, @PathVariable(name = "id") Long id) {
        return ApiResponse.<RoleResponse>builder()
                .code(200)
                .message("success")
                .result(permissionService.updatePermissionForRole(id, addPermissionForRoleRequest))
                .build();
    }

    @PostMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('ROLE_PERMISSION_ADD')")
    public ApiResponse<RoleResponse> addRole(@RequestBody AddPermissionForRoleRequest addPermissionForRoleRequest, @PathVariable(name = "id") Long id) {
        return ApiResponse.<RoleResponse>builder()
                .code(200)
                .message("success")
                .result(permissionService.addPermissionsForRole(id, addPermissionForRoleRequest))
                .build();
    }
}
