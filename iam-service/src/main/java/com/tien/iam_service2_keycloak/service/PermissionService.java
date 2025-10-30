package com.tien.iam_service2_keycloak.service;


import com.tien.iam_service2_keycloak.dto.request.AddPermissionForRoleRequest;
import com.tien.iam_service2_keycloak.dto.request.PermissionRequest;
import com.tien.iam_service2_keycloak.dto.response.PermissionResponse;
import com.tien.iam_service2_keycloak.dto.response.RoleResponse;
import org.springframework.data.domain.Page;


public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest permissionRequest);

    Page<PermissionResponse> getAllPermission(int page, int size);

    void deletePermission(Long id);

    RoleResponse addPermissionsForRole(Long id, AddPermissionForRoleRequest addPermissionForRoleRequest);

    RoleResponse updatePermissionForRole(Long id, AddPermissionForRoleRequest addPermissionForRoleRequest);
}
