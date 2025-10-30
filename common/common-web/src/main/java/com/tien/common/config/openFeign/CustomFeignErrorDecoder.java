package com.tien.common.config.openFeign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tien.common.dto.response.ApiResponse;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.body() == null) {
                return new ResponseStatusException(HttpStatus.valueOf(response.status()), "No response body from service B");
            }

            String body = Util.toString(response.body().asReader());
            ApiResponse apiResponse = objectMapper.readValue(body, ApiResponse.class);

            return new ResponseStatusException( //lấy về thằng Response
                    HttpStatus.valueOf(response.status()), apiResponse.getMessage());
        } catch (IOException e) {
            return new ResponseStatusException(HttpStatus.valueOf(response.status()), "Failed to decode error from service B: " + e.getMessage());
        }
    }
}
