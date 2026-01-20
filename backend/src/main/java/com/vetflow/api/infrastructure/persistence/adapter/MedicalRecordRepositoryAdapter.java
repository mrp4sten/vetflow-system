// src/main/java/com/vetflow/api/infrastructure/persistence/adapter/MedicalRecordRepositoryAdapter.java
package com.vetflow.api.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vetflow.api.domain.model.MedicalRecord;
import com.vetflow.api.domain.port.MedicalRecordRepository;
import com.vetflow.api.infrastructure.persistence.entity.MedicalRecordEntity;
import com.vetflow.api.infrastructure.persistence.mapper.MedicalRecordMapper;
import com.vetflow.api.infrastructure.persistence.repository.MedicalRecordJpaRepository;

@Component
@Transactional
public class MedicalRecordRepositoryAdapter implements MedicalRecordRepository {

    private final MedicalRecordJpaRepository jpa;
    private final MedicalRecordMapper mapper;

    public MedicalRecordRepositoryAdapter(MedicalRecordJpaRepository jpa, MedicalRecordMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        MedicalRecordEntity entity = mapper.toEntity(medicalRecord);
        MedicalRecordEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicalRecord> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecord> findByPatientId(Long patientId) {
        return jpa.findByPatientIdOrderByVisitDateDesc(patientId)
                  .stream()
                  .map(mapper::toDomain)
                  .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecord> findAll() {
        return jpa.findAll()
                  .stream()
                  .map(mapper::toDomain)
                  .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
