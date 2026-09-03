package com.psh0x00.jobpulse.dto;

import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import com.psh0x00.jobpulse.model.enums.JobType;

public class ApplicationResponse {

    private Long id;
    private String companyName;
    private String roleTitle;
    private JobType jobType;
    private ApplicationStatus applicationStatus;
    private String dateApplied;


    public ApplicationResponse(Application application) {
        this.id = application.getId();
        this.companyName = application.getCompany().getName();
        this.roleTitle = application.getRoleTitle();
        this.jobType = application.getJobType();
        this.applicationStatus = application.getApplicationStatus();
        this.dateApplied = application.getDateApplied().toString();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getDateApplied() {
        return dateApplied;
    }

    public void setDateApplied(String dateApplied) {
        this.dateApplied = dateApplied;
    }
}
