package com.learning.store.service;

import com.learning.store.dto.DashboardDto;
import com.learning.store.entity.Campaign;
import com.learning.store.entity.Donation;
import com.learning.store.model.CampaignStatus;
import com.learning.store.model.DonationStatus;
import com.learning.store.repository.CampaignRepository;
import com.learning.store.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;

    public DashboardService(DonationRepository donationRepository,
                            CampaignRepository campaignRepository) {
        this.donationRepository = donationRepository;
        this.campaignRepository = campaignRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboardData() {
        DashboardDto.Totals totals = new DashboardDto.Totals(
                donationRepository.count(),
                donationRepository.countDistinctDonorEmail(),
                campaignRepository.countByStatus(CampaignStatus.ACTIVE),
                donationRepository.countByStatus(DonationStatus.PENDING)
        );

        List<DashboardDto.RecentDonation> recentDonations =
                donationRepository.findTop5ByOrderByCreatedAtDesc().stream()
                        .map(this::toRecentDonation)
                        .collect(Collectors.toList());

        List<DashboardDto.RecentCampaign> recentCampaigns =
                campaignRepository.findTop5ByOrderByCreatedAtDesc().stream()
                        .map(this::toRecentCampaign)
                        .collect(Collectors.toList());

        return new DashboardDto(totals, recentDonations, recentCampaigns);
    }

    private DashboardDto.RecentDonation toRecentDonation(Donation donation) {
        return new DashboardDto.RecentDonation(
                donation.getId(),
                donation.getDonorName(),
                donation.getCampaign() != null ? donation.getCampaign().getTitle() : null,
                donation.getAmount(),
                donation.getStatus(),
                donation.getCreatedAt()
        );
    }

    private DashboardDto.RecentCampaign toRecentCampaign(Campaign campaign) {
        return new DashboardDto.RecentCampaign(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getTargetAmount(),
                campaign.getCurrentAmount(),
                campaign.getStatus()
        );
    }
}
