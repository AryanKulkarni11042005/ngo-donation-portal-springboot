package com.learning.store.service;

import com.learning.store.dto.CertificateDetailsDto;
import com.learning.store.entity.Certificate;
import com.learning.store.entity.Donation;
import com.learning.store.exception.ResourceNotFoundException;
import com.learning.store.repository.CertificateRepository;
import com.learning.store.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final DonationService donationService;

    public CertificateService(CertificateRepository certificateRepository,
                              DonationService donationService) {
        this.certificateRepository = certificateRepository;
        this.donationService = donationService;
    }

    /**
     * Certificates are issued lazily: the first download for a donation creates
     * one, later downloads reuse it so the code stays stable.
     */
    @Transactional
    public CertificateDetailsDto getOrCreateForDonation(Integer donationId) {
        Donation donation = donationService.findEntityById(donationId);
        Certificate certificate = certificateRepository.findByDonationId(donationId)
                .orElseGet(() -> {
                    Certificate created = new Certificate();
                    created.setDonation(donation);
                    created.setCertificateCode(IdGenerator.certificateCode());
                    created.setVerificationId(IdGenerator.verificationId());
                    return certificateRepository.save(created);
                });
        return toDto(certificate, donation);
    }

    @Transactional(readOnly = true)
    public CertificateDetailsDto getByVerificationId(String verificationId) {
        Certificate certificate = certificateRepository.findByVerificationId(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        return toDto(certificate, certificate.getDonation());
    }

    private CertificateDetailsDto toDto(Certificate certificate, Donation donation) {
        return new CertificateDetailsDto(
                certificate.getCertificateCode(),
                certificate.getVerificationId(),
                donation.getDonorName(),
                donation.getCampaign() != null ? donation.getCampaign().getTitle() : "Unknown Campaign",
                donation.getAmount(),
                donation.getTransactionId(),
                donation.getCreatedAt()
        );
    }
}
