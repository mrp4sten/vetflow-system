package com.vetflow.api.web.v1.owner;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request payload for creating a new owner. */
public record CreateOwnerRequest(
    @NotBlank(message = "name is required")
    @Size(max = 120, message = "name must be at most 120 characters")
    String name,

    @NotBlank(message = "phone is required")
    @Size(max = 30, message = "phone must be at most 30 characters")
    String phone,

    @Email(message = "email must be valid")
    @Size(max = 120, message = "email must be at most 120 characters")
    String email,

    @Size(max = 255, message = "address must be at most 255 characters")
    String address) {
}