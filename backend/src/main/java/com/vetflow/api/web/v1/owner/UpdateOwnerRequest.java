package com.vetflow.api.web.v1.owner;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/** Request payload for updating an owner's contact information. */
public record UpdateOwnerRequest(
    @Size(min = 1, max = 30, message = "phone must be between 1 and 30 characters")
    String phone,

    @Email(message = "email must be valid")
    @Size(max = 120, message = "email must be at most 120 characters")
    String email,

    @Size(min = 1, max = 255, message = "address must be between 1 and 255 characters")
    String address) {

  @AssertTrue(message = "At least one field must be provided to update an owner")
  public boolean hasUpdates() {
    return (phone != null && !phone.isBlank())
        || (email != null && !email.isBlank())
        || (address != null && !address.isBlank());
  }
}