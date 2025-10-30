package com.tien.common.client.storage;


import com.tien.common.config.openFeign.FeignClientConfig;
import com.tien.common.dto.request.*;
import com.tien.common.dto.response.ApiResponse;
import com.tien.common.dto.response.FileS2Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "storage-service",
        url = "${storage.service.url}",
        configuration = FeignClientConfig.class,
        fallbackFactory = StorageClientFallback.class)
public interface StorageServiceClient {
    @PostMapping(value = "/api/storage-service/cloudinary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<String> uploadFile(@ModelAttribute UploadFileRequest uploadFileRequest);

    @PostMapping(value = "/api/storage-service/cloudinary/multi-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<List<String>> uploadMultiFile(@ModelAttribute UploadMultiFileRequest uploadMultiFileRequest);

    @DeleteMapping(value = "/api/storage-service/cloudinary")
    ApiResponse<String> deleteFile(@RequestParam("publicId") String publicId);

    @PutMapping(value = "/api/storage-service/cloudinary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<String> updateFile(@ModelAttribute UpdateFileRequest updateFileRequest);

    @GetMapping(value = "/api/storage-service/cloudinary/file-publicId")
    ApiResponse<String> getFile(@SpringQueryMap GetImageRequest getImageRequest);

    @GetMapping(value = "/api/storage-service/cloudinary/profile")
    ApiResponse<String> getProfile(@SpringQueryMap GetProfileRequest getProfileRequest);

    @GetMapping(value = "/api/storage-service/cloudinary/filter")
    ApiResponse<Page<FileS2Response>> filter(@SpringQueryMap FileFilterRequest filterRequest);
}
