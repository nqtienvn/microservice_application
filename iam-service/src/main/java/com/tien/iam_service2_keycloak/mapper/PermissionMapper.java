package com.tien.iam_service2_keycloak.mapper;


import com.tien.iam_service2_keycloak.dto.request.PermissionRequest;
import com.tien.iam_service2_keycloak.dto.response.PermissionResponse;
import com.tien.iam_service2_keycloak.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
