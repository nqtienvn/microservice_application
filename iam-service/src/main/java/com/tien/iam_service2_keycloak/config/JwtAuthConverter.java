package com.tien.iam_service2_keycloak.config;


import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.iam_service2_keycloak.entity.Permission;
import com.tien.iam_service2_keycloak.entity.Role;
import com.tien.iam_service2_keycloak.entity.User;
import com.tien.iam_service2_keycloak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
//đưa ào thàng jwt và nhận về thằng jwtAuthenticationToken extend abtractAuthenticationToken
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final UserRepository userRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return new JwtAuthenticationToken(jwt, getAuthorities(user), username);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            if (role.getPermissions() != null) {
                for (Permission perm : role.getPermissions()) {
                    authorities.add(new SimpleGrantedAuthority(perm.getName()));
                }
            }
        }
        return authorities;
    }
}
