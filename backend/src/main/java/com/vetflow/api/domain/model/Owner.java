package com.vetflow.api.domain.model;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import lombok.Data;

/**
 * Represents a pet owner in the veterinary system.
 * 
 * <p>This is a rich domain model following DDD principles, containing
 * both data and business behavior related to pet owners.</p>
 *
 * <p>Key business rules:
 * <ul>
 *   <li>Email must be valid format</li>
 *   <li>Phone must follow international format</li>
 *   <li>Name and address have length constraints</li>
 * </ul>
 * </p>
 *
 * @author MrP4sten
 * @version 1.0
 */
@Data
public class Owner {
  private Long id;
  private String name;
  private String phone;
  private String email;
  private String address;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Email REGEX
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  // Phone REGEX
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{10,}$");

  // Main constructor
  public Owner(String name, String phone, String email, String address) {
    this.name = validateName(name);
    this.phone = validatePhone(phone);
    this.email = validateEmail(email);
    this.address = validateAddress(address);
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  // Empty constructor for JPA
  protected Owner() {
    // For frameworks that need an empty constructor
  }

  // Business methods
  private String validateEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Owner email cannot be null or empty");
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new IllegalArgumentException("Invalid Owner email format");
    }

    return email.trim();
  }

  private String validatePhone(String phone) {
    if (phone == null || phone.trim().isEmpty()) {
      throw new IllegalArgumentException("Owner phone cannot be null or empty");
    }
    if (!PHONE_PATTERN.matcher(phone).matches()) {
      throw new IllegalArgumentException("Invalid Owner phone format");
    }
    return phone.trim();
  }

  private String validateAddress(String address) {
    if (address == null || address.trim().isEmpty()) {
      throw new IllegalArgumentException("Owner address cannot be null or empty");
    }
    if (address.length() > 500) {
      throw new IllegalArgumentException("Owner address cannot exced 500 characters");
    }
    return address;
  }

  private String validateName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Owner name cannot be null or empty");
    }
    if (name.length() > 100) {
      throw new IllegalArgumentException("Owner name cannot exced 100 characters");
    }
    return name;
  }

  public void changeEmail(String newEmail) {
    this.email = validateEmail(newEmail);
    this.updatedAt = LocalDateTime.now();
  }

  public void changePhone(String newPhone) {
    this.phone = validatePhone(newPhone);
    this.updatedAt = LocalDateTime.now();
  }

  public void updateAddress(String newAddress) {
    this.address = validateAddress(newAddress);
    this.updatedAt = LocalDateTime.now();
  }

}
