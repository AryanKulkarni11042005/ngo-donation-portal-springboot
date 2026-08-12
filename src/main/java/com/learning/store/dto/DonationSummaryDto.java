package com.learning.store.dto;

import com.learning.store.model.DonationStatus;
import com.learning.store.model.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DonationSummaryDto {
        private Integer id;
        private CampaignSummaryDto campaign;
        private String donorName;
        private String donorEmail;
        private String donorPhone;
        private BigDecimal amount;
        private String transactionId;
        private PaymentStatus paymentStatus;
        private DonationStatus status;
        private LocalDateTime createdAt;
}

