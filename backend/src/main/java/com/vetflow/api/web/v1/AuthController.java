package com.vetflow.api.web.v1;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetflow.api.security.jwt.JwtTokenService;
import com.vetflow.api.security.user.SystemUserDetails;
import com.vetflow.api.web.v1.auth.TokenRequest;
import com.vetflow.api.web.v1.auth.TokenResponse;
import com.vetflow.api.web.v1.error.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "Authentication", description = "Obtain JWT tokens for API access")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenService tokenService;

  public AuthController(AuthenticationManager authenticationManager, JwtTokenService tokenService) {
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
  }

  @PostMapping("/token")
  @Operation(summary = "Issue JWT token", description = "Authenticates credentials and returns a bearer token.")
  public ResponseEntity<Object> issueToken(@Valid @RequestBody TokenRequest request, HttpServletRequest httpRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.username(), request.password()));
      SystemUserDetails principal = (SystemUserDetails) authentication.getPrincipal();
      String token = tokenService.generateToken(principal);
      return ResponseEntity.ok(new TokenResponse(token, tokenService.expiresInSeconds()));
    } catch (AuthenticationException ex) {
      ErrorResponse error = new ErrorResponse(Instant.now(),
          HttpStatus.UNAUTHORIZED.value(),
          HttpStatus.UNAUTHORIZED.getReasonPhrase(),
          "Invalid username or password",
          httpRequest.getRequestURI(),
          null);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
  }
}
