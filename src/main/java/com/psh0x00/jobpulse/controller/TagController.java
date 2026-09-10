package com.psh0x00.jobpulse.controller;

import com.psh0x00.jobpulse.dto.TagRequest;
import com.psh0x00.jobpulse.dto.TagResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.service.TagService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest tagRequest, @AuthenticationPrincipal User currentUser) {
        TagResponse tagResponse = tagService.createTag(tagRequest, currentUser);
        return ResponseEntity.ok(tagResponse);
    }

    @GetMapping
    public ResponseEntity<Page<TagResponse>> getAllTags(@AuthenticationPrincipal User currentUser, Pageable pageable) {
        Page<TagResponse> tagResponse = tagService.getAllTags(currentUser, pageable);
        return ResponseEntity.ok(tagResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        tagService.deleteTag(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
