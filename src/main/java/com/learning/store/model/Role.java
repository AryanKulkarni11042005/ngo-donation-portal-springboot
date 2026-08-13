package com.learning.store.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ADMIN,VOLUNTEER;

    // The frontend and DB both use lowercase ("admin"), Java convention is uppercase.
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
