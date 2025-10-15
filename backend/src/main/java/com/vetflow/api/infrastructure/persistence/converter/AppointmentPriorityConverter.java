package com.vetflow.api.infrastructure.persistence.converter;

import java.util.Locale;

import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Priority;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AppointmentPriorityConverter implements AttributeConverter<Priority, String> {
    @Override
    public String convertToDatabaseColumn(Priority attribute) {
        return attribute == null ? null : attribute.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Priority convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Priority.valueOf(dbData.toUpperCase(Locale.ROOT));
    }
}
