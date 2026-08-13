package com.learning.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CertificateDetailsDto {
    private String certificateId;
    private String verificationId;
    private String donorName;
    private String campaignTitle;
    private BigDecimal amount;
    private String transactionId;
    private LocalDateTime date;
}
