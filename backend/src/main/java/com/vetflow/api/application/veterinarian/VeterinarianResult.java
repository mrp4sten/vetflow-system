package com.vetflow.api.application.veterinarian;

import java.time.LocalDateTime;

import com.vetflow.api.domain.model.Veterinarian;

import lombok.Builder;

/**
 * Result DTO for veterinarian queries.
 */
@Builder
public record VeterinarianResult(
    Long id,
    String username,
    String email,
    boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime lastLogin
) {

  /**
   * Creates a VeterinarianResult from a domain Veterinarian.
   */
  public static VeterinarianResult from(Veterinarian veterinarian) {
    return VeterinarianResult.builder()
        .id(veterinarian.getId())
        .username(veterinarian.getUsername())
        .email(veterinarian.getEmail())
        .isActive(veterinarian.isActive())
        .createdAt(veterinarian.getCreatedAt())
        .lastLogin(veterinarian.getLastLogin())
        .build();
  }
}
