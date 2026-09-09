package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.InterviewRequest;
import com.psh0x00.jobpulse.dto.InterviewResponse;
import com.psh0x00.jobpulse.exception.ResourceNotFoundException;
import com.psh0x00.jobpulse.exception.UnauthorizedAccessException;
import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.Interview;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.InterviewOutcome;
import com.psh0x00.jobpulse.repository.ApplicationRepository;
import com.psh0x00.jobpulse.repository.InterviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private InterviewService interviewService;

    @Test
    void testCreateInterview_Success() {
        User user = new User();
        user.setId(1L);

        Application application = new Application();
        application.setId(10L);
        application.setUser(user);

        InterviewRequest request = new InterviewRequest();
        request.setNotes("First round");

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));

        InterviewResponse response = interviewService.createInterview(10L, request, user);

        assertEquals(InterviewOutcome.PENDING, response.getInterviewOutcome());
        verify(interviewRepository, times(1)).save(any(Interview.class));
    }

    @Test
    void testUpdateOutcome_Success() {
        User user = new User();
        user.setId(1L);

        Application application = new Application();
        application.setUser(user);

        Interview interview = new Interview();
        interview.setId(100L);
        interview.setApplication(application);
        interview.setInterviewOutcome(InterviewOutcome.PENDING);

        when(interviewRepository.findById(100L)).thenReturn(Optional.of(interview));

        InterviewResponse response = interviewService.updateOutcome(100L, InterviewOutcome.PASSED, user);

        assertEquals(InterviewOutcome.PASSED, response.getInterviewOutcome());
        verify(interviewRepository, times(1)).save(interview);
    }

    @Test
    void testCreateInterview_Unauthorized() {
        User owner = new User();
        owner.setId(1L);

        User intruder = new User();
        intruder.setId(2L);

        Application application = new Application();
        application.setId(10L);
        application.setUser(owner);

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));

        assertThrows(UnauthorizedAccessException.class, () -> interviewService.createInterview(10L, new InterviewRequest(), intruder));
    }
}
