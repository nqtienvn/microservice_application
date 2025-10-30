package com.tien.iam_service2_keycloak.repository;

import com.tien.common.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, String> {
}
