package com.vetflow.api.web.v1.auth;

public record TokenResponse(String accessToken, long expiresInSeconds) {
}
