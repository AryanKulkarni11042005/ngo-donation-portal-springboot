package com.learning.store.service;

import com.learning.store.dto.DonationRequestDto;
import com.learning.store.dto.DonationSummaryDto;
import com.learning.store.entity.Campaign;
import com.learning.store.entity.Certificate;
import com.learning.store.entity.Donation;
import com.learning.store.exception.ResourceNotFoundException;
import com.learning.store.model.DonationStatus;
import com.learning.store.model.PaymentStatus;
import com.learning.store.repository.CampaignRepository;
import com.learning.store.repository.CertificateRepository;
import com.learning.store.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private DonationService donationService;

    private Campaign campaign;
    private Donation donation;

    @BeforeEach
    void setUp() {
        campaign = new Campaign();
        campaign.setId(10);
        campaign.setTitle("Clean Water");
        campaign.setCurrentAmount(new BigDecimal("1000.00"));

        donation = new Donation();
        donation.setId(5);
        donation.setCampaign(campaign);
        donation.setDonorName("Asha");
        donation.setDonorEmail("asha@example.com");
        donation.setDonorPhone("9876543210");
        donation.setAmount(new BigDecimal("250.00"));
        donation.setTransactionId("TXN-20260101-ABCD1234");
        donation.setPaymentStatus(PaymentStatus.SUCCESS);
        donation.setStatus(DonationStatus.PENDING);
    }

    private DonationRequestDto request(String transactionId) {
        DonationRequestDto dto = new DonationRequestDto();
        dto.setCampaignId(10);
        dto.setDonorName("Asha");
        dto.setDonorEmail("asha@example.com");
        dto.setDonorPhone("9876543210");
        dto.setAmount(new BigDecimal("250.00"));
        dto.setTransactionId(transactionId);
        return dto;
    }

    @Test
    @DisplayName("A donation without a transaction id gets a generated one")
    void createDonationGeneratesTransactionIdWhenBlank() {
        when(campaignRepository.findById(10)).thenReturn(Optional.of(campaign));
        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));

        donationService.createDonation(request("   "));

        ArgumentCaptor<Donation> saved = ArgumentCaptor.forClass(Donation.class);
        verify(donationRepository).save(saved.capture());
        assertThat(saved.getValue().getTransactionId()).startsWith("TXN-");
    }

    @Test
    @DisplayName("A client-supplied transaction id is kept as-is")
    void createDonationKeepsSuppliedTransactionId() {
        when(campaignRepository.findById(10)).thenReturn(Optional.of(campaign));
        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));

        donationService.createDonation(request("TXN-CUSTOM-0001"));

        ArgumentCaptor<Donation> saved = ArgumentCaptor.forClass(Donation.class);
        verify(donationRepository).save(saved.capture());
        assertThat(saved.getValue().getTransactionId()).isEqualTo("TXN-CUSTOM-0001");
    }

    @Test
    @DisplayName("A new donation starts PENDING with a successful payment, since both columns are NOT NULL")
    void createDonationSetsRequiredStatuses() {
        when(campaignRepository.findById(10)).thenReturn(Optional.of(campaign));
        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));

        donationService.createDonation(request(null));

        ArgumentCaptor<Donation> saved = ArgumentCaptor.forClass(Donation.class);
        verify(donationRepository).save(saved.capture());
        assertThat(saved.getValue().getStatus()).isEqualTo(DonationStatus.PENDING);
        assertThat(saved.getValue().getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("Donating raises the campaign total by the donated amount")
    void createDonationIncrementsCampaignTotal() {
        when(campaignRepository.findById(10)).thenReturn(Optional.of(campaign));
        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));

        donationService.createDonation(request(null));

        assertThat(campaign.getCurrentAmount()).isEqualByComparingTo(new BigDecimal("1250.00"));
        verify(campaignRepository).save(campaign);
    }

    @Test
    @DisplayName("Donating to a campaign that does not exist is reported as not found")
    void createDonationThrowsWhenCampaignMissing() {
        when(campaignRepository.findById(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> donationService.createDonation(request(null)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("10");

        verify(donationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deleting a donation removes its certificate first, because the certificate references it")
    void deleteDonationRemovesCertificateFirst() {
        Certificate certificate = new Certificate();
        certificate.setId(7);
        certificate.setDonation(donation);
        when(donationRepository.findById(5)).thenReturn(Optional.of(donation));
        when(certificateRepository.findByDonationId(5)).thenReturn(Optional.of(certificate));

        donationService.deleteDonation(5);

        verify(certificateRepository).delete(certificate);
        verify(donationRepository).delete(donation);
    }

    @Test
    @DisplayName("Deleting a donation lowers the campaign total so it matches the donations that remain")
    void deleteDonationDecrementsCampaignTotal() {
        when(donationRepository.findById(5)).thenReturn(Optional.of(donation));
        when(certificateRepository.findByDonationId(5)).thenReturn(Optional.empty());

        donationService.deleteDonation(5);

        assertThat(campaign.getCurrentAmount()).isEqualByComparingTo(new BigDecimal("750.00"));
        verify(campaignRepository).save(campaign);
    }

    @Test
    @DisplayName("Updating a donation status persists the new value")
    void updateStatusSavesNewStatus() {
        when(donationRepository.findById(5)).thenReturn(Optional.of(donation));
        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));

        DonationSummaryDto result = donationService.updateStatus(5, DonationStatus.VERIFIED);

        assertThat(result.getStatus()).isEqualTo(DonationStatus.VERIFIED);
        assertThat(donation.getStatus()).isEqualTo(DonationStatus.VERIFIED);
    }

    @Test
    @DisplayName("An unknown donation id is reported as not found")
    void findEntityByIdThrowsWhenMissing() {
        when(donationRepository.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> donationService.getDonationById(404))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("404");
    }
}
