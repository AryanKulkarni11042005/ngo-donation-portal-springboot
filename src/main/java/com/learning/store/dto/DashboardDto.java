package com.learning.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.learning.store.model.CampaignStatus;
import com.learning.store.model.DonationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardDto {

    private Totals totals;
    // The frontend reads these two keys as camelCase, unlike the rest of the API.
    @JsonProperty("recentDonations")
    private List<RecentDonation> recentDonations;
    @JsonProperty("recentCampaigns")
    private List<RecentCampaign> recentCampaigns;

    @Getter
    @AllArgsConstructor
    public static class Totals {
        @JsonProperty("totalDonations")
        private long totalDonations;
        @JsonProperty("totalDonors")
        private long totalDonors;
        @JsonProperty("activeCampaigns")
        private long activeCampaigns;
        @JsonProperty("pendingDonations")
        private long pendingDonations;
    }

    @Getter
    @AllArgsConstructor
    public static class RecentDonation {
        private Integer id;
        private String donorName;
        private String campaignTitle;
        private BigDecimal amount;
        private DonationStatus status;
        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    public static class RecentCampaign {
        private Integer id;
        private String title;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private CampaignStatus status;
    }
}
