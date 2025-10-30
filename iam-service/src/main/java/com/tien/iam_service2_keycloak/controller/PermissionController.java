package com.tien.iam_service2_keycloak.controller;


import com.tien.common.dto.response.ApiResponse;
import com.tien.iam_service2_keycloak.dto.request.PermissionRequest;
import com.tien.iam_service2_keycloak.dto.response.PermissionResponse;
import com.tien.iam_service2_keycloak.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping()
    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest permissionRequest) {
        return ApiResponse.<PermissionResponse>builder()
                .code(200)
                .message("created permission")
                .result(permissionService.createPermission(permissionRequest))
                .build();
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ApiResponse<Page<PermissionResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "2") int size) {
        return ApiResponse.<Page<PermissionResponse>>builder()
                .code(200)
                .message("get all permissions")
                .result(permissionService.getAllPermission(page, size))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Xóa thành công")
                .build();
    }
}
