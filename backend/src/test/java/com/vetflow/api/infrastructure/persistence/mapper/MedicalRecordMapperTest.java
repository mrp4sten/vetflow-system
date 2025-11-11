// src/test/java/com/vetflow/api/infrastructure/persistence/mapper/MedicalRecordMapperTest.java
package com.vetflow.api.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vetflow.api.domain.model.MedicalRecord;
import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.infrastructure.persistence.entity.MedicalRecordEntity;

class MedicalRecordMapperTest {

    private final PatientMapperImpl patientMapper = new PatientMapperImpl();

    private MedicalRecordMapperImpl newMapper() {
        MedicalRecordMapperImpl m = new MedicalRecordMapperImpl();
        try {
            Field f = MedicalRecordMapperImpl.class.getDeclaredField("patientMapper");
            f.setAccessible(true);
            f.set(m, patientMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return m;
    }

    @Test
    @DisplayName("Domain -> Entity -> Domain maps 1:1 and normalizes texts")
    void roundTrip_medicalRecord() {
        MedicalRecordMapper mapper = newMapper();

        Patient patient = Patient.builder()
            .id(2L)
            .name("Mishi")
            .owner(Owner.builder().id(20L).name("Ana").email("a@v.com").phone("222").address("addr").build())
            .build();

        MedicalRecord domain = MedicalRecord.builder()
            .id(5L)
            .patient(patient)
            .veterinarianId(99L)
            .visitDate(LocalDateTime.of(2025, 2, 1, 12, 0))
            .diagnosis("  otitis   ")
            .treatment("  drops  ")
            .medications("  amoxi ")
            .notes("  mejorar en 3 dias ")
            .createdAt(LocalDateTime.of(2025, 2, 1, 12, 1))
            .build();

        MedicalRecordEntity entity = mapper.toEntity(domain);

        assertThat(entity.getDiagnosis()).isEqualTo("otitis");
        assertThat(entity.getTreatment()).isEqualTo("drops");
        assertThat(entity.getMedications()).isEqualTo("amoxi");
        assertThat(entity.getNotes()).isEqualTo("mejorar en 3 dias");

        MedicalRecord back = mapper.toDomain(entity);

        assertThat(back.getPatient().getId()).isEqualTo(2L);
        assertThat(back.getVeterinarianId()).isEqualTo(99L);
        assertThat(back.getDiagnosis()).isEqualTo("otitis");
        assertThat(back.getTreatment()).isEqualTo("drops");
        assertThat(back.getMedications()).isEqualTo("amoxi");
        assertThat(back.getNotes()).isEqualTo("mejorar en 3 dias");
    }

    @Test
    @DisplayName("Null/blank text -> null")
    void normalize_blankToNull() {
        MedicalRecordMapper mapper = newMapper();

        MedicalRecord domain = MedicalRecord.builder()
            .patient(Patient.builder().id(1L).name("X").build())
            .diagnosis("  ")
            .treatment(null)
            .build();

        MedicalRecordEntity entity = mapper.toEntity(domain);

        assertThat(entity.getDiagnosis()).isNull();
        assertThat(entity.getTreatment()).isNull();
    }
}
