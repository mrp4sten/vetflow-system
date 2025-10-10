package com.vetflow.api.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OwnerTest {

  @Test
  @DisplayName("Should create owner with valid data")
  void shouldCreateOwnerWithValidData() {
    String name = "Juan Pérez";
    String phone = "+525512345678";
    String email = "juan@vetflow.com";
    String address = "Av. Reforma 123, CDMX";
    Owner owner = new Owner(name, phone, email, address);
    assertThat(owner.getName()).isEqualTo(name);
    assertThat(owner.getPhone()).isEqualTo(phone);
    assertThat(owner.getEmail()).isEqualTo(email);
    assertThat(owner.getAddress()).isEqualTo(address);
    assertThat(owner.getCreatedAt()).isNotNull();
    assertThat(owner.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should throw exception when name is null or empty")
  void shouldThrowExceptionWhenNameIsNullOrEmpty() {
    assertThatThrownBy(() -> new Owner(null, "+525512345678", "test@email.com", "Address"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner name cannot be null");
    assertThatThrownBy(() -> new Owner("", "+525512345678", "test@email.com", "Address"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner name cannot be null");
  }

  @Test
  @DisplayName("Should throw excpetion when email is null or empty")
  void shouldThrowExceptionWhenEmailIsNullOrEmpty() {
    assertThatThrownBy(() -> new Owner("John Doe", "+525512345678", null, "Address"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner email cannot be null or empty");
    assertThatThrownBy(() -> new Owner("John Doe", "+525512345678", "", "Address"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner email cannot be null or empty");
  }

  @Test
  @DisplayName("Should throw exception when email is invalid")
  void shouldThrowExceptionWhenEmailIsInvalid() {
    String invalidEmail = "not-an-email";
    assertThatThrownBy(() -> new Owner("John Doe", "+525512345678", invalidEmail, "Address"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid Owner email format");
  }

  @Test
  @DisplayName("Should throw exception when phone is null or empty")
  void shouldThrowExceptionWhenPhoneIsNullOrEmpty() {
    assertThatThrownBy(() -> new Owner("John Doe", null, "test@email.com", "Address"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner phone cannot be null or empty");
    assertThatThrownBy(() -> new Owner("John Doe", "", "test@email.com", "Address"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner phone cannot be null or empty");
  }

  @Test
  @DisplayName("Should throw exception when phone is invalid")
  void shouldThrowExceptionWhenPhoneIsInvalid() {
    String invalidPhone = "+3f31233441fa";
    assertThatThrownBy(() -> new Owner("John Doe", invalidPhone, "test@email.com", "Address"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid Owner phone format");
  }

  @Test
  @DisplayName("Should throw exception when address is null or empty")
  void shouldThrowExceptionWhenAddressIsNullOrEmpty() {
    assertThatThrownBy(() -> new Owner("John Doe", "+525512345678", "test@email.com", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner address cannot be null or empty");
    assertThatThrownBy(() -> new Owner("John Doe", "+525512345678", "test@email.com", ""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Owner address cannot be null or empty");
  }

  @Test
  @DisplayName("Should change email successfully")
  void shouldChangeEmailSuccessfully() {
    Owner owner = new Owner("Maria", "+525512345678", "old@email.com", "Address");
    String newEmail = "new@email.com";
    owner.changeEmail(newEmail);
    assertThat(owner.getEmail()).isEqualTo(newEmail);
    assertThat(owner.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should throw exception when changing to invalid email")
  void shouldThrowExceptionWhenChangingToInvalidEmail() {
    Owner owner = new Owner("Maria", "+525512345678", "old@email.com", "Address");
    String invalidEmail = "invalid-email";
    assertThatThrownBy(() -> owner.changeEmail(invalidEmail)).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid Owner email format");
  }

  @Test
  @DisplayName("Should update phone number successfully")
  void shouldUpdatePhoneNumberSuccessfully() {
    Owner owner = new Owner("Carlos", "+525512345678", "carlos@email.com", "Address");
    String newPhone = "+525598765432";
    owner.changePhone(newPhone);
    assertThat(owner.getPhone()).isEqualTo(newPhone);
    assertThat(owner.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should update address successfully")
  void shouldUpdateAddressSuccessfully() {
    Owner owner = new Owner("Ana", "+525512345678", "ana@email.com", "Old Address");
    String newAddress = "New Address 456";
    owner.updateAddress(newAddress);
    assertThat(owner.getAddress()).isEqualTo(newAddress);
    assertThat(owner.getUpdatedAt()).isNotNull();
  }
}