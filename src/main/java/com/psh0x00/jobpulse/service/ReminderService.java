package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.ReminderRequest;
import com.psh0x00.jobpulse.dto.ReminderResponse;
import com.psh0x00.jobpulse.exception.ResourceNotFoundException;
import com.psh0x00.jobpulse.exception.UnauthorizedAccessException;
import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.Reminder;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.repository.ApplicationRepository;
import com.psh0x00.jobpulse.repository.ReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderService {

    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    private final ReminderRepository reminderRepository;
    private final ApplicationRepository applicationRepository;

    public ReminderService(ReminderRepository reminderRepository, ApplicationRepository applicationRepository) {
        this.reminderRepository = reminderRepository;
        this.applicationRepository = applicationRepository;
    }


    public ReminderResponse createReminder(ReminderRequest reminderRequest, User currentUser) {

        Application application = applicationRepository.findById(reminderRequest.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to create a reminder for this application");
        }

        Reminder reminder = new Reminder();
        reminder.setUser(currentUser);
        reminder.setApplication(application);
        reminder.setActionDescription(reminderRequest.getActionDescription());
        reminder.setRemindAt(reminderRequest.getRemindAt());
        reminder.setCompleted(false);

        reminderRepository.save(reminder);

        return new ReminderResponse(reminder);
    }

    public Page<ReminderResponse> getUserReminders(User user, Pageable pageable) {
        return reminderRepository.findAllByUserId(user.getId(), pageable)
                .map(ReminderResponse::new);
    }

    public ReminderResponse markAsCompleted(Long reminderId, User currentUser) {

        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found"));

        if (!reminder.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to mark this reminder as completed");
        }

        reminder.setCompleted(true);
        reminderRepository.save(reminder);

        return new ReminderResponse(reminder);
    }

    @Scheduled(cron = "0 * * * * *")
    public void processDueReminders(){

        LocalDateTime now = LocalDateTime.now();

        List<Reminder> dueReminders = reminderRepository.findByIsCompletedFalseAndRemindAtBefore(now);

        for (Reminder reminder : dueReminders) {

            logger.info("REMINDER ALERT: Sending email to {} -> {}", reminder.getUser().getEmail(), reminder.getActionDescription());

            reminder.setCompleted(true);
            reminderRepository.save(reminder);
        }
    }
}
