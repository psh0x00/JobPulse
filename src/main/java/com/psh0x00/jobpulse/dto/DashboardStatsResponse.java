package com.psh0x00.jobpulse.dto;

import java.io.Serializable;

public class DashboardStatsResponse implements Serializable {

    private Long totalApplications;
    private Long totalInterviews;
    private Long totalOffers;

    public  DashboardStatsResponse(Long totalApplications, Long totalInterviews, Long totalOffers) {
        this.totalApplications = totalApplications;
        this.totalInterviews = totalInterviews;
        this.totalOffers = totalOffers;
    }


    public Long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(Long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public Long getTotalInterviews() {
        return totalInterviews;
    }

    public void setTotalInterviews(Long totalInterviews) {
        this.totalInterviews = totalInterviews;
    }

    public Long getTotalOffers() {
        return totalOffers;
    }

    public void setTotalOffers(Long totalOffers) {
        this.totalOffers = totalOffers;
    }
}
