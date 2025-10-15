package com.vetflow.api.infrastructure.persistence.converter;

import java.util.Locale;

import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Status;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AppointmentStatusConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status attribute) {
        return attribute == null ? null : attribute.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Status.valueOf(dbData.toUpperCase(Locale.ROOT));
    }
}
