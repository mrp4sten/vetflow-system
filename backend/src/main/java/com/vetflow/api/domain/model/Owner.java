package com.vetflow.api.domain.model;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a pet owner in the VetFlow domain.
 *
 * <p><strong>Design notes</strong>:
 * <ul>
 *   <li>DDD rich model: encapsulates invariants and domain behavior.</li>
 *   <li>No persistence annotations here (Hexagonal Architecture).</li>
 *   <li>Controlled mutability: no public setters; use factory and domain methods.</li>
 * </ul>
 * </p>
 *
 * <p><strong>Invariants</strong>:
 * <ul>
 *   <li><code>name</code>: required, 1..100 chars.</li>
 *   <li><code>email</code>: required, simple RFC-like format.</li>
 *   <li><code>phone</code>: required, international-like format, 10..20 digits.</li>
 *   <li><code>address</code>: required, 1..500 chars.</li>
 * </ul>
 * </p>
 */
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Owner {

  private Long id;
  private String name;
  private String phone;
  private String email;
  private String address;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  private static final Pattern PHONE_ALLOWED_CHARS =
      Pattern.compile("^[+0-9()\\s-]+$");

  // ========= FACTORY METHODS =========

  public static Owner create(String name, String phone, String email, String address) {
    return create(name, phone, email, address, Clock.systemDefaultZone());
  }

  static Owner create(String name, String phone, String email, String address, Clock clock) {
    LocalDateTime now = LocalDateTime.now(clock);
    return Owner.builder()
        .name(validateName(name))
        .phone(validatePhone(phone))
        .email(validateEmail(email))
        .address(validateAddress(address))
        .createdAt(now)
        .updatedAt(now)
        .build();
  }

  // ========= DOMAIN BEHAVIOR =========

  public void changeEmail(String newEmail) {
    this.email = validateEmail(newEmail);
    touch();
  }

  public void changePhone(String newPhone) {
    this.phone = validatePhone(newPhone);
    touch();
  }

  public void updateAddress(String newAddress) {
    this.address = validateAddress(newAddress);
    touch();
  }

  void setId(Long id) { this.id = id; }

  private void touch() { this.updatedAt = LocalDateTime.now(); }

  // ========= VALIDATIONS =========

  private static String validateName(String name) {
    if (name == null || name.trim().isEmpty())
      throw new IllegalArgumentException("Owner name cannot be null or empty");
    String n = name.trim();
    if (n.length() > 100)
      throw new IllegalArgumentException("Owner name cannot exceed 100 characters");
    return n;
  }

  private static String validateEmail(String email) {
    if (email == null || email.trim().isEmpty())
      throw new IllegalArgumentException("Owner email cannot be null or empty");
    String e = email.trim();
    if (!EMAIL_PATTERN.matcher(e).matches())
      throw new IllegalArgumentException("Invalid owner email format");
    return e;
  }

  private static String validatePhone(String phone) {
    if (phone == null || phone.trim().isEmpty())
      throw new IllegalArgumentException("Owner phone cannot be null or empty");
    String p = phone.trim();
    if (!PHONE_ALLOWED_CHARS.matcher(p).matches())
      throw new IllegalArgumentException("Invalid owner phone characters");
    String digits = p.replaceAll("\\D", "");
    if (digits.length() < 10 || digits.length() > 20)
      throw new IllegalArgumentException("Owner phone must contain between 10 and 20 digits");
    return p;
  }

  private static String validateAddress(String address) {
    if (address == null || address.trim().isEmpty())
      throw new IllegalArgumentException("Owner address cannot be null or empty");
    String a = address.trim();
    if (a.length() > 500)
      throw new IllegalArgumentException("Owner address cannot exceed 500 characters");
    return a;
  }
}
