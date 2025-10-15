package com.vetflow.api.infrastructure.persistence.converter;

import java.util.Locale;

import com.vetflow.api.infrastructure.persistence.entity.AppointmentEntity.Type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AppointmentTypeConverter implements AttributeConverter<Type, String> {
    @Override
    public String convertToDatabaseColumn(Type attribute) {
        return attribute == null ? null : attribute.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Type convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Type.valueOf(dbData.toUpperCase(Locale.ROOT));
    }
}
