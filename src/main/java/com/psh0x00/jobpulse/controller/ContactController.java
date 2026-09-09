package com.psh0x00.jobpulse.controller;

import com.psh0x00.jobpulse.dto.ContactRequest;
import com.psh0x00.jobpulse.dto.ContactResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactService contactService;


    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactResponse> createContact(@Valid @RequestBody ContactRequest contactRequest, @AuthenticationPrincipal User currentUser) {
        ContactResponse savedContact = contactService.createContact(contactRequest, currentUser);
        return ResponseEntity.ok(savedContact);
    }

    @GetMapping
    public ResponseEntity<List<ContactResponse>> getAllContacts(@AuthenticationPrincipal User currentUser) {
        List<ContactResponse> userContacts = contactService.getUserContacts(currentUser);
        return ResponseEntity.ok(userContacts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponse> updateContact(@PathVariable Long id, @Valid @RequestBody ContactRequest contactRequest, @AuthenticationPrincipal User currentUser) {
        ContactResponse updatedContact = contactService.updateContact(id, contactRequest, currentUser);
        return ResponseEntity.ok(updatedContact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        contactService.deleteContact(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
