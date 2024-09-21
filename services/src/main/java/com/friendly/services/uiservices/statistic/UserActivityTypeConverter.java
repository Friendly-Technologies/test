package com.friendly.services.uiservices.statistic;

import com.friendly.commons.models.reports.UserActivityType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserActivityTypeConverter implements AttributeConverter<UserActivityType, String> {

    @Override
    public String convertToDatabaseColumn(UserActivityType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getName();
    }

    @Override
    public UserActivityType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return UserActivityType.byName(dbData);
    }
}

