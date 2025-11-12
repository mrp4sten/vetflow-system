package com.vetflow.api.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import com.vetflow.api.infrastructure.persistence.entity.SystemUserEntity;
import com.vetflow.api.security.user.SystemUserDetails;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtTokenService tokenService;

  @Mock
  private UserDetailsService userDetailsService;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void populatesSecurityContextWhenTokenValid() throws Exception {
    SystemUserDetails details = userDetails("agent");
    when(tokenService.isTokenValid("token")).thenReturn(true);
    when(tokenService.extractUsername("token")).thenReturn("agent");
    when(userDetailsService.loadUserByUsername("agent")).thenReturn(details);
    when(tokenService.isTokenValid("token", details)).thenReturn(true);

    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenService, userDetailsService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer token");
    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(details);
  }

  @Test
  void skipsAuthenticationWhenTokenMissingOrInvalid() throws Exception {
    when(tokenService.isTokenValid("bad")).thenReturn(false);

    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenService, userDetailsService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer bad");
    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(userDetailsService, never()).loadUserByUsername(any());
  }

  private static SystemUserDetails userDetails(String username) {
    SystemUserEntity entity = new SystemUserEntity();
    ReflectionTestUtils.setField(entity, "id", 33L);
    entity.setUsername(username);
    entity.setPasswordHash("$2a$test");
    entity.setRole("user");
    entity.setActive(true);
    return SystemUserDetails.fromEntity(entity);
  }
}
