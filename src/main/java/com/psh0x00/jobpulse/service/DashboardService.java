package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.DashboardStatsResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.repository.ApplicationRepository;
import com.psh0x00.jobpulse.repository.InterviewRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;

    public DashboardService(ApplicationRepository applicationRepository, InterviewRepository interviewRepository) {
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
    }

    @Cacheable(value = "dashboardStats", key = "#currentUser.id")
    public DashboardStatsResponse getStats(User currentUser) {
        Long userId = currentUser.getId();

        long totalApplications = applicationRepository.countByUserId(userId);
        long totalOffers = applicationRepository.countByUserIdAndApplicationStatus(userId, ApplicationStatus.OFFER);
        long totalInterviews = interviewRepository.countByApplicationUserId(userId);

        return new DashboardStatsResponse(totalApplications, totalInterviews, totalOffers);
    }
}
