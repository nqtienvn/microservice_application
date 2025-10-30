package com.tien.storageservice_3.mapper;

import com.tien.common.dto.response.FileS2Response;
import com.tien.storageservice_3.entity.FileS2;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileS2Mapper {
    FileS2Response toFileS2Response(FileS2 fileS2);
}
