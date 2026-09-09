package com.psh0x00.jobpulse.controller;


import com.psh0x00.jobpulse.dto.ApplicationRequest;
import com.psh0x00.jobpulse.dto.ApplicationResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;


    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody ApplicationRequest request, @AuthenticationPrincipal User currentUser){

        ApplicationResponse savedApplication = applicationService.createApplication(request, currentUser);

        return ResponseEntity.ok(savedApplication);
    }

    @GetMapping
    public ResponseEntity<Page<ApplicationResponse>> getUserApplications(@AuthenticationPrincipal User currentUser, Pageable pageable){
        Page<ApplicationResponse> userApplications = applicationService.getUserApplications(currentUser, pageable);
        return ResponseEntity.ok(userApplications);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id, @RequestParam ApplicationStatus newStatus, @AuthenticationPrincipal User currentUser){
        ApplicationResponse updatedApplication = applicationService.updateStatus(id, newStatus, currentUser);
        return ResponseEntity.ok(updatedApplication);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(@PathVariable Long id, @Valid @RequestBody ApplicationRequest updatedApplication, @AuthenticationPrincipal User currentUser){
        ApplicationResponse updatedApp = applicationService.updateApplication(id, updatedApplication, currentUser);
        return ResponseEntity.ok(updatedApp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id, @AuthenticationPrincipal User currentUser){
        applicationService.deleteApplication(id, currentUser);

        return ResponseEntity.noContent().build();
    }
}
