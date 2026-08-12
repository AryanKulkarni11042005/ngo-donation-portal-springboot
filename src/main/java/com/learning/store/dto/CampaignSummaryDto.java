package com.learning.store.dto;

import com.learning.store.model.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CampaignSummaryDto {

    private Integer id;
    private String title;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private CampaignStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private UserSummaryDto createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
