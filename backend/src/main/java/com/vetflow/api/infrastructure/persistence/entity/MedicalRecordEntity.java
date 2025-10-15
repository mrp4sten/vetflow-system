package com.vetflow.api.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "medical_records", indexes = {
    @Index(name = "idx_medical_records_patient_id", columnList = "patient_id"),
    @Index(name = "idx_medical_records_visit_date", columnList = "visit_date"),
    @Index(name = "idx_medical_records_veterinarian", columnList = "veterinarian_id")
})
public class MedicalRecordEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "patient_id", nullable = false)
  private PatientEntity patient;

  @Column(name = "veterinarian_id", nullable = false)
  private Long veterinarianId;

  @Column(name = "visit_date", nullable = false)
  private LocalDateTime visitDate = LocalDateTime.now();

  @Column(name = "diagnosis", nullable = false, columnDefinition = "TEXT")
  private String diagnosis;

  @Column(name = "treatment", columnDefinition = "TEXT")
  private String treatment;

  @Column(name = "medications", columnDefinition = "TEXT")
  private String medications;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  @PrePersist
  void prePersist() {
    if (createdAt == null)
      createdAt = LocalDateTime.now();
  }
}
