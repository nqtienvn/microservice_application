package com.tien.storageservice_3.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.tien.common.dto.request.*;
import com.tien.common.dto.response.FileS2Response;
import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.storageservice_3.entity.FileS2;
import com.tien.storageservice_3.mapper.FileS2Mapper;
import com.tien.storageservice_3.repository.FileS2Repository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    private final FileS2Repository fileS2Repository;
    private final Cloudinary cloudinary;
    private final FileS2Mapper fileS2Mapper;

    public String uploadFile(UploadFileRequest uploadFileRequest) {
        String originalFilename = uploadFileRequest.getFile().getOriginalFilename();
        if (originalFilename == null) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
        String publicValue = generatePublicValue(originalFilename);
        String extension = getFileExtension(originalFilename);
        File fileUpload = convert(uploadFileRequest.getFile(), publicValue, extension);

        try {
            cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", publicValue, "resource_type", "image", "folder", uploadFileRequest.getTypeOfFile()));
            FileS2 fileS2 = new FileS2();
            fileS2.setFileName(originalFilename);
            fileS2.setUrl(cloudinary.url().resourceType("image").generate(uploadFileRequest.getTypeOfFile() + "/" + publicValue + "." + extension));
            fileS2.setPublicId(uploadFileRequest.getTypeOfFile() + "/" + publicValue);
            fileS2.setType(uploadFileRequest.getTypeOfFile());
            fileS2Repository.save(fileS2);
            return cloudinary
                    .url()
                    .resourceType("image")
                    .generate(uploadFileRequest.getTypeOfFile() + "/" + publicValue + "." + extension);
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        } finally {
            cleanDisk(fileUpload);
        }
    }

    public List<String> uploadMultiFile(UploadMultiFileRequest uploadMultiFileRequest) {
        try {
            List<String> fileUrls = new ArrayList<>();
            for (MultipartFile file : uploadMultiFileRequest.getFiles()) {
                String url = uploadFile(UploadFileRequest.builder()
                        .file(file)
                        .typeOfFile(uploadMultiFileRequest.getTypeOfFile())
                        .build());
                fileUrls.add(url);
            }
            return fileUrls;
        } catch (Exception e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
    }

    public String update(UpdateFileRequest updateFileRequest) {
        String originalFilename = updateFileRequest.getFile().getOriginalFilename();
        if (originalFilename == null) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
        String publicValue = generatePublicValue(originalFilename);//
        String extension = getFileExtension(originalFilename);
        File fileUpload = convert(updateFileRequest.getFile(), publicValue, extension);
        FileS2 fileS2 = fileS2Repository.findByPublicId(updateFileRequest.getOldPublicId()).orElseThrow(() -> new AppException(ErrorCode.ERROR_UPLOAD_FILE));
        String type = updateFileRequest.getTypeOfFile() == null ? fileS2.getType() : updateFileRequest.getTypeOfFile();
        try {
            cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", publicValue, "resource_type", "image", "folder", type));
            fileS2.setType(type);
            fileS2.setFileName(originalFilename);
            fileS2.setUrl(cloudinary
                    .url()
                    .resourceType("image")
                    .generate(type + "/" + publicValue + "." + extension));
            fileS2.setPublicId(type + "/" + publicValue);
            fileS2Repository.save(fileS2);
            return cloudinary
                    .url()
                    .resourceType("image").generate(type + "/" + publicValue + "." + extension);
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        } finally {
            cleanDisk(fileUpload);
        }
    }

    public String deleteFile(String publicId) {
        try {
            FileS2 fileS2 = fileS2Repository.findByPublicId(publicId).orElseThrow(() -> new AppException(ErrorCode.ERROR_PUBLIC_ID));
            fileS2Repository.delete(fileS2);
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return result.get("result").toString();
        } catch (Exception e) {
            throw new AppException(ErrorCode.ERROR_DELETE_FILE);
        }
    }

    @Transactional
    public String updateFile(UpdateFileRequest updateFileRequest) {
        try {
            log.debug(updateFileRequest.getOldPublicId());
            cloudinary.uploader().destroy(updateFileRequest.getOldPublicId(), ObjectUtils.emptyMap());
            return update(updateFileRequest);
        } catch (Exception e) {
            throw new AppException(ErrorCode.ERROR_DELETE_FILE);
        }

    }

    private File convert(MultipartFile multipartFile, String publicValue, String extension) {
        try {
            File tempFile = File.createTempFile(publicValue, "." + extension);
            try (InputStream in = multipartFile.getInputStream()) {
                Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return tempFile;
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
    }

    private void cleanDisk(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            log.error("Error deleting temp file: {}", file.getName(), e);
        }
    }

    private String generatePublicValue(String originalFilename) {
        String fileName = getBaseName(originalFilename);
        return UUID.randomUUID() + "_" + fileName;
    }

    private String getBaseName(String filename) {
        return filename.substring(0, filename.lastIndexOf("."));
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public String getTransformedImageUrl(GetImageRequest getImageRequest) {
        Transformation transformation = new Transformation()
                .width(getImageRequest.getWidth())
                .height(getImageRequest.getHeight())
                .crop(getImageRequest.getCropMode() != null ? getImageRequest.getCropMode() : "fill");

        return cloudinary
                .url()
                .transformation(transformation)
                .generate(getImageRequest.getPublicId());
    }

    public String getImageByRatio(GetImageRequest getImageRequest) {
        return cloudinary
                .url()
                .transformation(new Transformation()
                        .aspectRatio(getImageRequest.getRatio())
                        .crop("fill"))
                .generate(getImageRequest.getPublicId());
    }

    public Page<FileS2Response> search(FileFilterRequest filterRequest) {
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize());
        Page<FileS2> pageFile = fileS2Repository
                .search(filterRequest.getFileName(), filterRequest.getTypeOfFile(), filterRequest.getCreateDate(), filterRequest.getModifyDate(), filterRequest.getOwner(), pageable);
        List<FileS2Response> listFilter = pageFile
                .getContent()
                .stream()
                .map(fileS2Mapper::toFileS2Response)
                .toList();
        return new PageImpl<>(listFilter, pageable, pageFile.getTotalElements());
    }

    public String getFileByPublicId(GetImageRequest getImageRequest) {
        if (getImageRequest.getRatio() == null) {
            return getTransformedImageUrl(getImageRequest);

        } else return getImageByRatio(getImageRequest);
    }

    public String getProfileImage(GetProfileRequest getProfileRequest) {
        String username = getCurrentUsername();
        List<FileS2> files = fileS2Repository.findByCreatedBy(username);

        List<String> publicIds = files.stream().map(FileS2::getPublicId).toList();
        String imageUrl;
        if (getProfileRequest.getRatio() == null) {
            imageUrl = getTransformedImageUrl(GetImageRequest.builder()
                    .cropMode(getProfileRequest.getCropMode())
                    .publicId(publicIds.getFirst())
                    .height(getProfileRequest.getHeight())
                    .width(getProfileRequest.getWidth()).build());
        } else {
            imageUrl = getImageByRatio(GetImageRequest.builder()
                    .ratio(getProfileRequest.getRatio())
                    .publicId(publicIds.getFirst())
                    .build());
        }
        return imageUrl;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwtToken) {
            return jwtToken.getClaimAsString("preferred_username");
        } else {
            return authentication.getName();
        }
    }
}

