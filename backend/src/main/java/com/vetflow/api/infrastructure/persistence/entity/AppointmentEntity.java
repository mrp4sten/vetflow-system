package com.vetflow.api.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import com.vetflow.api.infrastructure.persistence.converter.AppointmentPriorityConverter;
import com.vetflow.api.infrastructure.persistence.converter.AppointmentStatusConverter;
import com.vetflow.api.infrastructure.persistence.converter.AppointmentTypeConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "appointments", indexes = {
    @Index(name = "idx_appointments_patient_id", columnList = "patient_id"),
    @Index(name = "idx_appointments_date", columnList = "appointment_date"),
    @Index(name = "idx_appointments_status", columnList = "status"),
    @Index(name = "idx_appointments_priority", columnList = "priority"),
    @Index(name = "idx_appointments_date_status", columnList = "appointment_date,status")
})
public class AppointmentEntity {

  public enum Type {
    CHECKUP, VACCINATION, SURGERY, GROOMING, EMERGENCY
  }

  public enum Status {
    SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
  }

  public enum Priority {
    LOW, NORMAL, HIGH, CRITICAL
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "patient_id", nullable = false)
  private PatientEntity patient;

  @Column(name = "appointment_date", nullable = false)
  private LocalDateTime appointmentDate;

  @Convert(converter = AppointmentTypeConverter.class)
  @Column(name = "type", nullable = false, length = 50)
  private Type type;

  @Convert(converter = AppointmentStatusConverter.class)
  @Column(name = "status", nullable = false, length = 20)
  private Status status = Status.SCHEDULED;

  @Convert(converter = AppointmentPriorityConverter.class)
  @Column(name = "priority", nullable = false, length = 20)
  private Priority priority = Priority.NORMAL;

  @Column(name = "notes")
  private String notes;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  void prePersist() {
    if (createdAt == null)
      createdAt = LocalDateTime.now();
  }
}
