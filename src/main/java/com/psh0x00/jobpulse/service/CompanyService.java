package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.CompanyRequest;
import com.psh0x00.jobpulse.dto.CompanyResponse;
import com.psh0x00.jobpulse.exception.ResourceNotFoundException;
import com.psh0x00.jobpulse.exception.UnauthorizedAccessException;
import com.psh0x00.jobpulse.model.Company;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.repository.CompanyRepository;
import com.psh0x00.jobpulse.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Page<CompanyResponse> getUserCompanies(User currentUser, Pageable pageable) {

        Page<Company> userCompanies = companyRepository.findAllByUserId(currentUser.getId(), pageable);

        return userCompanies.map(CompanyResponse::new);
    }

    public CompanyResponse updateCompany(Long companyId, CompanyRequest updatedCompany, User currentUser) {

        Company existingCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found or does not belong to the user"));

        if(!existingCompany.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to update this company");
        }

        existingCompany.setIndustry(updatedCompany.getIndustry());
        existingCompany.setWebsite(updatedCompany.getWebsite());
        existingCompany.setNotes(updatedCompany.getNotes());

        Company savedCompany = companyRepository.save(existingCompany);
        return new CompanyResponse(savedCompany);
    }
}
