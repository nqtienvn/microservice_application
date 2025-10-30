package com.tien.iam_service2_keycloak.entity;

import com.tien.common.entity.Auditor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Role extends Auditor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true)
    String name;
    String description;
    @Enumerated(EnumType.STRING)
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Permission> permissions;
}
