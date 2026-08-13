package com.learning.store.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DonationStatus {
    PENDING,VERIFIED,REJECTED;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static DonationStatus fromJson(String value) {
        return value == null ? null : DonationStatus.valueOf(value.toUpperCase());
    }
}
