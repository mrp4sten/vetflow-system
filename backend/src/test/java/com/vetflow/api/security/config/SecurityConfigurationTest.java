package com.vetflow.api.security.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest
class SecurityConfigurationTest {

  @Autowired
  private SecurityFilterChain securityFilterChain;

  @Test
  void shouldLoadSecurityFilterChainBean() {
    assertThat(securityFilterChain).isNotNull();
  }
}
