package com.psh0x00.jobpulse.repository;

import com.psh0x00.jobpulse.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Page<Tag> findAllByUserId(Long userId, Pageable pageable);

    Optional<Tag> findByNameAndUserId(String name, Long userId);
}
