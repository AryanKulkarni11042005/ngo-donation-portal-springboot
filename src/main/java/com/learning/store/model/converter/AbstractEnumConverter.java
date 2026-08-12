package com.learning.store.model.converter;

import jakarta.persistence.AttributeConverter;

public abstract class AbstractEnumConverter<T extends Enum<T>> implements AttributeConverter<T, String> {

    private final Class<T> enumClass;
    private final boolean storeLowercase;

    protected AbstractEnumConverter(Class<T> enumClass) {
        this(enumClass, true); // default: lowercase, matches Role/CampaignStatus/etc.
    }

    protected AbstractEnumConverter(Class<T> enumClass, boolean storeLowercase) {
        this.enumClass = enumClass;
        this.storeLowercase = storeLowercase;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (attribute == null) return null;
        return storeLowercase ? attribute.name().toLowerCase() : attribute.name();
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Enum.valueOf(enumClass, dbData.toUpperCase());
    }
}