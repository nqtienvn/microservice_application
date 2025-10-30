package com.tien.iam_service2_keycloak.entity;

import com.tien.common.entity.Auditor;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User extends Auditor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    private String firstName;
    private String lastName;
    private String pass;
    private String keycloakUserId;
    @Column(nullable = false)
    private Boolean enabled;
    @Column(nullable = false)
    private Boolean deleted = false;
    @Enumerated(EnumType.STRING)
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Role> roles;
    private String avatarPublicId;
    private String avatarUrl;

}