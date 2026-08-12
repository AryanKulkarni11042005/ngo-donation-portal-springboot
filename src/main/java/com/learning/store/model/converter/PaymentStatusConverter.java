package com.learning.store.model.converter;

import com.learning.store.model.PaymentStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter extends AbstractEnumConverter<PaymentStatus> {
    public PaymentStatusConverter() {
        super(PaymentStatus.class, false); // false = keep uppercase in DB
    }
}