package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.TagRequest;
import com.psh0x00.jobpulse.dto.TagResponse;
import com.psh0x00.jobpulse.exception.DuplicateResourceException;
import com.psh0x00.jobpulse.exception.ResourceNotFoundException;
import com.psh0x00.jobpulse.exception.UnauthorizedAccessException;
import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.Tag;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.repository.ApplicationRepository;
import com.psh0x00.jobpulse.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final ApplicationRepository applicationRepository;

    public TagService(TagRepository tagRepository, ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
        this.tagRepository = tagRepository;
    }


    public TagResponse createTag(TagRequest tagRequest, User currentUser){

        if (tagRepository.findByNameAndUserId(tagRequest.getName(), currentUser.getId()).isPresent()) {
            throw new DuplicateResourceException("Tag with name '" + tagRequest.getName() + "' already exists for this user.");
        }

        Tag tag = new Tag();
        tag.setName(tagRequest.getName());
        tag.setUser(currentUser);

        tagRepository.save(tag);

        return new TagResponse(tag);
    }

    public Page<TagResponse> getAllTags(User currentUser, Pageable pageable) {
        return tagRepository.findAllByUserId(currentUser.getId(), pageable).map(TagResponse::new);
    }

    public void deleteTag(Long tagId, User currentUser) {

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id '" + tagId + "' not found."));

        if (!tag.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("User does not have permission to delete this tag.");
        }

        for(Application app : tag.getApplications()) {
            app.getTags().remove(tag);
            applicationRepository.save(app);
        }

        tagRepository.delete(tag);
    }
}
