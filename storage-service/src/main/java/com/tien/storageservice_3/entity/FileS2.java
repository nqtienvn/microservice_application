package com.tien.storageservice_3.entity;

import com.tien.common.entity.Auditor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileS2 extends Auditor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String type;
    @Column(unique = true)
    private String url;
    @Column(unique = true)
    private String publicId;
}
