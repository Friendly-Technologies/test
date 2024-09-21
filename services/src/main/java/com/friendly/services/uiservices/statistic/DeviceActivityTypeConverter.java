package com.friendly.services.uiservices.statistic;

import com.friendly.commons.models.reports.DeviceActivityType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DeviceActivityTypeConverter implements AttributeConverter<DeviceActivityType, String> {
    @Override
    public String convertToDatabaseColumn(DeviceActivityType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getName();
    }

    @Override
    public DeviceActivityType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return DeviceActivityType.byName(dbData);
    }
}
