package com.vetflow.api.web.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetflow.api.infrastructure.persistence.entity.SystemUserEntity;
import com.vetflow.api.security.jwt.JwtTokenService;
import com.vetflow.api.security.user.SystemUserDetails;
import com.vetflow.api.web.v1.auth.TokenRequest;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @SuppressWarnings("removal")
  @MockBean
  private AuthenticationManager authenticationManager;

  @SuppressWarnings("removal")
  @MockBean
  private JwtTokenService tokenService;

  @Test
  void issueTokenReturnsJwtWhenCredentialsAreValid() throws Exception {
    SystemUserDetails principal = userDetails("agent");
    Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(tokenService.generateToken(principal)).thenReturn("jwt-token");
    when(tokenService.expiresInSeconds()).thenReturn(900L);

    TokenRequest request = new TokenRequest("agent", "secret");

    mockMvc.perform(post("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("jwt-token"))
        .andExpect(jsonPath("$.expiresInSeconds").value(900));
  }

  @Test
  void issueTokenReturnsUnauthorizedWhenAuthenticationFails() throws Exception {
    when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("boom"));

    TokenRequest request = new TokenRequest("ghost", "wrong");

    mockMvc.perform(post("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Invalid username or password"));
  }

  private static SystemUserDetails userDetails(String username) {
    SystemUserEntity entity = new SystemUserEntity();
    ReflectionTestUtils.setField(entity, "id", 5L);
    entity.setUsername(username);
    entity.setPasswordHash("$2a$test");
    entity.setRole("user");
    entity.setActive(true);
    return SystemUserDetails.fromEntity(entity);
  }
}
