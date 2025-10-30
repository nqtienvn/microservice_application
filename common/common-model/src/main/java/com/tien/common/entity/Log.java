package com.tien.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "inbound_request_log")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "http_code")
    private Integer httpCode;
    @Column(columnDefinition = "varchar(20)")
    private String method;
    @Column(name = "request_param", columnDefinition = "varchar(2000)")
    private String requestParam;
    @Column(name = "request_payload",columnDefinition = "LONGTEXT")
    private String requestPayload;
    @Column(name = "response_payload",columnDefinition = "LONGTEXT")
    private String responsePayload;
    @CreatedDate
    @Column(name = "sent_at")
    private Instant sentAt;
    @Column(columnDefinition = "TEXT")
    private String uri;
}
