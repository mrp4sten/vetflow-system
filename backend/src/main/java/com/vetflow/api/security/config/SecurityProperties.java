package com.vetflow.api.security.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Externalised security settings for the VetFlow API.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "vetflow.api.security")
public class SecurityProperties {

  private final Jwt jwt = new Jwt();

  @Getter
  @Setter
  public static class Jwt {
    /** Secret key used to sign JWT access tokens. */
    private String secret = "change-me-in-prod-32-char-secret!";

    /** Token time-to-live. */
    private Duration expiration = Duration.ofHours(1);
  }
}
