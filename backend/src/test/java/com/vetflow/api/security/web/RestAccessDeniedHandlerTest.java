package com.vetflow.api.security.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vetflow.api.web.v1.error.ErrorResponse;

class RestAccessDeniedHandlerTest {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Test
  void writesForbiddenResponseBody() throws Exception {
    RestAccessDeniedHandler handler = new RestAccessDeniedHandler(objectMapper);
    MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/v1/admin");
    MockHttpServletResponse response = new MockHttpServletResponse();

    handler.handle(request, response, new org.springframework.security.access.AccessDeniedException("nope"));

    assertThat(response.getStatus()).isEqualTo(403);
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    ErrorResponse error = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);
    assertThat(error.status()).isEqualTo(403);
    assertThat(error.path()).isEqualTo("/api/v1/admin");
    assertThat(error.message()).contains("permission");
  }
}
