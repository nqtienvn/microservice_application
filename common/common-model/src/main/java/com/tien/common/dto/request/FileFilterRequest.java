package com.tien.common.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileFilterRequest {
    String fileName;
    String typeOfFile;
    Instant createDate;
    Instant modifyDate;
    String owner;
    int page = 0;
    int size = 5;
}
