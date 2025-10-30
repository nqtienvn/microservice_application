package com.tien.storageservice_3.repository;

import com.tien.common.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, String> {
}
