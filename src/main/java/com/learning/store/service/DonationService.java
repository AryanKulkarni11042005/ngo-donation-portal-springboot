package com.learning.store.service;

import com.learning.store.dto.DonationRequestDto;
import com.learning.store.dto.DonationSummaryDto;
import com.learning.store.exception.ResourceNotFoundException;
import com.learning.store.model.DonationStatus;
import com.learning.store.model.PaymentStatus;
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
    public DonationSummaryDto createDonation(DonationRequestDto dto) {
        Campaign campaign = campaignRepository.findById(dto.getCampaignId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign with id " + dto.getCampaignId() + " not found"));
        Donation donation = new Donation();
        donation.setCampaign(campaign);
        donation.setDonorName(dto.getDonorName());
        donation.setDonorEmail(dto.getDonorEmail());
        donation.setDonorPhone(dto.getDonorPhone());
        donation.setAmount(dto.getAmount());
        donation.setTransactionId(dto.getTransactionId());
        Donation savedDonation = donationRepository.save(donation);
        campaign.setCurrentAmount(campaign.getCurrentAmount().add(savedDonation.getAmount()));
        donation.setPaymentStatus(PaymentStatus.SUCCESS);
        donation.setStatus(DonationStatus.PENDING);
        campaignRepository.save(campaign);
        return toDto(savedDonation);
    }
}
