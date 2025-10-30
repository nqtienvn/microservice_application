package com.tien.iam_service2_keycloak.service.impl;

import com.tien.iam_service2_keycloak.dto.request.CreateUserRequest;
import com.tien.iam_service2_keycloak.dto.response.ImportErrorResponse;
import com.tien.iam_service2_keycloak.entity.User;
import com.tien.iam_service2_keycloak.repository.UserRepository;
import com.tien.iam_service2_keycloak.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EXCEL_SERVICE_ANH_TIEN")
public class ExcelService {
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final PasswordEncoder passwordEncoder;

    //đây là xuất file ra toàn bộ thông tin
    public byte[] exportUsersToExcel(List<User> users) throws IOException {
        //tạo một workbook đại diện cho một file Excel
        Workbook workbook = new XSSFWorkbook(); //cai nay la tao file.xlsx cho excel, dung cho excel tu 2007 den hien tai
        //tao mot sheat moi trong work book,(day chinh la buoc ma tao mot bang tinh trong file excel)
        Sheet sheet = workbook.createSheet("Users");
        //tao tieu de export ta la SOLID va BLUE
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontName("Arial");
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        //dinh nghia cac cot thoi, chua add vao cell
        String[] columns = {"ID", "Username", "Email", "FirstName", "LastName", "Avatar Url"};
        //tao cac cell cho header
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setWrapText(true); //tu dong xuong dong trong cell khi noi dung qua dai

        int rowNumber = 1; //bat dau tu dong 1 dien cac du lieu tu data base vao file
        for (User user : users) {
            Row row = sheet.createRow(rowNumber++);
            // Tạo cell cho từng thuộc tính
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(user.getId());
            cell0.setCellStyle(dataStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(user.getUsername());
            cell1.setCellStyle(dataStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(user.getEmail());
            cell2.setCellStyle(dataStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(user.getFirstName());
            cell3.setCellStyle(dataStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(user.getLastName());
            cell4.setCellStyle(dataStyle);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(user.getAvatarUrl());
            cell5.setCellStyle(dataStyle);
        }
        // tu dong dieu chinh do rong cho sheet
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        // chuyen work book thanh mang byte va tra ve
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public List<ImportErrorResponse> importUsersFromExcel(MultipartFile file) throws IOException {
        List<ImportErrorResponse> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); //lay trang sheet dau tien
            int numberOfRows = sheet.getLastRowNum();

            for (int i = 1; i <= numberOfRows; i++) { // Bỏ dòng header, bắt đầu từ index 1
                Row row = sheet.getRow(i);
                if (isRowEmpty(row)) {
                    log.debug("Empty is row: {}", i);
                    continue;
                }
                List<String> rowErrors = new ArrayList<>();
                String username = getStringCellValue(row.getCell(1));
                String email = getStringCellValue(row.getCell(2));
                String firstName = getStringCellValue(row.getCell(3));
                String lastName = getStringCellValue(row.getCell(4));
                String avatar_url = getStringCellValue(row.getCell(5));


                if (username == null || username.trim().isEmpty()) {
                    rowErrors.add("Username is required");
                } else if (userRepository.existsByUsername(username)) {
                    rowErrors.add("Username already exists in system");
                }
                if (email == null || email.trim().isEmpty()) {
                    rowErrors.add("Email is required");
                } else if (userRepository.existsByEmail(email)) {
                    rowErrors.add("Email already exists in system");
                }
                if (firstName == null || firstName.trim().isEmpty()) {
                    rowErrors.add("First Name is required");
                }
                if (lastName == null || lastName.trim().isEmpty()) {
                    rowErrors.add("Last Name is required");
                }
                if (!rowErrors.isEmpty()) {
                    errors.add(new ImportErrorResponse(i + 1, "Row", String.join("; ", rowErrors)));
                    continue;
                }
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setAvatarUrl(avatar_url);
                user.setPass(passwordEncoder.encode("123"));
                user.setEnabled(true);
                user.setDeleted(false);
                userRepository.save(user);
                CreateUserRequest createUserRequest = new CreateUserRequest();
                createUserRequest.setUsername(username);
                createUserRequest.setEmail(email);
                createUserRequest.setFirstName(firstName);
                createUserRequest.setLastName(lastName);
                createUserRequest.setPass("123");
                keycloakService.createUser(createUserRequest);
            }
        }
        return errors;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getStringCellValue(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }
//bây giờ ta cần xuất theo file theo điều kiện, như là tim được list user nào đó rồi xuất ra
    //nói chung là giờ chỉ cần timf được một list đã filter rồi xuất ra là được
}
