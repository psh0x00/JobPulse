package com.psh0x00.jobpulse.controller;

import com.psh0x00.jobpulse.dto.DashboardStatsResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.service.ApplicationService;
import com.psh0x00.jobpulse.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard/stats")
public class DashboardController {

    private final DashboardService dashboardService;

    public  DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    @GetMapping
    public ResponseEntity<DashboardStatsResponse> getStats(@AuthenticationPrincipal User currentUser){
        DashboardStatsResponse stats = dashboardService.getStats(currentUser);
        return ResponseEntity.ok(stats);
    }
}
