package com.vetflow.api.domain.model;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a veterinary patient (pet) in the VetFlow domain.
 *
 * <p>
 * <strong>Design notes</strong>:
 * <ul>
 * <li>DDD-rich model: encapsulates invariants and behavior.</li>
 * <li>No persistence annotations: keep JPA in infrastructure layer
 * (Hexagonal).</li>
 * <li>Controlled mutability: no public setters; use factory and domain
 * methods.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Invariants</strong>:
 * <ul>
 * <li><code>name</code>: required, 1..100 chars.</li>
 * <li><code>species</code>: required, one of {@link Species}.</li>
 * <li><code>breed</code>: optional, max 50 chars.</li>
 * <li><code>birthDate</code>: required, not in the future.</li>
 * <li><code>owner</code>: required.</li>
 * </ul>
 * </p>
 */
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Patient {

  public enum Species {
    DOG, CAT;

    public static Species from(String value) {
      if (value == null)
        throw new IllegalArgumentException("Species cannot be null");
      String v = value.trim().toLowerCase();
      switch (v) {
        case "dog":
          return DOG;
        case "cat":
          return CAT;
        default:
          throw new IllegalArgumentException("Invalid species: " + value);
      }
    }
  }

  private Long id;
  private String name;
  private Species species;
  private String breed; // optional
  private LocalDate birthDate;
  private BigDecimal weight; // optional, in kg
  private boolean isActive; // soft delete flag
  private Owner owner;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // ========= FACTORY METHODS =========

  public static Patient create(String name,
      String species,
      String breed,
      LocalDate birthDate,
      Owner owner) {
    return create(name, species, breed, birthDate, null, owner, Clock.systemDefaultZone());
  }

  public static Patient create(String name,
      String species,
      String breed,
      LocalDate birthDate,
      BigDecimal weight,
      Owner owner) {
    return create(name, species, breed, birthDate, weight, owner, Clock.systemDefaultZone());
  }

  static Patient create(String name,
      String species,
      String breed,
      LocalDate birthDate,
      BigDecimal weight,
      Owner owner,
      Clock clock) {
    LocalDateTime now = LocalDateTime.now(clock);
    return Patient.builder()
        .name(validateName(name))
        .species(Species.from(species))
        .breed(validateBreed(breed))
        .birthDate(validateBirthDate(birthDate, clock))
        .weight(weight != null ? validateWeight(weight) : null)
        .isActive(true)
        .owner(validateOwner(owner))
        .createdAt(now)
        .updatedAt(now)
        .build();
  }

  // ========= DOMAIN BEHAVIOR =========

  public void rename(String newName) {
    this.name = validateName(newName);
    touch();
  }

  public void updateProfile(String species, String breed, LocalDate birthDate, Owner owner) {
    updateProfile(species, breed, birthDate, owner, Clock.systemDefaultZone());
  }

  public void updateProfile(String species, String breed, LocalDate birthDate, Owner owner, Clock clock) {
    this.species = Species.from(species);
    this.breed = validateBreed(breed);
    this.birthDate = validateBirthDate(birthDate, clock);
    this.owner = validateOwner(owner);
    touch(clock);
  }

  public int ageInYears() {
    return ageInYears(Clock.systemDefaultZone());
  }

  public int ageInYears(Clock clock) {
    LocalDate today = LocalDate.now(clock);
    int years = today.getYear() - birthDate.getYear();
    if (today.getDayOfYear() < birthDate.getDayOfYear())
      years--;
    return years;
  }

  public void deactivate() {
    if (!this.isActive) {
      throw new IllegalStateException("Patient is already deactivated");
    }
    this.isActive = false;
    touch();
  }

  public void activate() {
    if (this.isActive) {
      throw new IllegalStateException("Patient is already active");
    }
    this.isActive = true;
    touch();
  }

  public void updateWeight(BigDecimal weight) {
    this.weight = validateWeight(weight);
    touch();
  }

  void setId(Long id) {
    this.id = id;
  }

  private void touch() {
    this.updatedAt = LocalDateTime.now();
  }

  private void touch(Clock clock) {
    this.updatedAt = LocalDateTime.now(clock);
  }

  // ========= VALIDATIONS =========

  private static String validateName(String name) {
    if (name == null || name.trim().isEmpty())
      throw new IllegalArgumentException("Patient name cannot be null or empty");
    String n = name.trim();
    if (n.length() > 100)
      throw new IllegalArgumentException("Patient name cannot exceed 100 characters");
    return n;
  }

  private static String validateBreed(String breed) {
    if (breed == null)
      return null;
    String b = breed.trim();
    if (b.isEmpty())
      return null;
    if (b.length() > 50)
      throw new IllegalArgumentException("Breed cannot exceed 50 characters");
    return b;
  }

  private static Owner validateOwner(Owner owner) {
    return Objects.requireNonNull(owner, "Patient owner cannot be null");
  }

  private static LocalDate validateBirthDate(LocalDate birthDate, Clock clock) {
    if (birthDate == null)
      throw new IllegalArgumentException("Patient birth date cannot be null");
    if (birthDate.isAfter(LocalDate.now(clock)))
      throw new IllegalArgumentException("Patient birth date cannot be in the future");
    return birthDate;
  }

  private static BigDecimal validateWeight(BigDecimal weight) {
    if (weight == null)
      return null;
    if (weight.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Patient weight must be positive");
    if (weight.compareTo(new BigDecimal("500")) > 0)
      throw new IllegalArgumentException("Patient weight cannot exceed 500 kg");
    return weight;
  }

  @PrePersist
  void onCreate() {
    if (createdAt == null)
      createdAt = LocalDateTime.now();
    if (updatedAt == null)
      updatedAt = createdAt;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

}
