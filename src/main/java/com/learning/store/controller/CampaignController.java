package com.learning.store.controller;

import com.learning.store.dto.CampaignSummaryDto;
import com.learning.store.entity.Campaign;
import com.learning.store.service.CampaignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {
    private final CampaignService campaignService;
    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }
    @GetMapping
    public List<CampaignSummaryDto> getAllCampaigns() {
        return campaignService.getAllCampaigns();
    }
    @GetMapping("/{id}")
    public CampaignSummaryDto getCampaignById(@PathVariable Integer id) {
        return campaignService.getCampaignById(id);
    }
}
