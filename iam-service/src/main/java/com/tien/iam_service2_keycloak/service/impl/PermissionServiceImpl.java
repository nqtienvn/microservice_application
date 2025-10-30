package com.tien.iam_service2_keycloak.service.impl;


import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.iam_service2_keycloak.dto.request.AddPermissionForRoleRequest;
import com.tien.iam_service2_keycloak.dto.request.PermissionRequest;
import com.tien.iam_service2_keycloak.dto.response.PermissionResponse;
import com.tien.iam_service2_keycloak.dto.response.RoleResponse;
import com.tien.iam_service2_keycloak.entity.Permission;
import com.tien.iam_service2_keycloak.entity.Role;
import com.tien.iam_service2_keycloak.mapper.PermissionMapper;
import com.tien.iam_service2_keycloak.mapper.RoleMapper;
import com.tien.iam_service2_keycloak.repository.PermissionRepository;
import com.tien.iam_service2_keycloak.repository.RoleRepository;
import com.tien.iam_service2_keycloak.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Override
    public PermissionResponse createPermission(PermissionRequest permissionRequest) {
        Permission permission = permissionMapper.toPermission(permissionRequest);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    @Override
    public Page<PermissionResponse> getAllPermission(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Permission> pagePermission = permissionRepository.findAll(pageable);
        return pagePermission.map(permissionMapper::toPermissionResponse);
    }

    @Override
    public void deletePermission(Long id) {
        //tim role theo permission id, xong xoa sach nhung permission cua role co cai id do
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_REPOSITORY_EMPTY));
        for (Role role : roleRepository.findAll()) {
            role.getPermissions().remove(permission);
        }
        permissionRepository.deleteById(id);
    }

    @Override
    public RoleResponse addPermissionsForRole(Long id, AddPermissionForRoleRequest addPermissionForRoleRequest) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_REPOSITORY_EMPTY));

        for (String permissionName : addPermissionForRoleRequest.getPermissionsName()) {
            Permission permission = permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_REPOSITORY_EMPTY));
            role.getPermissions().add(permission);
        }
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse updatePermissionForRole(Long id, AddPermissionForRoleRequest addPermissionForRoleRequest) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_REPOSITORY_EMPTY));
        Set<Permission> newPermissions = new HashSet<>();
        for (String permissionName : addPermissionForRoleRequest.getPermissionsName()) {
            Permission permission = permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_REPOSITORY_EMPTY));
            newPermissions.add(permission);
        }
        role.setPermissions(newPermissions);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }
}
