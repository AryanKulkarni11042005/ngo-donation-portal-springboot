package com.learning.store.controller;

import com.learning.store.dto.DonationSummaryDto;
import com.learning.store.entity.Donation;
import com.learning.store.service.DonationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping
    public List<DonationSummaryDto> getAllDonations() {
        return donationService.getAllDonations();
    }

    @GetMapping("/{id}")
    public DonationSummaryDto getDonationById(@PathVariable Integer id) {
        return donationService.getDonationById(id);
    }
}