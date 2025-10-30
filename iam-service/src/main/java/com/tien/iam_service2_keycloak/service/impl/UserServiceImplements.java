package com.tien.iam_service2_keycloak.service.impl;

import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.iam_service2_keycloak.dto.request.CreateUserRequest;
import com.tien.iam_service2_keycloak.dto.request.UpdateRequest;
import com.tien.iam_service2_keycloak.dto.request.UserRoleRequest;
import com.tien.iam_service2_keycloak.dto.response.CreateUserResponse;
import com.tien.iam_service2_keycloak.dto.response.UserInformResponse;
import com.tien.iam_service2_keycloak.entity.Role;
import com.tien.iam_service2_keycloak.entity.User;
import com.tien.iam_service2_keycloak.mapper.UserMapper;
import com.tien.iam_service2_keycloak.repository.RoleRepository;
import com.tien.iam_service2_keycloak.repository.UserRepository;
import com.tien.iam_service2_keycloak.role_permission_enum.Role_System;
import com.tien.iam_service2_keycloak.service.KeycloakService;
import com.tien.iam_service2_keycloak.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER_SERVICE_IMPLEMENTS")
public class UserServiceImplements implements UserService {
    private final KeycloakService keycloakService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${iam.use-keycloak:false}")
    private boolean useKeycloak;

    @Override
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        if (useKeycloak) {
            String keycloakUserId = keycloakService.createUser(createUserRequest);
            log.info("keycloakUserId:{}", keycloakUserId);
            User user = userMapper.toUser(createUserRequest);
            Set<String> roleName = createUserRequest.getRoleName();
            Set<Role> roles = new HashSet<>();
            if (roleName == null) {
                roles.add(roleRepository.findByName(Role_System.USER.toString()).orElseThrow());
            } else {
                roleName.forEach(role -> roles.add(roleRepository.findByName(role).orElseThrow()));
            }
            user.setRoles(roles);
            user.setKeycloakUserId(keycloakUserId);
            user.setDeleted(false);
            user.setEnabled(true);
            user.setPass(passwordEncoder.encode(createUserRequest.getPass()));
            return userMapper.toCreateUserResponse(userRepository.save(user));

        } else {
            User user = userMapper.toUser(createUserRequest);
            Set<String> roleName = createUserRequest.getRoleName();
            Set<Role> roles = new HashSet<>();
            if (roleName == null) {
                roles.add(roleRepository.findByName(Role_System.USER.toString()).orElseThrow());
            } else {
                roleName.forEach(role -> roles.add(roleRepository.findByName(role).orElseThrow()));
            }
            user.setPass(passwordEncoder.encode(createUserRequest.getPass()));
            user.setRoles(roles);
            user.setDeleted(false);
            user.setEnabled(true);
            return userMapper.toCreateUserResponse(userRepository.save(user));
        }
    }

    @Override
    public CreateUserResponse updateUser(UpdateRequest updateRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (Boolean.TRUE.equals(user.getDeleted())) {
            throw new AppException(ErrorCode.ERROR_DELETE);
        }
        if (useKeycloak) {
            String keycloakUserId = user.getKeycloakUserId();
            keycloakService.updateUser(updateRequest, keycloakUserId);
            user.setEmail(updateRequest.getEmail());
            user.setFirstName(updateRequest.getFirstName());
            user.setLastName(updateRequest.getLastName());
            User userUpdate = userRepository.save(user);
            log.info("Inform before keycloak update: {}", updateRequest);
            log.info("Inform after keycloak update: {}", user);
            return userMapper.toCreateUserResponse(userUpdate);
        } else {
            user.setEmail(updateRequest.getEmail());
            user.setFirstName(updateRequest.getFirstName());
            user.setLastName(updateRequest.getLastName());
            User userUpdate = userRepository.save(user);
            log.info("Inform before update: {}", updateRequest);
            log.info("Inform after update: {}", user);
            return userMapper.toCreateUserResponse(userUpdate);
        }
    }

    @Override
    public Boolean deleteUser(Long userId) {
        if (useKeycloak) {
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            String keycloakUserId = user.getKeycloakUserId();
            keycloakService.softDelete(keycloakUserId); //da enable roi nha
            user.setDeleted(true);
            user.setEnabled(false);
            userRepository.save(user);
            log.info("Inform before delete: {}", user.getUsername());
            return user.getDeleted();
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setDeleted(true);
        user.setEnabled(false);
        userRepository.save(user);
        log.info("Inform before delete: {}", user.getUsername());
        return user.getDeleted();
    }

    @Override
    public Boolean blockUser(Long userId) {
        if (useKeycloak) {
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            String keycloakUserId = user.getKeycloakUserId();
            keycloakService.blockUser(keycloakUserId);
            user.setEnabled(false);
            userRepository.save(user);
            return user.getEnabled();
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setEnabled(false);
        userRepository.save(user);
        return user.getEnabled();
    }

    @Override
    public Boolean unBlockUser(Long userId) {
        if (useKeycloak) {
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            String keycloakUserId = user.getKeycloakUserId();
            if (Boolean.TRUE.equals(user.getDeleted())) {
                return user.getEnabled(); //luon false
            }
            keycloakService.unblockUser(keycloakUserId);
            user.setEnabled(true);
            userRepository.save(user);
            return user.getEnabled();
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (Boolean.TRUE.equals(user.getDeleted())) {
            return user.getEnabled(); //luon false
        }
        user.setEnabled(true);
        userRepository.save(user);
        return user.getEnabled();
    }

    @Override
    public Page<UserInformResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageUser = userRepository.findAll(pageable);
        //tu page sang list de map
        List<UserInformResponse> dtoList = pageUser.getContent().stream().map(user -> new UserInformResponse(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getEnabled(), user.getDeleted(), user.getCreatedBy(), user.getCreatedDate(), user.getModifiedBy(), user.getModifiedDate())).collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, pageUser.getTotalElements());
    }

    @Override
    public CreateUserResponse userDetail(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getDeleted() || !user.getEnabled()) {
            log.info("user đã xóa hoặc là disable rồi nên không thể get userdetail được: deleted : {}, enabled: {}", user.getDeleted(), user.getEnabled());
            return null;
        }
        return userMapper.toCreateUserResponse(user);
    }

    @Override
    public CreateUserResponse updateRoleForUser(Long id, UserRoleRequest userRoleRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Set<Role> newRoles = new HashSet<>();
        for (String roleName : userRoleRequest.getRoleName()) {
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new AppException(ErrorCode.ROLE_REPOSITORY_EMPTY));
            newRoles.add(role);
        }
        user.setRoles(newRoles);
        return userMapper.toCreateUserResponse(userRepository.save(user));
    }

    @Override
    public CreateUserResponse addRoleUser(Long id, UserRoleRequest userRoleRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        for (String roleName : userRoleRequest.getRoleName()) {
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new AppException(ErrorCode.ROLE_REPOSITORY_EMPTY));
            user.getRoles().add(role);
        }
        return userMapper.toCreateUserResponse(userRepository.save(user));
    }

    @Override
    public List<User> filter(String firstName, String lastName) {
        return userRepository.filter(firstName, lastName);
    }
}
