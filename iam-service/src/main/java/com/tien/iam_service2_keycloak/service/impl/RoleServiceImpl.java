package com.tien.iam_service2_keycloak.service.impl;


import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.iam_service2_keycloak.dto.request.RoleRequest;
import com.tien.iam_service2_keycloak.dto.response.RoleResponse;
import com.tien.iam_service2_keycloak.entity.Permission;
import com.tien.iam_service2_keycloak.entity.Role;
import com.tien.iam_service2_keycloak.entity.User;
import com.tien.iam_service2_keycloak.mapper.RoleMapper;
import com.tien.iam_service2_keycloak.repository.PermissionRepository;
import com.tien.iam_service2_keycloak.repository.RoleRepository;
import com.tien.iam_service2_keycloak.repository.UserRepository;
import com.tien.iam_service2_keycloak.role_permission_enum.Role_System;
import com.tien.iam_service2_keycloak.service.RoleService;
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
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {
        Role role = roleMapper.toRole(roleRequest);
        Set<String> permissionName = roleRequest.getPermissionsName();
        if (permissionName == null) {
            throw new AppException(ErrorCode.PERMISSION_REPOSITORY_EMPTY);
        }
        Set<Permission> permissions = new HashSet<>();
        permissionName.forEach(permission -> permissions.add(permissionRepository.findByName(permission).orElseThrow()));
        role.setPermissions(permissions);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    public Page<RoleResponse> listRole(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> pageRole = roleRepository.findAll(pageable);
        return pageRole.map(roleMapper::toRoleResponse);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_REPOSITORY_EMPTY));
        // Gỡ khỏi users
        for (User user : userRepository.findAll()) {
            user.getRoles().remove(role);
            if (user.getRoles() == null) {
                Set<Role> roles = new HashSet<>();
                roles.add(roleRepository.findByName(Role_System.USER.toString()).orElseThrow());
                user.setRoles(roles);
                userRepository.save(user);
            }
        }
        role.getPermissions().clear();
        roleRepository.deleteById(id);
    }
}
