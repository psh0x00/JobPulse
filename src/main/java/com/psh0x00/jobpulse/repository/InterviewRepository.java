package com.psh0x00.jobpulse.repository;

import com.psh0x00.jobpulse.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findAllByApplicationId(Long applicationId);

    long countByApplicationUserId(Long userId);
}
