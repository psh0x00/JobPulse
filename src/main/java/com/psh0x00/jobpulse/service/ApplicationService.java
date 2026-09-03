package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.ApplicationRequest;
import com.psh0x00.jobpulse.dto.ApplicationResponse;
import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.Company;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.repository.ApplicationRepository;
import com.psh0x00.jobpulse.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;


    public ApplicationService(ApplicationRepository aaplicationRepository, CompanyRepository companyRepository) {
        this.applicationRepository = aaplicationRepository;
        this.companyRepository = companyRepository;
    }

    public ApplicationResponse createApplication(ApplicationRequest request, User currentUser){

        Company company = companyRepository.findByNameAndUserId(request.getCompanyName(), currentUser.getId())
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(request.getCompanyName());
                    newCompany.setUser(currentUser);
                    return companyRepository.save(newCompany);
                });

        Application application = new Application();
        application.setUser(currentUser);
        application.setCompany(company);
        application.setRoleTitle(request.getRoleTitle());
        application.setJobUrl(request.getJobUrl());
        application.setLocation(request.getLocation());
        application.setJobType(request.getJobType());
        application.setSalaryMin(request.getSalaryMin());
        application.setSalaryMax(request.getSalaryMax());
        application.setNotes(request.getNotes());

        application.setApplicationStatus(ApplicationStatus.SAVED);
        application.setDateApplied(LocalDateTime.now());

        applicationRepository.save(application);

        return new ApplicationResponse(application);
    }

    public List<ApplicationResponse> getUserApplications(User currentUser){

        List<Application> applications = applicationRepository.findAllByUserId(currentUser.getId());
        List<ApplicationResponse> userApplications = applications.stream().map(ApplicationResponse::new).toList();

        return userApplications;
    }
}
