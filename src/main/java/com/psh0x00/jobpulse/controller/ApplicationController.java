package com.psh0x00.jobpulse.controller;


import com.psh0x00.jobpulse.dto.ApplicationRequest;
import com.psh0x00.jobpulse.dto.ApplicationResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;


    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(@RequestBody ApplicationRequest request, @AuthenticationPrincipal User currentUser){

        ApplicationResponse savedApplication = applicationService.createApplication(request, currentUser);

        return ResponseEntity.ok(savedApplication);
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getUserApplications(@AuthenticationPrincipal User currentUser){
        List<ApplicationResponse> userApplications = applicationService.getUserApplications(currentUser);
        return ResponseEntity.ok(userApplications);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id, @RequestParam ApplicationStatus newStatus, @AuthenticationPrincipal User currentUser){

        ApplicationResponse updatedApplication = applicationService.updateStatus(id, newStatus, currentUser);
        return ResponseEntity.ok(updatedApplication);
    }
}
