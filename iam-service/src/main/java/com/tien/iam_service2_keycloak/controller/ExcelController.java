package com.tien.iam_service2_keycloak.controller;

import com.tien.iam_service2_keycloak.dto.response.ImportErrorResponse;
import com.tien.iam_service2_keycloak.entity.User;
import com.tien.iam_service2_keycloak.service.UserService;
import com.tien.iam_service2_keycloak.service.impl.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/iam-service/excel")
@RequiredArgsConstructor
public class ExcelController {
    private final ExcelService excelService;
    private final UserService userService;

    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportToExcel(@RequestParam(name = "firstName", required = false) String firstName, @RequestParam(name = "lastName", required = false) String lastName) throws IOException {
        List<User> users = userService.filter(firstName, lastName);
        byte[] excelData = excelService.exportUsersToExcel(users);
        ByteArrayResource resource = new ByteArrayResource(excelData);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users_iam_service.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(excelData.length)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))//đe khi in ra trinh duyet hieu do la file excel
                .body(resource);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty!");
            }
            String contentType = file.getContentType();
            if (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                return ResponseEntity.badRequest().body("Only .xlsx files are allowed!");
            }
            List<ImportErrorResponse> errors = excelService.importUsersFromExcel(file);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }
            return ResponseEntity.ok("Import thành công");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error importing file: " + e.getMessage());
        }
    }
}
