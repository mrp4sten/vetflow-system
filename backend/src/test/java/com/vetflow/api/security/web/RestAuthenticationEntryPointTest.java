package com.vetflow.api.security.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vetflow.api.web.v1.error.ErrorResponse;

class RestAuthenticationEntryPointTest {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Test
  void writesUnauthorizedResponseBody() throws Exception {
    RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint(objectMapper);
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/patients");
    MockHttpServletResponse response = new MockHttpServletResponse();

    entryPoint.commence(request, response, new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException("unauthenticated"));

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    ErrorResponse error = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);
    assertThat(error.status()).isEqualTo(401);
    assertThat(error.path()).isEqualTo("/api/v1/patients");
    assertThat(error.message()).contains("Authentication is required");
  }
}
