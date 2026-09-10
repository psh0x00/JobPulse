package com.psh0x00.jobpulse.repository;

import com.psh0x00.jobpulse.model.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Page<Reminder> findAllByUserId(Long userId, Pageable pageable);

    List<Reminder> findByIsCompletedFalseAndRemindAtBefore(LocalDateTime time);
}
