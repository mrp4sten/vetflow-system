package com.vetflow.api.audit;

import java.util.Map;

/**
 * Records auditable changes on key aggregates.
 */
public interface AuditService {

  Map<String, Object> snapshot(Object value);

  void recordCreation(String tableName, Long recordId, Object newValue);

  void recordUpdate(String tableName, Long recordId, Object oldValue, Object newValue);
}
