package com.psh0x00.jobpulse.dto;

import com.psh0x00.jobpulse.model.Contact;
import com.psh0x00.jobpulse.model.enums.RelationshipType;

public class ContactResponse {

    private Long id;
    private String name;
    private Long companyId;
    private String role;
    private String email;
    private String linkedinUrl;
    private RelationshipType relationshipType;
    private String notes;



    public  ContactResponse(Contact contact) {
        this.id = contact.getId();
        this.name = contact.getName();
        this.companyId = contact.getCompany().getId();
        this.role = contact.getRole();
        this.email = contact.getEmail();
        this.linkedinUrl = contact.getLinkedinUrl();
        this.relationshipType = contact.getRelationshipType();
        this.notes = contact.getNotes();
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}