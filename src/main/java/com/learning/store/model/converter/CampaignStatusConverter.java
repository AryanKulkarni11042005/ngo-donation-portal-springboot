package com.learning.store.model.converter;

import com.learning.store.model.CampaignStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CampaignStatusConverter extends AbstractEnumConverter<CampaignStatus> {
    public CampaignStatusConverter() {
        super(CampaignStatus.class);
    }
}