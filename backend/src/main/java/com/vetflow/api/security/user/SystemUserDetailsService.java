package com.vetflow.api.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vetflow.api.infrastructure.persistence.repository.SystemUserJpaRepository;

/**
 * Loads VetFlow staff users from the database.
 */
@Service
public class SystemUserDetailsService implements UserDetailsService {

  private final SystemUserJpaRepository repository;

  public SystemUserDetailsService(SystemUserJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository.findByUsernameIgnoreCase(username)
        .map(SystemUserDetails::fromEntity)
        .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
  }
}
