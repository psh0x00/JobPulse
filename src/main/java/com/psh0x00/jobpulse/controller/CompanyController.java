package com.psh0x00.jobpulse.controller;

import com.psh0x00.jobpulse.dto.ApplicationResponse;
import com.psh0x00.jobpulse.dto.CompanyRequest;
import com.psh0x00.jobpulse.dto.CompanyResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.service.CompanyService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<CompanyResponse>> getUserCompanies(@AuthenticationPrincipal User currentUser) {
        List<CompanyResponse> companies = companyService.getUserCompanies(currentUser);
        return ResponseEntity.ok(companies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyRequest newCompany, @AuthenticationPrincipal User currentUser){
        CompanyResponse updatedCompany = companyService.updateCompany(id, newCompany, currentUser);
        return ResponseEntity.ok(updatedCompany);
    }
}
