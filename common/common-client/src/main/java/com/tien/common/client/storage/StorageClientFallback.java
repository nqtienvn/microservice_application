package com.tien.common.client.storage;

import com.tien.common.dto.request.*;
import com.tien.common.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageClientFallback implements FallbackFactory<StorageServiceClient> {

    @Override
    public StorageServiceClient create(Throwable cause) { //neu am create nem tjrow thi trả ve day
        return new FallbackWithFactory(cause); //trả về đối tượng fall back
    }

    @Slf4j
    static class FallbackWithFactory implements StorageServiceClient {
        private final Throwable cause;

        FallbackWithFactory(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public ApiResponse<String> uploadFile(UploadFileRequest uploadFileRequest) {
            return ApiResponse.<String>builder()
                    .code(180)
                    .message("fall back retry open feign upload File")
                    .build();
        }

        @Override
        public ApiResponse<List<String>> uploadMultiFile(UploadMultiFileRequest uploadMultiFileRequest) {
            return ApiResponse.<List<String>>builder()
                    .code(180)
                    .message("fall back retry open feign upload multi File")
                    .build();
        }

        @Override
        public ApiResponse<String> deleteFile(String publicId) {
            return ApiResponse.<String>builder()
                    .code(180)
                    .message("fall back retry open feign delete File")
                    .build();
        }

        @Override
        public ApiResponse<String> updateFile(UpdateFileRequest updateFileRequest) {
            return ApiResponse.<String>builder()
                    .code(180)
                    .message("fall back retry open feign update File")
                    .build();
        }

        @Override
        public ApiResponse<String> getFile(GetImageRequest getImageRequest) {
            return ApiResponse.<String>builder()
                    .code(180)
                    .message("fall back retry open feign get File")
                    .build();
        }

        @Override
        public ApiResponse<String> getProfile(GetProfileRequest getProfileRequest) {
            return ApiResponse.<String>builder()
                    .code(180)
                    .message("fall back retry open feign get profile File")
                    .build();
        }

        @Override
        public ApiResponse<Page<FileS2Response>> filter(FileFilterRequest filterRequest) {
            return ApiResponse.<Page<FileS2Response>>builder()
                    .code(180)
                    .message("fall back retry open feign filter File")
                    .build();
        }
    }
}
