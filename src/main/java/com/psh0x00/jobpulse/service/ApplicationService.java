package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.ApplicationRequest;
import com.psh0x00.jobpulse.dto.ApplicationResponse;
import com.psh0x00.jobpulse.exception.InvalidStatusTransitionException;
import com.psh0x00.jobpulse.exception.ResourceNotFoundException;
import com.psh0x00.jobpulse.exception.UnauthorizedAccessException;
import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.Company;
import com.psh0x00.jobpulse.model.Tag;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.repository.ApplicationRepository;
import com.psh0x00.jobpulse.repository.CompanyRepository;
import com.psh0x00.jobpulse.repository.TagRepository;
import com.psh0x00.jobpulse.specification.ApplicationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final TagRepository tagRepository;


    public ApplicationService(ApplicationRepository applicationRepository, CompanyRepository companyRepository, TagRepository tagRepository) {
        this.applicationRepository = applicationRepository;
        this.companyRepository = companyRepository;
        this.tagRepository = tagRepository;
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
        setApplicationAttributes(request, application);

        application.setApplicationStatus(ApplicationStatus.SAVED);
        application.setDateApplied(LocalDateTime.now());

        applicationRepository.save(application);

        return new ApplicationResponse(application);
    }

    public Page<ApplicationResponse> getUserApplications(User currentUser, ApplicationStatus status, String companyName, Pageable pageable) {

        Specification<Application> spec = Specification.where(ApplicationSpecification.hasUserId(currentUser.getId()));

        if (status != null) {
            spec = spec.and(ApplicationSpecification.hasStatus(status));
        }

        if (companyName != null && !companyName.trim().isEmpty()) {
            spec = spec.and(ApplicationSpecification.hasCompanyName(companyName));
        }

        Page<Application> applications = applicationRepository.findAll(spec, pageable);

        return applications.map(ApplicationResponse::new);
    }

    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus newStatus, User currentUser) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (!application.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to update this application");
        }

        if(!isValidStatusTransition(application.getApplicationStatus(), newStatus)) {
            throw new InvalidStatusTransitionException("Invalid status transition from " + application.getApplicationStatus() + " to " + newStatus);
        }

        application.setApplicationStatus(newStatus);
        applicationRepository.save(application);

        return new ApplicationResponse(application);
    }

    public ApplicationResponse updateApplication(Long applicationId, ApplicationRequest request, User currentUser) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (!application.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to update this application");
        }

        setApplicationAttributes(request, application);

        applicationRepository.save(application);

        return new ApplicationResponse(application);
    }

    public void deleteApplication(Long applicationId, User currentUser){

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if(!application.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to delete this application");
        }

        applicationRepository.delete(application);
    }

    public ApplicationResponse addTagToApplication(Long applicationId, Long tagId, User currentUser){

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if(!application.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to update this application");
        }

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));

        application.getTags().add(tag);
        applicationRepository.save(application);

        return new ApplicationResponse(application);
    }

    public ApplicationResponse removeTagFromApplication(Long applicationId, Long tagId, User currentUser){

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if(!application.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to update this application");
        }

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));

        application.getTags().remove(tag);
        applicationRepository.save(application);

        return new ApplicationResponse(application);
    }


    private void setApplicationAttributes(ApplicationRequest request, Application application) {
        application.setRoleTitle(request.getRoleTitle());
        application.setJobUrl(request.getJobUrl());
        application.setLocation(request.getLocation());
        application.setJobType(request.getJobType());
        application.setSalaryMin(request.getSalaryMin());
        application.setSalaryMax(request.getSalaryMax());
        application.setNotes(request.getNotes());
    }

    private boolean isValidStatusTransition(ApplicationStatus applicationStatus, ApplicationStatus newStatus) {

        return switch (applicationStatus) {
            case SAVED -> newStatus == ApplicationStatus.APPLIED || newStatus == ApplicationStatus.WITHDRAWN;
            case APPLIED -> newStatus  == ApplicationStatus.SCREENING || newStatus == ApplicationStatus.INTERVIEW || newStatus == ApplicationStatus.REJECTED || newStatus == ApplicationStatus.WITHDRAWN;
            case SCREENING -> newStatus == ApplicationStatus.INTERVIEW || newStatus == ApplicationStatus.REJECTED || newStatus == ApplicationStatus.WITHDRAWN;
            case INTERVIEW -> newStatus == ApplicationStatus.OFFER || newStatus == ApplicationStatus.REJECTED || newStatus == ApplicationStatus.WITHDRAWN;
            case OFFER -> newStatus == ApplicationStatus.OFFER_ACCEPTED || newStatus == ApplicationStatus.OFFER_DECLINED || newStatus == ApplicationStatus.WITHDRAWN;
            case REJECTED -> false;
            case OFFER_ACCEPTED -> false;
            case OFFER_DECLINED -> false;
            case WITHDRAWN -> false;
            default -> false;
        };
    }
}
