package com.learning.store.controller;

import com.learning.store.dto.DonationRequestDto;
import com.learning.store.dto.DonationStatusRequestDto;
import com.learning.store.dto.DonationSummaryDto;
import com.learning.store.model.DonationStatus;
import com.learning.store.service.DonationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping
    public Map<String, Object> getAllDonations(@RequestParam(required = false) String search,
                                               @RequestParam(required = false) DonationStatus status) {
        return Map.of("donations", donationService.getAllDonations(search, status));
    }

    @GetMapping("/{id}")
    public Map<String, Object> getDonationById(@PathVariable Integer id) {
        return Map.of("donation", donationService.getDonationById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDonation(@Valid @RequestBody DonationRequestDto dto) {
        DonationSummaryDto created = donationService.createDonation(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("donation", created));
    }

    @PatchMapping("/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable Integer id,
                                            @Valid @RequestBody DonationStatusRequestDto dto) {
        return Map.of("donation", donationService.updateStatus(id, dto.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Integer id) {
        donationService.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }
}
