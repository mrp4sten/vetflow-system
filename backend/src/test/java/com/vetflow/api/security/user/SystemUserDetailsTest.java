package com.vetflow.api.security.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.vetflow.api.infrastructure.persistence.entity.SystemUserEntity;

class SystemUserDetailsTest {

  @Test
  void fromEntityMapsRoleAuthoritiesAndStatuses() {
    SystemUserEntity entity = new SystemUserEntity();
    ReflectionTestUtils.setField(entity, "id", 77L);
    entity.setUsername("dr.vet");
    entity.setPasswordHash("$2a$hash");
    entity.setRole("vet");
    entity.setActive(true);

    SystemUserDetails details = SystemUserDetails.fromEntity(entity);

    assertThat(details.getId()).isEqualTo(77L);
    assertThat(details.getUsername()).isEqualTo("dr.vet");
    assertThat(details.authoritiesAsRoles()).containsExactly("VET");
    assertThat(details.isAccountNonExpired()).isTrue();
    assertThat(details.isAccountNonLocked()).isTrue();
    assertThat(details.isCredentialsNonExpired()).isTrue();
    assertThat(details.isEnabled()).isTrue();
  }

  @Test
  void inactiveUserWithBlankRoleAndPasswordReflectsDisabledState() {
    SystemUserEntity entity = new SystemUserEntity();
    entity.setUsername("support");
    entity.setPasswordHash("   ");
    entity.setRole("  ");
    entity.setActive(false);

    SystemUserDetails details = SystemUserDetails.fromEntity(entity);

    assertThat(details.authoritiesAsRoles()).containsExactly("USER");
    assertThat(details.isAccountNonExpired()).isFalse();
    assertThat(details.isAccountNonLocked()).isTrue();
    assertThat(details.isCredentialsNonExpired()).isFalse();
    assertThat(details.isEnabled()).isFalse();
  }
}
