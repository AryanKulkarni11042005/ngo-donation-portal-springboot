package com.learning.store.model.converter;

import com.learning.store.model.DonationStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DonationStatusConverter extends AbstractEnumConverter<DonationStatus> {
    public DonationStatusConverter() {
        super(DonationStatus.class);
    }
}