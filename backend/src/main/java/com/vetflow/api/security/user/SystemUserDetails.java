package com.vetflow.api.security.user;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vetflow.api.infrastructure.persistence.entity.SystemUserEntity;

/**
 * Spring Security bridge around {@link SystemUserEntity}.
 */
public class SystemUserDetails implements UserDetails {

  private final Long id;
  private final String username;
  private final String password;
  private final boolean active;
  private final List<GrantedAuthority> authorities;

  private SystemUserDetails(Long id, String username, String password, boolean active, List<GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.active = active;
    this.authorities = authorities;
  }

  public static SystemUserDetails fromEntity(SystemUserEntity entity) {
    String role = entity.getRole() == null ? "USER" : entity.getRole().toUpperCase(Locale.ROOT);
    if (role.isBlank()) {
      role = "USER";
    }
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
    return new SystemUserDetails(entity.getId(), entity.getUsername(), entity.getPasswordHash(), entity.isActive(), authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return active;
  }

  @Override
  public boolean isAccountNonLocked() {
    // Locking is not modeled yet, so every stored account is considered unlocked.
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return password != null && !password.isBlank();
  }

  @Override
  public boolean isEnabled() {
    return active && !authorities.isEmpty();
  }

  public Long getId() {
    return id;
  }

  public List<String> authoritiesAsRoles() {
    return authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .map(auth -> auth.replace("ROLE_", ""))
        .toList();
  }
}
