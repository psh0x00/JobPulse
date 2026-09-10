package com.psh0x00.jobpulse.controller;

import com.psh0x00.jobpulse.dto.ReminderRequest;
import com.psh0x00.jobpulse.dto.ReminderResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }


    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(@Valid @RequestBody ReminderRequest reminderRequest, @AuthenticationPrincipal User currentUser) {

        ReminderResponse reminderResponse = reminderService.createReminder(reminderRequest, currentUser);

        return ResponseEntity.ok(reminderResponse);
    }

    @GetMapping
    public ResponseEntity<Page<ReminderResponse>> getReminders(@AuthenticationPrincipal User currentUser, Pageable pageable) {
        Page<ReminderResponse> reminders = reminderService.getUserReminders(currentUser, pageable);
        return ResponseEntity.ok(reminders);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ReminderResponse> markAsCompleted(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        ReminderResponse reminderResponse = reminderService.markAsCompleted(id, currentUser);
        return ResponseEntity.ok(reminderResponse);
    }
}
