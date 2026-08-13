package com.learning.store.controller;

import com.learning.store.dto.DashboardDto;
import com.learning.store.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // The frontend reads this response directly, without a wrapper key.
    @GetMapping
    public DashboardDto getDashboard() {
        return dashboardService.getDashboardData();
    }
}
