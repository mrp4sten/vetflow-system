package com.vetflow.api.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_log", indexes = {
    @Index(name = "idx_audit_log_table_record", columnList = "table_name,record_id"),
    @Index(name = "idx_audit_log_changed_at", columnList = "changed_at")
})
public class AuditLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "table_name", nullable = false, length = 50)
  private String tableName;

  @Column(name = "record_id", nullable = false)
  private Long recordId;

  @Column(nullable = false, length = 20)
  private String action;

  @Column(name = "old_values", columnDefinition = "jsonb")
  private String oldValues;

  @Column(name = "new_values", columnDefinition = "jsonb")
  private String newValues;

  @Column(name = "changed_by", length = 100)
  private String changedBy;

  @Column(name = "changed_at")
  private LocalDateTime changedAt;

  public AuditLogEntity() {
  }

  public AuditLogEntity(String tableName, Long recordId, String action, String oldValues, String newValues, String changedBy, LocalDateTime changedAt) {
    this.tableName = tableName;
    this.recordId = recordId;
    this.action = action;
    this.oldValues = oldValues;
    this.newValues = newValues;
    this.changedBy = changedBy;
    this.changedAt = changedAt;
  }

  public Long getId() {
    return id;
  }

  public String getTableName() {
    return tableName;
  }

  public Long getRecordId() {
    return recordId;
  }

  public String getAction() {
    return action;
  }

  public String getOldValues() {
    return oldValues;
  }

  public String getNewValues() {
    return newValues;
  }

  public String getChangedBy() {
    return changedBy;
  }

  public LocalDateTime getChangedAt() {
    return changedAt;
  }
}
