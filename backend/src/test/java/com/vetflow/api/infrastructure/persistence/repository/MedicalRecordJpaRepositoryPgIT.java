package com.vetflow.api.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.vetflow.api.infrastructure.persistence.AbstractPostgresDataJpaTest;
import com.vetflow.api.infrastructure.persistence.entity.MedicalRecordEntity;
import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;
import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class MedicalRecordJpaRepositoryPgIT extends AbstractPostgresDataJpaTest {

    @Autowired OwnerJpaRepository owners;
    @Autowired PatientJpaRepository patients;
    @Autowired MedicalRecordJpaRepository medicalRecords;

    @PersistenceContext
    EntityManager em;

    private PatientEntity newPatient() {
        OwnerEntity owner = new OwnerEntity();
        owner.setName("Owner med");
        owner.setEmail("owner-med@vetflow.com");
        owner.setPhone("+525500000000");
        owner.setAddress("CDMX");
        owner = owners.saveAndFlush(owner);

        PatientEntity p = new PatientEntity();
        p.setName("Paciente med");
        p.setSpecies("dog");
        p.setOwner(owner);
        return patients.saveAndFlush(p);
    }

    private void insertSystemUser(String username) {
        em.createNativeQuery("""
            INSERT INTO system_users (username, email, password_hash, role, is_active, created_at, updated_at)
            VALUES (?1, ?2, 'x', 'veterinarian', true, now(), now())
            """)
          .setParameter(1, username)
          .setParameter(2, username + "@vetflow.test")
          .executeUpdate();
    }

    @Test
    @DisplayName("saves medical record and queries by patient ordered desc (Postgres)")
    void savesAndQueries() {
        insertSystemUser("vet1");
        insertSystemUser("vet2");

        PatientEntity patient = newPatient();

        MedicalRecordEntity r1 = new MedicalRecordEntity();
        r1.setPatient(patient);
        r1.setVeterinarianId(1L);
        r1.setVisitDate(LocalDateTime.of(2025, 2, 1, 10, 0));
        r1.setDiagnosis("otitis");
        r1.setCreatedAt(LocalDateTime.of(2025, 2, 1, 11, 0));
        medicalRecords.saveAndFlush(r1);

        MedicalRecordEntity r2 = new MedicalRecordEntity();
        r2.setPatient(patient);
        r2.setVeterinarianId(2L);
        r2.setVisitDate(LocalDateTime.of(2025, 2, 2, 10, 0));
        r2.setDiagnosis("control");
        r2.setCreatedAt(LocalDateTime.of(2025, 2, 2, 11, 0));
        medicalRecords.saveAndFlush(r2);

        List<MedicalRecordEntity> found =
            medicalRecords.findByPatientIdOrderByVisitDateDesc(patient.getId());

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getDiagnosis()).isEqualTo("control");
        assertThat(found.get(1).getDiagnosis()).isEqualTo("otitis");
    }
}
