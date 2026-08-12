package com.learning.store.service;


import com.learning.store.dto.CampaignSummaryDto;
import com.learning.store.dto.UserSummaryDto;
import com.learning.store.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.learning.store.entity.Campaign;
import com.learning.store.repository.CampaignRepository;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }
    public CampaignSummaryDto toDto(Campaign campaign) {
        UserSummaryDto createdByDto = new UserSummaryDto(
                campaign.getCreatedBy().getId(),
                campaign.getCreatedBy().getName(),
                campaign.getCreatedBy().getEmail()
        );
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
    public List<CampaignSummaryDto> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public CampaignSummaryDto getCampaignById(Integer id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        return toDto(campaign);
    }

}
