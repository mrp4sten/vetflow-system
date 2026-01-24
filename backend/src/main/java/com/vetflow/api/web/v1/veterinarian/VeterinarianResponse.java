package com.vetflow.api.web.v1.veterinarian;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vetflow.api.application.veterinarian.VeterinarianResult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * REST response DTO for veterinarian information.
 */
@Builder
@Schema(description = "Veterinarian information")
public record VeterinarianResponse(
    @Schema(description = "Veterinarian ID", example = "1")
    Long id,

    @Schema(description = "Username", example = "dr.smith")
    String username,

    @Schema(description = "Email address", example = "dr.smith@vetflow.test")
    String email,

    @Schema(description = "Whether the veterinarian is active", example = "true")
    @JsonProperty("isActive")
    boolean isActive,

    @Schema(description = "Account creation timestamp")
    LocalDateTime createdAt,

    @Schema(description = "Last login timestamp")
    LocalDateTime lastLogin
) {

  /**
   * Creates a VeterinarianResponse from application result.
   */
  public static VeterinarianResponse from(VeterinarianResult result) {
    return VeterinarianResponse.builder()
        .id(result.id())
        .username(result.username())
        .email(result.email())
        .isActive(result.isActive())
        .createdAt(result.createdAt())
        .lastLogin(result.lastLogin())
        .build();
  }
}
