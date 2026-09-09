package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.ApplicationRequest;
import com.psh0x00.jobpulse.dto.ApplicationResponse;
import com.psh0x00.jobpulse.exception.InvalidStatusTransitionException;
import com.psh0x00.jobpulse.exception.UnauthorizedAccessException;
import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.Company;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.model.enums.JobType;
import com.psh0x00.jobpulse.repository.ApplicationRepository;
import com.psh0x00.jobpulse.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private CompanyRepository companyRepository;
    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void testValidStatusTransition() {
        // 1. ARRANGE (Given)
        // Create a user and an application with the initial status of SAVED
        User user = new User();
        user.setId(1L);

        Application application = createApplication(user);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));

        // 2. ACT (When)
        // Attempt to update the status from SAVED to APPLIED
        applicationService.updateStatus(100L, ApplicationStatus.APPLIED, user);

        // 3. ASSERT (Then)
        // Here you would typically verify that the status was updated correctly.
        assertEquals(ApplicationStatus.APPLIED, application.getApplicationStatus());
    }

    @Test
    void testInvalidStatusTransition_SavedToInterviewing(){

        // 1. ARRANGE (Given)
        User user = new User();
        user.setId(1L);

        Application application = createApplication(user);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));

        // 2. & 3. ACT & ASSERT (When & Then)
        RuntimeException exception = assertThrows(InvalidStatusTransitionException.class, () -> {
            applicationService.updateStatus(100L, ApplicationStatus.INTERVIEW, user);
        });

        assertEquals("Invalid status transition from SAVED to INTERVIEW", exception.getMessage());
    }

    @Test
    void testUnauthorizedAccess(){
        // 1. ARRANGE (Given)
        User userA = new User();
        userA.setId(1L);

        User userB = new User();
        userB.setId(2L);

        Application application = createApplication(userB);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));

        // 2. & 3. ACT & ASSERT (When & Then)
        RuntimeException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            applicationService.updateStatus(100L, ApplicationStatus.APPLIED, userA);
        });

        assertEquals("User is not authorized to update this application", exception.getMessage());
    }

    @Test
    void testCreateApplication_Success(){

        Company company = new Company();
        company.setName("Software Engineer");

        ApplicationRequest request = new ApplicationRequest();
        request.setRoleTitle("Software Engineer");
        request.setCompanyName(company.getName());
        request.setJobType(JobType.FULL_TIME);

        User user = new User();
        user.setId(1L);

        when(companyRepository.findByNameAndUserId("Software Engineer", 1L)).thenReturn(Optional.of(company));

        ApplicationResponse response = applicationService.createApplication(request, user);

        assertEquals("Software Engineer", response.getRoleTitle());
        assertEquals(JobType.FULL_TIME, response.getJobType());
        assertEquals("Software Engineer", response.getCompanyName());

        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    void testGetUserApplications_ReturnsPage(){

        User user = new User();
        user.setId(1L);

       when(applicationRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(createApplication(user))));

        Page<ApplicationResponse> applicationsPage = applicationService.getUserApplications(user, ApplicationStatus.SAVED, "test_name", PageRequest.of(0, 10));

        assertEquals(1, applicationsPage.getTotalElements());
        verify(applicationRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void testDeleteApplication_Success(){

        User user = new User();
        user.setId(1L);

        Application application = createApplication(user);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));

        applicationService.deleteApplication(100L, user);

        verify(applicationRepository, times(1)).delete(application);
    }

    @Test
    void testDeleteApplication_Unauthorized() {

        User userA = new User();
        userA.setId(1L);

        User userB = new User();
        userB.setId(2L);

        Application application = createApplication(userB);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));

        RuntimeException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            applicationService.deleteApplication(100L, userA);
        });

        assertEquals("User is not authorized to delete this application", exception.getMessage());
    }


    private Application createApplication(User user){
        Company company = new Company();
        company.setName("Test Company");

        Application application = new Application();
        application.setId(100L);
        application.setUser(user);
        application.setCompany(company);
        application.setApplicationStatus(ApplicationStatus.SAVED);
        application.setDateApplied(LocalDateTime.now());

        return application;
    }
}
