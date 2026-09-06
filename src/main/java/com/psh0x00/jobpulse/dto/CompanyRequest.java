package com.psh0x00.jobpulse.dto;

import com.psh0x00.jobpulse.model.User;
import jakarta.validation.constraints.NotBlank;

public class CompanyRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String industry;
    private String website;
    private String notes;

    @NotBlank
    private User user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
