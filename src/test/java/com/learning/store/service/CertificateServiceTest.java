package com.learning.store.service;

import com.learning.store.dto.CertificateDetailsDto;
import com.learning.store.entity.Campaign;
import com.learning.store.entity.Certificate;
import com.learning.store.entity.Donation;
import com.learning.store.exception.ResourceNotFoundException;
import com.learning.store.repository.CertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private DonationService donationService;

    @InjectMocks
    private CertificateService certificateService;

    private Donation donation;

    @BeforeEach
    void setUp() {
        Campaign campaign = new Campaign();
        campaign.setId(10);
        campaign.setTitle("Clean Water");

        donation = new Donation();
        donation.setId(5);
        donation.setCampaign(campaign);
        donation.setDonorName("Asha");
        donation.setAmount(new BigDecimal("250.00"));
        donation.setTransactionId("TXN-20260101-ABCD1234");
    }

    @Test
    @DisplayName("The first download issues a certificate for the donation")
    void getOrCreateIssuesCertificateOnFirstCall() {
        when(donationService.findEntityById(5)).thenReturn(donation);
        when(certificateRepository.findByDonationId(5)).thenReturn(Optional.empty());
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(inv -> inv.getArgument(0));

        CertificateDetailsDto result = certificateService.getOrCreateForDonation(5);

        assertThat(result.getCertificateId()).startsWith("CERT-");
        assertThat(result.getVerificationId()).startsWith("VER-");
        assertThat(result.getDonorName()).isEqualTo("Asha");
        assertThat(result.getCampaignTitle()).isEqualTo("Clean Water");
        verify(certificateRepository).save(any(Certificate.class));
    }

    @Test
    @DisplayName("A later download reuses the existing certificate, so the code stays stable")
    void getOrCreateReusesExistingCertificate() {
        Certificate existing = new Certificate();
        existing.setId(7);
        existing.setDonation(donation);
        existing.setCertificateCode("CERT-20260101-EXISTING");
        existing.setVerificationId("VER-EXISTING01");

        when(donationService.findEntityById(5)).thenReturn(donation);
        when(certificateRepository.findByDonationId(5)).thenReturn(Optional.of(existing));

        CertificateDetailsDto result = certificateService.getOrCreateForDonation(5);

        assertThat(result.getCertificateId()).isEqualTo("CERT-20260101-EXISTING");
        assertThat(result.getVerificationId()).isEqualTo("VER-EXISTING01");
        verify(certificateRepository, never()).save(any());
    }

    @Test
    @DisplayName("A donation whose campaign is gone still yields a certificate with a placeholder title")
    void getOrCreateHandlesMissingCampaign() {
        donation.setCampaign(null);
        when(donationService.findEntityById(5)).thenReturn(donation);
        when(certificateRepository.findByDonationId(5)).thenReturn(Optional.empty());
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(inv -> inv.getArgument(0));

        CertificateDetailsDto result = certificateService.getOrCreateForDonation(5);

        assertThat(result.getCampaignTitle()).isEqualTo("Unknown Campaign");
    }

    @Test
    @DisplayName("Verifying a certificate returns the donation behind it")
    void getByVerificationIdReturnsDetails() {
        Certificate existing = new Certificate();
        existing.setDonation(donation);
        existing.setCertificateCode("CERT-20260101-ABCD1234");
        existing.setVerificationId("VER-ABCDEFGHIJ");
        when(certificateRepository.findByVerificationId("VER-ABCDEFGHIJ")).thenReturn(Optional.of(existing));

        CertificateDetailsDto result = certificateService.getByVerificationId("VER-ABCDEFGHIJ");

        assertThat(result.getDonorName()).isEqualTo("Asha");
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
    }

    @Test
    @DisplayName("An unknown verification id is reported as not found")
    void getByVerificationIdThrowsWhenMissing() {
        when(certificateRepository.findByVerificationId("VER-NOPE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificateService.getByVerificationId("VER-NOPE"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Certificate not found");
    }
}
