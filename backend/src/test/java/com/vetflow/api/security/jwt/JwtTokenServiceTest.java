package com.vetflow.api.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.vetflow.api.infrastructure.persistence.entity.SystemUserEntity;
import com.vetflow.api.security.config.SecurityProperties;
import com.vetflow.api.security.user.SystemUserDetails;

class JwtTokenServiceTest {

  @Test
  void generateTokenIncludesUsernameRolesAndExpiry() {
    JwtTokenService service = new JwtTokenService(securityProperties(Duration.ofMinutes(5)));
    SystemUserDetails user = userDetails("alice", "ADMIN");

    String token = service.generateToken(user);

    assertThat(service.isTokenValid(token)).isTrue();
    assertThat(service.isTokenValid(token, user)).isTrue();
    assertThat(service.extractUsername(token)).isEqualTo("alice");
    assertThat(service.extractRoles(token)).containsExactly("ADMIN");
    assertThat(service.extractExpiry(token)).isAfter(Instant.now());
    assertThat(service.expiresInSeconds()).isEqualTo(Duration.ofMinutes(5).getSeconds());
  }

  @Test
  void invalidWhenCorruptedExpiredOrDifferentUser() {
    JwtTokenService service = new JwtTokenService(securityProperties(Duration.ofSeconds(-5)));
    SystemUserDetails owner = userDetails("owner", "USER");

    String expiredToken = service.generateToken(owner);
    assertThat(service.isTokenValid(expiredToken)).isFalse();

    JwtTokenService validService = new JwtTokenService(securityProperties(Duration.ofMinutes(10)));
    String token = validService.generateToken(owner);
    SystemUserDetails other = userDetails("other", "USER");

    assertThat(validService.isTokenValid(token, other)).isFalse();
    assertThat(validService.isTokenValid(token + "tampered")).isFalse();
  }

  private static SecurityProperties securityProperties(Duration expiration) {
    SecurityProperties properties = new SecurityProperties();
    properties.getJwt().setSecret("0123456789abcdefghijklmnopqrstuvwxyz0123456789");
    properties.getJwt().setExpiration(expiration);
    return properties;
  }

  private static SystemUserDetails userDetails(String username, String role) {
    SystemUserEntity entity = new SystemUserEntity();
    ReflectionTestUtils.setField(entity, "id", 1L);
    entity.setUsername(username);
    entity.setPasswordHash("$2a$test");
    entity.setRole(role);
    entity.setActive(true);
    return SystemUserDetails.fromEntity(entity);
  }
}
