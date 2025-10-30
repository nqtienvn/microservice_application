package com.tien.iam_service2_keycloak.repository;


import com.tien.iam_service2_keycloak.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    Page<Permission> findAll(Pageable pageable);
}
