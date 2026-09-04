package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.exception.InvalidStatusTransitionException;
import com.psh0x00.jobpulse.exception.UnauthorizedAccessException;
import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.Company;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.repository.ApplicationRepository;
import com.psh0x00.jobpulse.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
