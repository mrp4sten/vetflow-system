// src/main/java/com/vetflow/api/infrastructure/persistence/mapper/MedicalRecordMapper.java
package com.vetflow.api.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.vetflow.api.domain.model.MedicalRecord;
import com.vetflow.api.infrastructure.persistence.entity.MedicalRecordEntity;

@Mapper(
    componentModel = "spring",
    uses = { PatientMapper.class },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MedicalRecordMapper {

    // domain -> entity
    @Mapping(target = "diagnosis", expression = "java(normalize(domain.getDiagnosis()))")
    @Mapping(target = "treatment", expression = "java(normalize(domain.getTreatment()))")
    @Mapping(target = "medications", expression = "java(normalize(domain.getMedications()))")
    @Mapping(target = "notes", expression = "java(normalize(domain.getNotes()))")
    MedicalRecordEntity toEntity(MedicalRecord domain);

    // entity -> domain
    @Mapping(target = "diagnosis", expression = "java(normalize(entity.getDiagnosis()))")
    @Mapping(target = "treatment", expression = "java(normalize(entity.getTreatment()))")
    @Mapping(target = "medications", expression = "java(normalize(entity.getMedications()))")
    @Mapping(target = "notes", expression = "java(normalize(entity.getNotes()))")
    MedicalRecord toDomain(MedicalRecordEntity entity);

    // helper
    default String normalize(String text) {
        if (text == null) return null;
        String t = text.trim();
        return t.isEmpty() ? null : t;
    }
}
