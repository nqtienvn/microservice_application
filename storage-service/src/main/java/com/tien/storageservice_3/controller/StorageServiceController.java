package com.tien.storageservice_3.controller;

import com.tien.common.dto.request.*;
import com.tien.common.dto.response.ApiResponse;
import com.tien.common.dto.response.FileS2Response;
import com.tien.storageservice_3.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cloudinary")
@RequiredArgsConstructor
@Tag(name = "Storage Controller")
public class StorageServiceController {
    private final CloudinaryService cloudinaryService;

    @Operation(summary = "upload a File",
            description = "upload a file and manage by cloudinary")
    @PostMapping()
    public ApiResponse<String> uploadFile(@ModelAttribute UploadFileRequest uploadFileRequest) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("upload file successfully")
                .result(cloudinaryService.uploadFile(uploadFileRequest))
                .build();
    }

    @Operation(summary = "upload multi File",
            description = "upload multi file and manage by cloudinary")
    @PostMapping("/multi-file")
    public ApiResponse<List<String>> uploadMultiFile(@ModelAttribute UploadMultiFileRequest uploadMultiFileRequest) {
        return ApiResponse.<List<String>>builder()
                .message("upload multi file successfully")
                .code(200)
                .result(cloudinaryService.uploadMultiFile(uploadMultiFileRequest))
                .build();
    }

    @Operation(summary = "delete a File",
            description = "delete a file in cloudinary and my database")
    @DeleteMapping()
    public ApiResponse<String> deleteFile(@RequestParam String publicId) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("delete file successfully")
                .result(cloudinaryService.deleteFile(publicId))
                .build();
    }

    @Operation(summary = "update a File",
            description = "update a file and manage by cloudinary")
    @PutMapping()
    public ApiResponse<String> updateFile(@ModelAttribute UpdateFileRequest updateFileRequest) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("update file successfully")
                .result(cloudinaryService.updateFile(updateFileRequest))
                .build();
    }

    @Operation(summary = "get a File",
            description = "get a file and manage by cloudinary with radio or width, height")
    @GetMapping("/file-publicId")
    public ApiResponse<String> getFile(@ModelAttribute GetImageRequest getImageRequest) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("get file successfully")
                .result(cloudinaryService.getFileByPublicId(getImageRequest))
                .build();
    }

    @Operation(summary = "get my Profile",
            description = "get my profile with ratio, width, height")
    @GetMapping(value = "/profile")
    ApiResponse<String> getProfile(@ModelAttribute GetProfileRequest getProfileRequest) {
        return ApiResponse.<String>builder()
                .message("get file success")
                .code(200)
                .result(cloudinaryService.getProfileImage(getProfileRequest))
                .build();
    }

    @Operation(summary = "filter File",
            description = "filter file with filename, type, create date, modify date, owner")
    @GetMapping("/filter")
    public ApiResponse<Page<FileS2Response>> filter(@ModelAttribute FileFilterRequest filterRequest) {
        return ApiResponse.<Page<FileS2Response>>builder()
                .code(200)
                .message("filter file successfully")
                .result(cloudinaryService.search(filterRequest))
                .build();
    }
}
