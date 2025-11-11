package com.vetflow.api.domain.port;

import java.util.List;
import java.util.Optional;

import com.vetflow.api.domain.model.MedicalRecord;

public interface MedicalRecordRepository {
    MedicalRecord save(MedicalRecord medicalRecord);
    Optional<MedicalRecord> findById(Long id);
    List<MedicalRecord> findByPatientId(Long patientId);
    void deleteById(Long id);
}
