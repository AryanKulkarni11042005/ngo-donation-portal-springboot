package com.learning.store.controller;

import com.learning.store.dto.CampaignRequestDto;
import com.learning.store.dto.CampaignSummaryDto;
import com.learning.store.entity.User;
import com.learning.store.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {
    private final CampaignService campaignService;
    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }
    @GetMapping
    public Map<String, Object> getAllCampaigns(@RequestParam(required = false) String search) {
        return Map.of("campaigns", campaignService.getAllCampaigns(search));
    }
    @GetMapping("/{id}")
    public Map<String, Object> getCampaignById(@PathVariable Integer id) {
        return Map.of("campaign", campaignService.getCampaignById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCampaign(
            @Valid @RequestBody CampaignRequestDto dto,
            @AuthenticationPrincipal User creator) {
        CampaignSummaryDto created = campaignService.createCampaign(dto, creator);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("campaign", created));
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateCampaign(@PathVariable Integer id,
                                              @Valid @RequestBody CampaignRequestDto dto) {
        return Map.of("campaign", campaignService.updateCampaign(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Integer id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }
}
