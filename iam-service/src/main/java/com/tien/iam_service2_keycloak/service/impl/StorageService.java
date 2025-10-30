package com.tien.iam_service2_keycloak.service.impl;

import com.tien.common.client.storage.StorageServiceClient;
import com.tien.common.dto.request.UpdateFileRequest;
import com.tien.common.dto.request.UploadFileRequest;
import com.tien.common.dto.response.ApiResponse;
import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.iam_service2_keycloak.entity.User;
import com.tien.iam_service2_keycloak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final UserRepository userRepository;
    private final StorageServiceClient storageServiceClient;

    public String uploadAvatar(UploadFileRequest uploadFileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        ApiResponse<String> response = storageServiceClient.uploadFile(uploadFileRequest);
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setAvatarUrl(response.getResult());
        userRepository.save(user);
        return response.getResult();
    }

    public String updateAvatarFile(UpdateFileRequest updateFileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        ApiResponse<String> response = storageServiceClient.updateFile(updateFileRequest);
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setAvatarUrl(response.getResult());
        userRepository.save(user);
        return response.getResult();
    }
}
