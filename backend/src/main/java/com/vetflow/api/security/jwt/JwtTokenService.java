package com.vetflow.api.security.jwt;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.vetflow.api.security.config.SecurityProperties;
import com.vetflow.api.security.user.SystemUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Handles creation and validation of JWT access tokens.
 */
public class JwtTokenService {

  private final SecurityProperties properties;
  private final Key signingKey;

  public JwtTokenService(SecurityProperties properties) {
    this.properties = properties;
    this.signingKey = Keys.hmacShaKeyFor(properties.getJwt().getSecret().getBytes());
  }

  public String generateToken(SystemUserDetails userDetails) {
    Instant now = Instant.now();
    Instant expiry = now.plus(properties.getJwt().getExpiration());
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiry))
        .claim("roles", userDetails.authoritiesAsRoles())
        .signWith(signingKey)
        .compact();
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = parse(token);
      return claims.getExpiration() != null && claims.getExpiration().after(new Date());
    } catch (RuntimeException ex) {
      return false;
    }
  }

  public boolean isTokenValid(String token, SystemUserDetails userDetails) {
    Claims claims = parse(token);
    String subject = claims.getSubject();
    return subject != null && subject.equalsIgnoreCase(userDetails.getUsername())
        && claims.getExpiration() != null && claims.getExpiration().after(new Date());
  }

  public String extractUsername(String token) {
    return parse(token).getSubject();
  }

  @SuppressWarnings("unchecked")
  public List<String> extractRoles(String token) {
    return (List<String>) parse(token).getOrDefault("roles", List.of());
  }

  public Instant extractExpiry(String token) {
    Date expiration = parse(token).getExpiration();
    return expiration == null ? null : expiration.toInstant();
  }

  private Claims parse(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public long expiresInSeconds() {
    return properties.getJwt().getExpiration().getSeconds();
  }
}
