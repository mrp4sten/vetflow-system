package com.vetflow.api.security.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetflow.api.security.jwt.JwtAuthenticationFilter;
import com.vetflow.api.security.jwt.JwtTokenService;
import com.vetflow.api.security.web.RestAccessDeniedHandler;
import com.vetflow.api.security.web.RestAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      CorsConfigurationSource corsConfigurationSource,
      AuthenticationProvider authenticationProvider,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      RestAuthenticationEntryPoint authenticationEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler,
      SecurityProperties securityProperties)
      throws Exception {
    boolean csrfDisabled = securityProperties.isDisableCsrf();

    if (csrfDisabled) {
      http.csrf(csrf -> csrf.disable()); // NOSONAR - Stateless JWT API; CSRF tokens would never be sent
    } else {
      http.csrf(Customizer.withDefaults());
    }

    SessionCreationPolicy sessionPolicy = csrfDisabled ? SessionCreationPolicy.STATELESS : SessionCreationPolicy.IF_REQUIRED;

    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .sessionManagement(session -> session.sessionCreationPolicy(sessionPolicy))
        .authenticationProvider(authenticationProvider)
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/v1/auth/token").permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
            .anyRequest().authenticated())
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .headers(headers -> headers
            .contentSecurityPolicy(csp -> csp.policyDirectives(
	            "default-src 'self'; " +
	                "script-src 'self'; " +
	                "style-src 'self' 'unsafe-inline'; " +
	                "img-src 'self' data:; " +
	                "connect-src 'self'; " +
	                "frame-ancestors 'none'"))
            .frameOptions(frame -> frame.deny())
            .httpStrictTransportSecurity(hsts -> hsts
                .includeSubDomains(true)
                .preload(true)
                .maxAgeInSeconds(31536000)));

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public JwtTokenService jwtTokenService(SecurityProperties properties) {
    return new JwtTokenService(properties);
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenService tokenService,
      UserDetailsService userDetailsService) {
    return new JwtAuthenticationFilter(tokenService, userDetailsService);
  }

  @Bean
  public RestAuthenticationEntryPoint restAuthenticationEntryPoint(ObjectMapper objectMapper) {
    return new RestAuthenticationEntryPoint(objectMapper);
  }

  @Bean
  public RestAccessDeniedHandler restAccessDeniedHandler(ObjectMapper objectMapper) {
    return new RestAccessDeniedHandler(objectMapper);
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(
      @Value("${vetflow.api.cors.allowed-origins:http://localhost:3000}") String origins) {
    List<String> allowedOrigins = Arrays.stream(origins.split(","))
        .map(String::trim)
        .filter(origin -> !origin.isBlank())
        .toList();
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/v1/**", configuration);
    return source;
  }
}
