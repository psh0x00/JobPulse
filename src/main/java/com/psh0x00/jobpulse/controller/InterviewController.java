package com.psh0x00.jobpulse.controller;

import com.psh0x00.jobpulse.dto.InterviewRequest;
import com.psh0x00.jobpulse.dto.InterviewResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.InterviewOutcome;
import com.psh0x00.jobpulse.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }


    @PostMapping("/api/v1/applications/{applicationId}/interviews")
    public ResponseEntity<InterviewResponse> createInterview(@PathVariable Long applicationId, @Valid @RequestBody InterviewRequest interviewRequest, @AuthenticationPrincipal User currentUser) {
        InterviewResponse interviewResponse = interviewService.createInterview(applicationId, interviewRequest, currentUser);
        return ResponseEntity.ok(interviewResponse);
    }

    @GetMapping("/api/v1/applications/{applicationId}/interviews")
    public ResponseEntity<List<InterviewResponse>> getInterviewsForApplication(@PathVariable Long applicationId, @AuthenticationPrincipal User currentUser) {
        List<InterviewResponse> interviews = interviewService.getInterviewsForApplication(applicationId, currentUser);
        return ResponseEntity.ok(interviews);
    }

    @PutMapping("/api/v1/interviews/{interviewId}/outcome")
    public ResponseEntity<InterviewResponse> updateInterviewOutcome(@PathVariable Long interviewId, @RequestParam InterviewOutcome newOutcome, @AuthenticationPrincipal User currentUser) {
        InterviewResponse interviewResponse = interviewService.updateOutcome(interviewId, newOutcome, currentUser);
        return ResponseEntity.ok(interviewResponse);
    }

    @PutMapping("/api/v1/interviews/{interviewId}")
    public ResponseEntity<InterviewResponse> updateInterview(@PathVariable Long interviewId, @Valid @RequestBody InterviewRequest updatedInterview, @AuthenticationPrincipal User currentUser) {
        InterviewResponse interviewResponse = interviewService.updateInterview(interviewId, updatedInterview, currentUser);
        return ResponseEntity.ok(interviewResponse);
    }

    @DeleteMapping("/api/v1/interviews/{interviewId}")
    public ResponseEntity<Void> deleteInterview(@PathVariable Long interviewId, @AuthenticationPrincipal User currentUser) {
        interviewService.deleteInterview(interviewId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
