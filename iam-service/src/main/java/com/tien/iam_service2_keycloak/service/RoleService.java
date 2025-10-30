package com.tien.iam_service2_keycloak.service;


import com.tien.iam_service2_keycloak.dto.request.RoleRequest;
import com.tien.iam_service2_keycloak.dto.response.RoleResponse;
import org.springframework.data.domain.Page;


public interface RoleService {
    RoleResponse createRole(RoleRequest roleRequest);

    Page<RoleResponse> listRole(int page, int size);

    void deleteRole(Long id);
}
