package com.vetflow.api.audit;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetflow.api.infrastructure.persistence.entity.AuditLogEntity;
import com.vetflow.api.infrastructure.persistence.repository.AuditLogJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService implements AuditService {

  private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

  private final AuditLogJpaRepository repository;
  private final ObjectMapper objectMapper;

  @Override
  public Map<String, Object> snapshot(Object value) {
    if (value == null) {
      return Collections.emptyMap();
    }
    try {
      return objectMapper.convertValue(value,
          objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
    } catch (IllegalArgumentException ex) {
      log.warn("Failed to build audit snapshot", ex);
      return Collections.emptyMap();
    }
  }

  @Override
  public void recordCreation(String tableName, Long recordId, Object newValue) {
    save(tableName, recordId, "INSERT", null, toJson(newValue));
  }

  @Override
  public void recordUpdate(String tableName, Long recordId, Object oldValue, Object newValue) {
    save(tableName, recordId, "UPDATE", toJson(oldValue), toJson(newValue));
  }

  private void save(String tableName, Long recordId, String action, String oldValueJson, String newValueJson) {
    String actor = resolveActor();
    AuditLogEntity entity = new AuditLogEntity(tableName, recordId, action, oldValueJson, newValueJson, actor, LocalDateTime.now());
    repository.save(entity);
  }

  private String resolveActor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
      return "system";
    }
    return authentication.getName();
  }

  private String toJson(Object value) {
    if (value == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      log.warn("Failed to serialize audit payload", ex);
      return null;
    }
  }
}
