package com.learning.store.service;

import com.learning.store.dto.DonationRequestDto;
import com.learning.store.dto.DonationSummaryDto;
import com.learning.store.exception.ResourceNotFoundException;
import com.learning.store.model.DonationStatus;
import com.learning.store.model.PaymentStatus;
import com.learning.store.util.IdGenerator;
import org.springframework.stereotype.Service;
import com.learning.store.repository.DonationRepository;
import com.learning.store.entity.Donation;
import com.learning.store.entity.Campaign;
import com.learning.store.repository.CampaignRepository;
import com.learning.store.repository.CertificateRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final CertificateRepository certificateRepository;

    public DonationService(DonationRepository donationRepository,
                           CampaignRepository campaignRepository,
                           CertificateRepository certificateRepository) {
        this.donationRepository = donationRepository;
        this.campaignRepository = campaignRepository;
        this.certificateRepository = certificateRepository;
    }

    public DonationSummaryDto toDto(Donation donation) {
        Campaign campaign = donation.getCampaign();
        return new DonationSummaryDto(
                donation.getId(),
                campaign != null ? campaign.getId() : null,
                campaign != null ? campaign.getTitle() : null,
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

    public List<DonationSummaryDto> getAllDonations(String search, DonationStatus status) {
        String normalised = (search == null || search.isBlank()) ? null : search.trim();
        return donationRepository.search(normalised, status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DonationSummaryDto getDonationById(Integer id) {
        return toDto(findEntityById(id));
    }

    public Donation findEntityById(Integer id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation with id " + id + " not found"));
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
        donation.setTransactionId(
                dto.getTransactionId() != null && !dto.getTransactionId().isBlank()
                        ? dto.getTransactionId()
                        : IdGenerator.transactionId());
        // payment_status and status are NOT NULL in the schema, so they must be set
        // before the insert rather than after save().
        donation.setPaymentStatus(PaymentStatus.SUCCESS);
        donation.setStatus(DonationStatus.PENDING);

        Donation savedDonation = donationRepository.save(donation);
        campaign.setCurrentAmount(campaign.getCurrentAmount().add(savedDonation.getAmount()));
        campaignRepository.save(campaign);
        return toDto(savedDonation);
    }

    @Transactional
    public DonationSummaryDto updateStatus(Integer id, DonationStatus status) {
        Donation donation = findEntityById(id);
        donation.setStatus(status);
        return toDto(donationRepository.save(donation));
    }

    @Transactional
    public void deleteDonation(Integer id) {
        Donation donation = findEntityById(id);
        Campaign campaign = donation.getCampaign();

        // A certificate references the donation, so it has to go first.
        certificateRepository.findByDonationId(id).ifPresent(certificateRepository::delete);
        donationRepository.delete(donation);

        // Keep the campaign total consistent with the donations that remain.
        if (campaign != null && donation.getAmount() != null) {
            campaign.setCurrentAmount(campaign.getCurrentAmount().subtract(donation.getAmount()));
            campaignRepository.save(campaign);
        }
    }
}
