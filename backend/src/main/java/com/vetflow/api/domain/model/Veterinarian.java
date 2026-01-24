package com.vetflow.api.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a veterinarian (system user with role 'veterinarian').
 *
 * <p><strong>Design notes</strong>:
 * <ul>
 *   <li>DDD value-rich entity: represents a veterinarian from system_users.</li>
 *   <li>Hexagonal: no persistence annotations here.</li>
 *   <li>Read-only from system_users: managed by authentication system.</li>
 * </ul>
 * </p>
 *
 * <p><strong>Invariants</strong>:
 * <ul>
 *   <li><code>id</code>: references system_users.id.</li>
 *   <li><code>username</code>: required, unique.</li>
 *   <li><code>email</code>: required, unique.</li>
 *   <li><code>isActive</code>: determines if vet can be assigned to appointments.</li>
 * </ul>
 * </p>
 */
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Veterinarian {

  private Long id;

  private String username;
  private String email;
  private boolean isActive;

  private LocalDateTime createdAt;
  private LocalDateTime lastLogin;

  // ========= FACTORY METHODS =========

  /**
   * Creates a new Veterinarian reference.
   * 
   * @param id system_users.id where role = 'veterinarian'
   * @param username the user's username
   * @param email the user's email
   * @param isActive whether the veterinarian is active
   * @return a new Veterinarian instance
   */
  public static Veterinarian of(Long id, String username, String email, boolean isActive) {
    Objects.requireNonNull(id, "Veterinarian ID cannot be null");
    Objects.requireNonNull(username, "Username cannot be null");
    Objects.requireNonNull(email, "Email cannot be null");

    return Veterinarian.builder()
        .id(id)
        .username(username)
        .email(email)
        .isActive(isActive)
        .build();
  }

  // ========= DOMAIN BEHAVIOR =========

  /**
   * Checks if this veterinarian can be assigned to appointments.
   */
  public boolean canBeAssigned() {
    return this.isActive;
  }

  /**
   * Gets a display name for the veterinarian.
   */
  public String getDisplayName() {
    return username;
  }
}
