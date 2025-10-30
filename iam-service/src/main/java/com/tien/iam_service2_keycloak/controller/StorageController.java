package com.tien.iam_service2_keycloak.controller;

import com.tien.common.client.storage.StorageServiceClient;
import com.tien.common.dto.request.*;
import com.tien.common.dto.response.ApiResponse;
import com.tien.common.dto.response.FileS2Response;
import com.tien.iam_service2_keycloak.service.impl.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@Tag(name = "Storage Service Controller")
public class StorageController {
    private final StorageService storageService;
    private final StorageServiceClient storageServiceClient;
    @Operation(summary = "upload file", description = "upload file to cloudinary and manage in database")
    @PostMapping()
    @PreAuthorize("hasAuthority('USER_UPLOAD_AVATAR')")
    ApiResponse<String> uploadFile(@ModelAttribute UploadFileRequest uploadFileRequest) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("upload avatar for User successfully")
                .result(storageService.uploadAvatar(uploadFileRequest))
                .build();
    }

    @Operation(summary = "upload multi File", description = "upload multi file and manage by cloudinary")
    @PreAuthorize("hasAuthority('FILE_UPLOAD_MULTI')")
    @PostMapping(value = "/multi-file")
    ApiResponse<List<String>> uploadMultiFile(@ModelAttribute UploadMultiFileRequest uploadMultiFileRequest) {
        return storageServiceClient.uploadMultiFile(uploadMultiFileRequest);
    }

    @Operation(summary = "delete a File", description = "delete a file in cloudinary and my database")
    @PreAuthorize("hasAuthority('FILE_DELETE')")
    @DeleteMapping()
    ApiResponse<String> deleteFile(@RequestParam String publicId) {
        return storageServiceClient.deleteFile(publicId);
    }

    @Operation(summary = "update a File", description = "update a file and manage by cloudinary")
    @PutMapping()
    @PreAuthorize("hasAuthority('USER_UPDATE_AVATAR')")
    ApiResponse<String> updateFile(@ModelAttribute UpdateFileRequest updateFileRequest) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("upload avatar for User successfully")
                .result(storageService.updateAvatarFile(updateFileRequest))
                .build();
    }

    @PreAuthorize("hasAuthority('FILE_VIEW')")
    @Operation(summary = "get a File", description = "get a file and manage by cloudinary with radio or width, height")
    @GetMapping("/file-publicId")
    ApiResponse<String> getFile(@ModelAttribute GetImageRequest getImageRequest) {
        return storageServiceClient.getFile(getImageRequest);
    }

    @PreAuthorize("hasAuthority('AVATAR_VIEW')")
    @Operation(summary = "avatar File", description = "avatar File description")
    @GetMapping("/profile")
    ApiResponse<String> getMyProfile(@ModelAttribute GetProfileRequest getProfileRequest) {
        return storageServiceClient.getProfile(getProfileRequest);
    }

    @PreAuthorize("hasAuthority('FILE_FILTER')")
    @Operation(summary = "filter File", description = "filter file with filename, type, create date, modify date, owner")
    @GetMapping("/filter")
    ApiResponse<Page<FileS2Response>> filter(@ModelAttribute FileFilterRequest filterRequest) {
        return storageServiceClient.filter(filterRequest);
    }
}
