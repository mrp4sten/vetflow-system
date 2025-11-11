package com.vetflow.api.infrastructure.persistence.mapper;

import java.util.Locale;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vetflow.api.domain.model.Appointment;
import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity;

@Mapper(componentModel = "spring", uses = { PatientMapper.class })
public interface AppointmentMapper {

    // Domain -> Entity
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "type", expression = "java(toEntityType(domain.getType()))")
    @Mapping(target = "status", expression = "java(toEntityStatus(domain.getStatus()))")
    @Mapping(target = "priority", expression = "java(toEntityPriority(domain.getPriority()))")
    AppointmentEntity toEntity(Appointment domain);

    // Entity -> Domain
    @InheritInverseConfiguration
    Appointment toDomain(AppointmentEntity entity);

    // ==========================
    // Enum helpers (null-safe)
    // ==========================
    default AppointmentEntity.Type toEntityType(Appointment.Type type) {
        if (type == null) return null;
        // misma constante que en el entity
        return AppointmentEntity.Type.valueOf(type.name());
    }

    default AppointmentEntity.Status toEntityStatus(Appointment.Status status) {
        if (status == null) return null;
        return AppointmentEntity.Status.valueOf(status.name());
    }

    default AppointmentEntity.Priority toEntityPriority(Appointment.Priority priority) {
        if (priority == null) return null;
        return AppointmentEntity.Priority.valueOf(priority.name());
    }

    default Appointment.Type toDomainType(AppointmentEntity.Type type) {
        if (type == null) return null;
        return Appointment.Type.valueOf(type.name());
    }

    default Appointment.Status toDomainStatus(AppointmentEntity.Status status) {
        if (status == null) return null;
        return Appointment.Status.valueOf(status.name());
    }

    default Appointment.Priority toDomainPriority(AppointmentEntity.Priority priority) {
        if (priority == null) return null;
        return Appointment.Priority.valueOf(priority.name());
    }

    // ======== lo que pide el acceptance: enum -> lowercase string ========
    default String toLower(Appointment.Type type) {
        return type == null ? null : type.name().toLowerCase(Locale.ROOT);
    }

    default String toLower(Appointment.Status status) {
        return status == null ? null : status.name().toLowerCase(Locale.ROOT);
    }

    default String toLower(Appointment.Priority priority) {
        return priority == null ? null : priority.name().toLowerCase(Locale.ROOT);
    }
}
