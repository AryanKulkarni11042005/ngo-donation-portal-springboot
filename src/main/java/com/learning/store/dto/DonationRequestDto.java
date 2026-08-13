package com.learning.store.dto;

import lombok.*;
import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
public class DonationRequestDto {
    private Integer campaignId;
    private String donorName;
    private String donorEmail;
    private String donorPhone;
    private BigDecimal amount;
    private String transactionId;
}
