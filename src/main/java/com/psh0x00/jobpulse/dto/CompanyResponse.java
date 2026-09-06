package com.psh0x00.jobpulse.dto;

import com.psh0x00.jobpulse.model.Company;

public class CompanyResponse {

    private Long id;
    private String name;
    private String website;
    private String industry;
    private String notes;

    public CompanyResponse(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.website = company.getWebsite();
        this.industry = company.getIndustry();
        this.notes = company.getNotes();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
