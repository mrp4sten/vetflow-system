package com.vetflow.api.web.v1.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank String username, @NotBlank String password) {
}
