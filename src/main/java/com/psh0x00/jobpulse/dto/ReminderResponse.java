package com.psh0x00.jobpulse.dto;

import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.Reminder;

import java.time.LocalDateTime;

public class ReminderResponse {

    private Long id;
    private Long applicationId;
    private String actionDescription;
    private LocalDateTime remindAt;
    private Boolean isCompleted;


    public ReminderResponse(Reminder reminder) {
        this.id = reminder.getId();
        this.applicationId = reminder.getApplication().getId();
        this.actionDescription = reminder.getActionDescription();
        this.remindAt = reminder.getRemindAt();
        this.isCompleted = reminder.getCompleted();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public LocalDateTime getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(LocalDateTime remindAt) {
        this.remindAt = remindAt;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }
}
