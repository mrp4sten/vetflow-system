package com.vetflow.api.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Owner} domain model.
 *
 * Notes:
 * - Uses the static factory Owner.create(...) to enforce invariants.
 * - Asserts on error messages to keep UX/dev hints consistent.
 */
class OwnerTest {

  @Test
  @DisplayName("Should create owner with valid data")
  void shouldCreateOwnerWithValidData() {
    var owner = Owner.create(
        "Juan Pérez",
        "+52 55 1234 5678",
        "juan@vetflow.com",
        "Av. Reforma 123, CDMX"
    );

    assertThat(owner.getId()).isNull();
    assertThat(owner.getName()).isEqualTo("Juan Pérez");
    assertThat(owner.getPhone()).isEqualTo("+52 55 1234 5678");
    assertThat(owner.getEmail()).isEqualTo("juan@vetflow.com");
    assertThat(owner.getAddress()).isEqualTo("Av. Reforma 123, CDMX");
    assertThat(owner.getCreatedAt()).isNotNull();
    assertThat(owner.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should throw when name is null or empty")
  void shouldThrowWhenNameIsNullOrEmpty() {
    assertThatThrownBy(() -> Owner.create(null, "+525512345678", "t@e.com", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner name cannot be null or empty");

    assertThatThrownBy(() -> Owner.create("  ", "+525512345678", "t@e.com", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner name cannot be null or empty");
  }

  @Test
  @DisplayName("Should throw when name exceeds 100 characters")
  void shouldThrowWhenNameExceedsLimit() {
    var longName = "x".repeat(101);
    assertThatThrownBy(() -> Owner.create(longName, "+525512345678", "t@e.com", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner name cannot exceed 100 characters");
  }

  @Test
  @DisplayName("Should throw when email is null or empty")
  void shouldThrowWhenEmailIsNullOrEmpty() {
    assertThatThrownBy(() -> Owner.create("John", "+525512345678", null, "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner email cannot be null or empty");

    assertThatThrownBy(() -> Owner.create("John", "+525512345678", "   ", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner email cannot be null or empty");
  }

  @Test
  @DisplayName("Should throw when email is invalid")
  void shouldThrowWhenEmailIsInvalid() {
    assertThatThrownBy(() -> Owner.create("John", "+525512345678", "not-an-email", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid owner email format");
  }

  @Test
  @DisplayName("Should throw when phone is null or empty")
  void shouldThrowWhenPhoneIsNullOrEmpty() {
    assertThatThrownBy(() -> Owner.create("John", null, "t@e.com", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner phone cannot be null or empty");

    assertThatThrownBy(() -> Owner.create("John", "   ", "t@e.com", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner phone cannot be null or empty");
  }

  @Test
  @DisplayName("Should throw when phone contains invalid characters")
  void shouldThrowWhenPhoneHasInvalidChars() {
    assertThatThrownBy(() -> Owner.create("John", "+52-55-12ab-5678", "t@e.com", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid owner phone characters");
  }

  @Test
  @DisplayName("Should throw when phone digits are less than 10 or more than 20")
  void shouldThrowWhenPhoneDigitsOutOfBounds() {
    // < 10 digits
    assertThatThrownBy(() -> Owner.create("John", "+52 55 123", "t@e.com", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("must contain between 10 and 20 digits");
    // > 20 digits
    assertThatThrownBy(() -> Owner.create("John", "+52 55 12345678901234567890", "t@e.com", "Addr"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("must contain between 10 and 20 digits");
  }

  @Test
  @DisplayName("Should accept phone with spaces, dashes and parentheses")
  void shouldAcceptPhoneWithSeparators() {
    var owner = Owner.create("John", "+52 (55) 1234-5678", "t@e.com", "Addr");
    assertThat(owner.getPhone()).isEqualTo("+52 (55) 1234-5678");
  }

  @Test
  @DisplayName("Should throw when address is null or empty")
  void shouldThrowWhenAddressIsNullOrEmpty() {
    assertThatThrownBy(() -> Owner.create("John", "+525512345678", "t@e.com", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner address cannot be null or empty");

    assertThatThrownBy(() -> Owner.create("John", "+525512345678", "t@e.com", "  "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner address cannot be null or empty");
  }

  @Test
  @DisplayName("Should throw when address exceeds 500 characters")
  void shouldThrowWhenAddressExceedsLimit() {
    var longAddr = "x".repeat(501);
    assertThatThrownBy(() -> Owner.create("John", "+525512345678", "t@e.com", longAddr))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner address cannot exceed 500 characters");
  }

  @Test
  @DisplayName("Should update email and refresh updatedAt")
  void shouldUpdateEmail() {
    var owner = Owner.create("Maria", "+525512345678", "old@email.com", "Addr");
    var before = owner.getUpdatedAt();
    owner.changeEmail("new@email.com");
    assertThat(owner.getEmail()).isEqualTo("new@email.com");
    assertThat(owner.getUpdatedAt()).isNotNull();
    // It may be equal if both operations are within the same tick, so use isAfterOrEqualTo
    assertThat(owner.getUpdatedAt()).isAfterOrEqualTo(before);
  }

  @Test
  @DisplayName("Should throw when changing to invalid email")
  void shouldThrowWhenChangingToInvalidEmail() {
    var owner = Owner.create("Maria", "+525512345678", "old@email.com", "Addr");
    assertThatThrownBy(() -> owner.changeEmail("invalid-email"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid owner email format");
  }

  @Test
  @DisplayName("Should update phone and refresh updatedAt")
  void shouldUpdatePhone() {
    var owner = Owner.create("Carlos", "+525512345678", "c@e.com", "Addr");
    var before = owner.getUpdatedAt();
    owner.changePhone("+52 55 9876 5432");
    assertThat(owner.getPhone()).isEqualTo("+52 55 9876 5432");
    assertThat(owner.getUpdatedAt()).isAfterOrEqualTo(before);
  }

  @Test
  @DisplayName("Should update address and refresh updatedAt")
  void shouldUpdateAddress() {
    var owner = Owner.create("Ana", "+525512345678", "a@e.com", "Old Address");
    var before = owner.getUpdatedAt();
    owner.updateAddress("New Address 456");
    assertThat(owner.getAddress()).isEqualTo("New Address 456");
    assertThat(owner.getUpdatedAt()).isAfterOrEqualTo(before);
  }
}
