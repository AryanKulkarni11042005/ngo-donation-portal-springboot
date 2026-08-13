package com.learning.store.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CampaignStatus {
    ACTIVE,CLOSED,DRAFT;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static CampaignStatus fromJson(String value) {
        return value == null ? null : CampaignStatus.valueOf(value.toUpperCase());
    }
}
