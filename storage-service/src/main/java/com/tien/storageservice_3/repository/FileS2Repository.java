package com.tien.storageservice_3.repository;

import com.tien.storageservice_3.entity.FileS2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FileS2Repository extends JpaRepository<FileS2, Long> {
    Optional<FileS2> findByPublicId(String publicId);
    List<FileS2> findByCreatedBy(String createdBy);
    Page<FileS2> findAll(Pageable pageable);

    @Query("""
            SELECT p FROM FileS2 p WHERE
            (:fileName IS NULL OR LOWER(p.fileName) LIKE LOWER(CONCAT('%', :fileName, '%'))) AND
            (:typeOfFile IS NULL OR p.type = :typeOfFile) AND
            (:createDate IS NULL OR p.createdDate = :createDate) AND
            (:modifyDate IS NULL OR p.modifiedDate = :modifyDate) AND
            (:owner IS NULL OR p.createdBy = :owner)
            """)
    Page<FileS2> search(
            @Param("fileName") String fileName,
            @Param("typeOfFile") String typeOfFile,
            @Param("createDate") Instant createDate,
            @Param("modifyDate") Instant modifyDate,
            @Param("owner") String owner,
            Pageable pageable);
}
