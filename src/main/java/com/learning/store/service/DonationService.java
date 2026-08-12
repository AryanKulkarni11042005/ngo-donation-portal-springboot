package com.learning.store.service;

import com.learning.store.dto.DonationSummaryDto;
import com.learning.store.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.learning.store.repository.DonationRepository;
import com.learning.store.entity.Donation;
import com.learning.store.entity.Campaign;
import com.learning.store.repository.CampaignRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignService campaignService;

    public DonationService(DonationRepository donationRepository,
                           CampaignRepository campaignRepository,
                           CampaignService campaignService) {
        this.donationRepository = donationRepository;
        this.campaignRepository = campaignRepository;
        this.campaignService = campaignService;
    }
    private DonationSummaryDto toDto(Donation donation) {
        return new DonationSummaryDto(
                donation.getId(),
                campaignService.toDto(donation.getCampaign()),
                donation.getDonorName(),
                donation.getDonorEmail(),
                donation.getDonorPhone(),
                donation.getAmount(),
                donation.getTransactionId(),
                donation.getPaymentStatus(),
                donation.getStatus(),
                donation.getCreatedAt()
        );
    }
    public List<DonationSummaryDto> getAllDonations() {
        return donationRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DonationSummaryDto getDonationById(Integer id) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation with id " + id + " not found"));
        return toDto(donation);
    }
    @Transactional
    public Donation createDonation(Donation donation) {
        Donation savedDonation = donationRepository.save(donation);
        Campaign campaign = savedDonation.getCampaign();
        campaign.setCurrentAmount(campaign.getCurrentAmount().add(savedDonation.getAmount()));
        campaignRepository.save(campaign);
        return savedDonation;
    }
}
