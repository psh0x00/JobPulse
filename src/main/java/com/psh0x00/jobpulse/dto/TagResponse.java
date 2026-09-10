package com.psh0x00.jobpulse.dto;

import com.psh0x00.jobpulse.model.Tag;

public class TagResponse {

    private Long id;

    private String name;


    public TagResponse(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
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
}
