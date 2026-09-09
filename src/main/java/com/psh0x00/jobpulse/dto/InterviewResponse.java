package com.psh0x00.jobpulse.dto;

import com.psh0x00.jobpulse.model.Interview;
import com.psh0x00.jobpulse.model.enums.InterviewOutcome;
import com.psh0x00.jobpulse.model.enums.InterviewType;

import java.time.LocalDateTime;

public class InterviewResponse {

    private Long id;
    private LocalDateTime scheduledAt;
    private InterviewType interviewType;
    private String locationOrLink;
    private String notes;
    private InterviewOutcome interviewOutcome;


    public InterviewResponse(Interview interview) {
        this.id = interview.getId();
        this.scheduledAt = interview.getScheduledAt();
        this.interviewType = interview.getInterviewType();
        this.locationOrLink = interview.getLocationOrLink();
        this.notes = interview.getNotes();
        this.interviewOutcome = interview.getInterviewOutcome();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public InterviewType getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(InterviewType interviewType) {
        this.interviewType = interviewType;
    }

    public String getLocationOrLink() {
        return locationOrLink;
    }

    public void setLocationOrLink(String locationOrLink) {
        this.locationOrLink = locationOrLink;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public InterviewOutcome getInterviewOutcome() {
        return interviewOutcome;
    }

    public void setInterviewOutcome(InterviewOutcome interviewOutcome) {
        this.interviewOutcome = interviewOutcome;
    }
}