package com.tien.iam_service2_keycloak.config;

import com.tien.common.entity.Log;
import com.tien.iam_service2_keycloak.repository.LogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {
    private final LogRepository logRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Bọc request và response
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        Log log = new Log();
        log.setMethod(request.getMethod());
        log.setUri(request.getRequestURI());
        log.setRequestParam(request.getQueryString());
        log.setSentAt(Instant.now());

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String requestBody = getRequestBody(requestWrapper);
            log.setRequestPayload(requestBody);

            String responseBody = getResponseBody(responseWrapper);
            log.setResponsePayload(responseBody);

            log.setHttpCode(responseWrapper.getStatus());

            logRepository.save(log);

            // copy sang client sau đó mới quay lại try để trả về thằng controller
            responseWrapper.copyBodyToResponse();
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper requestWrapper) {
        byte[] buf = requestWrapper.getContentAsByteArray(); //lấy về content dạng byte lưu trong cache
        if (buf.length == 0) return null;
        try {
            return new String(buf, requestWrapper.getCharacterEncoding()); //trả về encode kiểu String
        } catch (UnsupportedEncodingException e) {
            return "Unsupported Encoding";
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        byte[] buf = responseWrapper.getContentAsByteArray();
        if (buf.length == 0) return null;
        try {
            return new String(buf, responseWrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "Unsupported Encoding";
        }
    }
}
