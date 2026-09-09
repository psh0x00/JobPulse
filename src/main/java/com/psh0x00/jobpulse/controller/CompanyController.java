package com.psh0x00.jobpulse.controller;

import com.psh0x00.jobpulse.dto.ApplicationResponse;
import com.psh0x00.jobpulse.dto.CompanyRequest;
import com.psh0x00.jobpulse.dto.CompanyResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;


    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<Page<CompanyResponse>> getUserCompanies(@AuthenticationPrincipal User currentUser, Pageable pageable) {
        Page<CompanyResponse> companies = companyService.getUserCompanies(currentUser, pageable);
        return ResponseEntity.ok(companies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyRequest newCompany, @AuthenticationPrincipal User currentUser){
        CompanyResponse updatedCompany = companyService.updateCompany(id, newCompany, currentUser);
        return ResponseEntity.ok(updatedCompany);
    }
}
