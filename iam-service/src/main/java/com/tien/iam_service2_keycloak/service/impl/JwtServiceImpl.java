package com.tien.iam_service2_keycloak.service.impl;


import com.tien.common.exception.AppException;
import com.tien.common.exception.ErrorCode;
import com.tien.iam_service2_keycloak.config.JwtProperties;
import com.tien.iam_service2_keycloak.service.BaseRedisV2Service;
import com.tien.iam_service2_keycloak.service.JwtService;
import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Jwt_service")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtServiceImpl implements JwtService {
    JwtProperties jwtProperties;
    CustomUserDetailService customerUserDetailService;
    BaseRedisV2Service baseRedisV2Service;

    public Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .id(UUID.randomUUID().toString())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public boolean validateToken(String token, UserDetails useDetails) {
        String email = extractEmail(token);
        String id = extracId(token);
        Object accessToken = baseRedisV2Service.get(id);
        if (accessToken != null) {
            return false;
        }
        return (email.equals(useDetails.getUsername())) && !isTokenExpired(token);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public String refreshAcessToken(String refreshToken) {
        try {
            String id = extracId(refreshToken);
            String email = extractEmail(refreshToken);//doan nay la no da ket hop ca validate token roi
            log.info("id: " + id);
            log.info("id in redis: " + baseRedisV2Service.get("refreshToken"));
            if (!isTokenExpired(refreshToken) /*&& baseRedisV2Service.get("refreshToken") != null && baseRedisV2Service.get("refreshToken").toString().equals(id)*/) {
                log.info(generateAccessToken(customerUserDetailService.loadUserByUsername(email)));
                return generateAccessToken(customerUserDetailService.loadUserByUsername(email));
            }
            throw new AppException(ErrorCode.UNVERIFY_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new AppException(ErrorCode.UNVERIFY_TOKEN);
        }
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extracId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public String generateToken(
            Map<String, Object> extraClaims, //gắn thành phần theme vào trong JWT
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .id(UUID.randomUUID().toString())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }
}