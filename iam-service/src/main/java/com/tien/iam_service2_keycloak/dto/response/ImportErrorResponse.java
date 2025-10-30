package com.tien.iam_service2_keycloak.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportErrorResponse {
    private int rowIndex;
    private String field;
    private String message;
}
