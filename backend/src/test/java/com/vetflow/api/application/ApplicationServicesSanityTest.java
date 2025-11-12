package com.vetflow.api.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vetflow.api.ApiApplication;
import com.vetflow.api.application.appointment.AppointmentApplicationService;
import com.vetflow.api.application.medicalrecord.MedicalRecordApplicationService;
import com.vetflow.api.application.owner.OwnerApplicationService;
import com.vetflow.api.application.patient.PatientApplicationService;

@SpringBootTest(classes = ApiApplication.class)
class ApplicationServicesSanityTest {

  @Autowired
  private OwnerApplicationService ownerApplicationService;
  @Autowired
  private PatientApplicationService patientApplicationService;
  @Autowired
  private AppointmentApplicationService appointmentApplicationService;
  @Autowired
  private MedicalRecordApplicationService medicalRecordApplicationService;

  @Test
  void servicesAreAutowired() {
    assertThat(ownerApplicationService).isNotNull();
    assertThat(patientApplicationService).isNotNull();
    assertThat(appointmentApplicationService).isNotNull();
    assertThat(medicalRecordApplicationService).isNotNull();
  }
}