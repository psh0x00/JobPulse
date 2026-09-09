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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewService {

    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;


    public InterviewService(ApplicationRepository applicationRepository, InterviewRepository interviewRepository) {
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
    }

    public InterviewResponse createInterview(Long applicationId, InterviewRequest interviewRequest, User currentUser) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to create an interview for this application");
        }

        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setInterviewOutcome(InterviewOutcome.PENDING);
        setInterviewAttributes(interviewRequest, interview);

        interviewRepository.save(interview);

        return new InterviewResponse(interview);
    }

    public List<InterviewResponse> getInterviewsForApplication(Long applicationId, User currentUser) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to view interviews for this application");
        }

        List<Interview> interviews = interviewRepository.findAllByApplicationId(applicationId);

        return interviews.stream().map(InterviewResponse::new).toList();
    }

    public InterviewResponse updateOutcome(Long interviewId, InterviewOutcome newOutcome, User currentUser) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));

        if (!interview.getApplication().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to update this interview");
        }

        interview.setInterviewOutcome(newOutcome);

        interviewRepository.save(interview);

        return new InterviewResponse(interview);
    }

    public InterviewResponse updateInterview(Long interviewId, InterviewRequest request, User currentUser) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));

        if (!interview.getApplication().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to update this interview");
        }

        setInterviewAttributes(request, interview);

        interviewRepository.save(interview);

        return new InterviewResponse(interview);
    }

    public void deleteInterview(Long interviewId, User currentUser) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));

        if (!interview.getApplication().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User is not authorized to delete this interview");
        }

        interviewRepository.delete(interview);
    }


    private void setInterviewAttributes(InterviewRequest request, Interview interview) {
        interview.setScheduledAt(request.getScheduledAt());
        interview.setInterviewType(request.getInterviewType());
        interview.setLocationOrLink(request.getLocationOrLink());
        interview.setNotes(request.getNotes());
    }
}
