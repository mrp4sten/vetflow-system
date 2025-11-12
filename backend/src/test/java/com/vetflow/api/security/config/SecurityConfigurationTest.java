package com.vetflow.api.security.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@SpringBootTest(properties = "vetflow.api.cors.allowed-origins=http://a.test, https://b.test")
class SecurityConfigurationTest {

  @Autowired
  private SecurityFilterChain securityFilterChain;

  @Autowired
  private CorsConfigurationSource corsConfigurationSource;

  @Test
  void shouldLoadSecurityFilterChainBean() {
    assertThat(securityFilterChain).isNotNull();
  }

  @Test
  void corsConfigurationHonorsConfiguredOrigins() {
    CorsConfiguration configuration = corsConfigurationSource.getCorsConfiguration(new MockHttpServletRequest("GET", "/api/v1/owners"));
    assertThat(configuration).isNotNull();
    assertThat(configuration.getAllowedOrigins()).containsExactly("http://a.test", "https://b.test");
    assertThat(configuration.getAllowedMethods()).contains("DELETE", "GET");
  }
}
