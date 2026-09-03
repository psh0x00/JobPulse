package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void testValidStatusTransition() {
        // 1. ARRANGE (Given)
        // Create a user and an application with the initial status of SAVED
        User user = new User();
        user.setId(1L);

        Application application = new Application();
        application.setId(100L);
        application.setUser(user);
        application.setApplicationStatus(ApplicationStatus.SAVED);

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

        Application application = new Application();
        application.setId(100L);
        application.setUser(user);
        application.setApplicationStatus(ApplicationStatus.SAVED);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));

        // 2. & 3. ACT & ASSERT (When & Then)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.updateStatus(100L, ApplicationStatus.INTERVIEW, user);
        });

        assertEquals("Invalid status transition from SAVED to INTERVIEW", exception.getMessage());
    }
}
