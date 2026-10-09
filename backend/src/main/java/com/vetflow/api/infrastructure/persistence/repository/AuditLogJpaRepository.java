package com.vetflow.api.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vetflow.api.infrastructure.persistence.entity.AuditLogEntity;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {
}
