package com.learning.store.service;


import com.learning.store.dto.CampaignRequestDto;
import com.learning.store.dto.CampaignSummaryDto;
import com.learning.store.dto.UserSummaryDto;
import com.learning.store.exception.ConflictException;
import com.learning.store.exception.ResourceNotFoundException;
import com.learning.store.repository.DonationRepository;
import com.learning.store.model.CampaignStatus;
import com.learning.store.entity.User;
import org.springframework.stereotype.Service;
import com.learning.store.entity.Campaign;
import com.learning.store.repository.CampaignRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final DonationRepository donationRepository;

    public CampaignService(CampaignRepository campaignRepository,
                           DonationRepository donationRepository) {
        this.campaignRepository = campaignRepository;
        this.donationRepository = donationRepository;
    }
    public CampaignSummaryDto toDto(Campaign campaign) {
        // created_by is nullable in the schema, so a campaign can have no author.
        UserSummaryDto createdByDto = null;
        if (campaign.getCreatedBy() != null) {
            createdByDto = new UserSummaryDto(
                    campaign.getCreatedBy().getId(),
                    campaign.getCreatedBy().getName(),
                    campaign.getCreatedBy().getEmail()
            );
        }
        return new CampaignSummaryDto(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getTargetAmount(),
                campaign.getCurrentAmount(),
                campaign.getStatus(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                createdByDto,
                campaign.getCreatedAt(),
                campaign.getUpdatedAt()
        );
    }
    public List<CampaignSummaryDto> getAllCampaigns(String search) {
        List<Campaign> campaigns = (search == null || search.isBlank())
                ? campaignRepository.findAll()
                : campaignRepository.findByTitleContainingIgnoreCase(search.trim());
        return campaigns.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public CampaignSummaryDto getCampaignById(Integer id) {
        return toDto(findEntityById(id));
    }

    public Campaign findEntityById(Integer id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
    }

    @Transactional
    public CampaignSummaryDto createCampaign(CampaignRequestDto dto, User creator) {
        Campaign campaign = new Campaign();
        campaign.setTitle(dto.getTitle());
        campaign.setDescription(dto.getDescription());
        campaign.setTargetAmount(dto.getTargetAmount());
        campaign.setCurrentAmount(java.math.BigDecimal.ZERO);
        campaign.setStatus(dto.getStatus() != null ? dto.getStatus() : CampaignStatus.ACTIVE);
        campaign.setStartDate(dto.getStartDate());
        campaign.setEndDate(dto.getEndDate());
        campaign.setCreatedBy(creator);
        return toDto(campaignRepository.save(campaign));
    }

    @Transactional
    public void deleteCampaign(Integer id) {
        Campaign campaign = findEntityById(id);
        // donations.campaign_id is NOT NULL, so a campaign with donations cannot go away
        // without losing that history. Refuse instead of cascading.
        long donations = donationRepository.countByCampaignId(id);
        if (donations > 0) {
            throw new ConflictException(
                    "Campaign has " + donations + " donation(s) and cannot be deleted");
        }
        campaignRepository.delete(campaign);
    }

    @Transactional
    public CampaignSummaryDto updateCampaign(Integer id, CampaignRequestDto dto) {
        Campaign campaign = findEntityById(id);
        campaign.setTitle(dto.getTitle());
        campaign.setDescription(dto.getDescription());
        campaign.setTargetAmount(dto.getTargetAmount());
        if (dto.getStatus() != null) {
            campaign.setStatus(dto.getStatus());
        }
        campaign.setStartDate(dto.getStartDate());
        campaign.setEndDate(dto.getEndDate());
        return toDto(campaignRepository.save(campaign));
    }
}
