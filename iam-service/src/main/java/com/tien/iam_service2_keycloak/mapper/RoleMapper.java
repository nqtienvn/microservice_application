package com.tien.iam_service2_keycloak.mapper;


import com.tien.iam_service2_keycloak.dto.request.RoleRequest;
import com.tien.iam_service2_keycloak.dto.response.RoleResponse;
import com.tien.iam_service2_keycloak.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);
    RoleResponse toRoleResponse(Role role);
}
