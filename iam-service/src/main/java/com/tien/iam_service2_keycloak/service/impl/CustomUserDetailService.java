package com.tien.iam_service2_keycloak.service.impl;


import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.iam_service2_keycloak.entity.CustomUserDetails;
import com.tien.iam_service2_keycloak.entity.User;
import com.tien.iam_service2_keycloak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    //hàm thể hiện là dùng thằng UserDetailsService để lấy từ db và trả về thăng UserDetail đó
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return new CustomUserDetails(user);
    }
}
