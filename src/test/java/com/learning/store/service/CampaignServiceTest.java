package com.learning.store.service;

import com.learning.store.dto.CampaignRequestDto;
import com.learning.store.dto.CampaignSummaryDto;
import com.learning.store.entity.Campaign;
import com.learning.store.entity.User;
import com.learning.store.exception.ConflictException;
import com.learning.store.exception.ResourceNotFoundException;
import com.learning.store.model.CampaignStatus;
import com.learning.store.model.Role;
import com.learning.store.repository.CampaignRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private CampaignService campaignService;

    private User creator;
    private Campaign campaign;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1);
        creator.setName("Admin");
        creator.setEmail("admin@ngo.org");
        creator.setRole(Role.ADMIN);

        campaign = new Campaign();
        campaign.setId(10);
        campaign.setTitle("Clean Water");
        campaign.setDescription("Wells for rural villages");
        campaign.setTargetAmount(new BigDecimal("50000.00"));
        campaign.setCurrentAmount(new BigDecimal("1200.00"));
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setStartDate(LocalDate.of(2026, 1, 1));
        campaign.setEndDate(LocalDate.of(2026, 12, 31));
        campaign.setCreatedBy(creator);
    }

    private CampaignRequestDto request(String title, CampaignStatus status) {
        CampaignRequestDto dto = new CampaignRequestDto();
        dto.setTitle(title);
        dto.setDescription("A description");
        dto.setTargetAmount(new BigDecimal("50000.00"));
        dto.setStatus(status);
        dto.setStartDate(LocalDate.of(2026, 1, 1));
        dto.setEndDate(LocalDate.of(2026, 12, 31));
        return dto;
    }

    @Test
    @DisplayName("A blank search returns every campaign rather than filtering on an empty string")
    void getAllCampaignsWithoutSearchReturnsAll() {
        when(campaignRepository.findAll()).thenReturn(List.of(campaign));

        List<CampaignSummaryDto> result = campaignService.getAllCampaigns("   ");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Clean Water");
        verify(campaignRepository, never()).findByTitleContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("A search term is trimmed before it reaches the repository")
    void getAllCampaignsWithSearchTrimsTerm() {
        when(campaignRepository.findByTitleContainingIgnoreCase("water")).thenReturn(List.of(campaign));

        List<CampaignSummaryDto> result = campaignService.getAllCampaigns("  water  ");

        assertThat(result).hasSize(1);
        verify(campaignRepository, never()).findAll();
    }

    @Test
    @DisplayName("A campaign with no author still maps to a DTO, since created_by is nullable")
    void toDtoHandlesMissingCreator() {
        campaign.setCreatedBy(null);
        when(campaignRepository.findById(10)).thenReturn(Optional.of(campaign));

        CampaignSummaryDto result = campaignService.getCampaignById(10);

        assertThat(result.getCreatedBy()).isNull();
        assertThat(result.getTitle()).isEqualTo("Clean Water");
    }

    @Test
    @DisplayName("An unknown campaign id is reported as not found")
    void getCampaignByIdThrowsWhenMissing() {
        when(campaignRepository.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> campaignService.getCampaignById(404))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("404");
    }

    @Test
    @DisplayName("A new campaign starts at zero raised and defaults to ACTIVE")
    void createCampaignDefaultsStatusAndAmount() {
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(inv -> inv.getArgument(0));

        campaignService.createCampaign(request("New Campaign", null), creator);

        ArgumentCaptor<Campaign> saved = ArgumentCaptor.forClass(Campaign.class);
        verify(campaignRepository).save(saved.capture());
        assertThat(saved.getValue().getStatus()).isEqualTo(CampaignStatus.ACTIVE);
        assertThat(saved.getValue().getCurrentAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getValue().getCreatedBy()).isSameAs(creator);
    }

    @Test
    @DisplayName("An explicit status on create is preserved")
    void createCampaignKeepsExplicitStatus() {
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(inv -> inv.getArgument(0));

        campaignService.createCampaign(request("Draft Campaign", CampaignStatus.DRAFT), creator);

        ArgumentCaptor<Campaign> saved = ArgumentCaptor.forClass(Campaign.class);
        verify(campaignRepository).save(saved.capture());
        assertThat(saved.getValue().getStatus()).isEqualTo(CampaignStatus.DRAFT);
    }

    @Test
    @DisplayName("Updating without a status leaves the existing one untouched")
    void updateCampaignKeepsStatusWhenNotSupplied() {
        when(campaignRepository.findById(10)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(inv -> inv.getArgument(0));

        campaignService.updateCampaign(10, request("Renamed", null));

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.ACTIVE);
        assertThat(campaign.getTitle()).isEqualTo("Renamed");
    }

    @Test
    @DisplayName("A campaign with donations cannot be deleted, so the history survives")
    void deleteCampaignWithDonationsThrowsConflict() {
        when(campaignRepository.findById(10)).thenReturn(Optional.of(campaign));
        when(donationRepository.countByCampaignId(10)).thenReturn(3L);

        assertThatThrownBy(() -> campaignService.deleteCampaign(10))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("3 donation(s)");

        verify(campaignRepository, never()).delete(any());
    }

    @Test
    @DisplayName("A campaign with no donations is deleted")
    void deleteCampaignWithoutDonationsSucceeds() {
        when(campaignRepository.findById(10)).thenReturn(Optional.of(campaign));
        when(donationRepository.countByCampaignId(10)).thenReturn(0L);

        campaignService.deleteCampaign(10);

        verify(campaignRepository).delete(campaign);
    }
}
