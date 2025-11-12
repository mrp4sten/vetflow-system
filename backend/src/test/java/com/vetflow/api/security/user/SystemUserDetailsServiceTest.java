package com.vetflow.api.security.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.vetflow.api.infrastructure.persistence.entity.SystemUserEntity;
import com.vetflow.api.infrastructure.persistence.repository.SystemUserJpaRepository;

@ExtendWith(MockitoExtension.class)
class SystemUserDetailsServiceTest {

  @Mock
  private SystemUserJpaRepository repository;

  private SystemUserDetailsService service;

  @BeforeEach
  void setUp() {
    service = new SystemUserDetailsService(repository);
  }

  @Test
  void loadUserByUsernameReturnsPrincipalWhenFound() {
    SystemUserEntity entity = new SystemUserEntity();
    ReflectionTestUtils.setField(entity, "id", 9L);
    entity.setUsername("nurse");
    entity.setPasswordHash("$2a$test");
    entity.setRole("nurse");
    entity.setActive(true);
    when(repository.findByUsernameIgnoreCase("nurse")).thenReturn(Optional.of(entity));

    SystemUserDetails details = (SystemUserDetails) service.loadUserByUsername("nurse");

    assertThat(details.getId()).isEqualTo(9L);
    assertThat(details.getUsername()).isEqualTo("nurse");
    assertThat(details.authoritiesAsRoles()).containsExactly("NURSE");
  }

  @Test
  void loadUserByUsernameThrowsWhenMissing() {
    when(repository.findByUsernameIgnoreCase("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.loadUserByUsername("missing"))
        .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
        .hasMessageContaining("missing");
  }
}
