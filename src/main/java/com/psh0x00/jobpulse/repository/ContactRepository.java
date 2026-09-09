package com.psh0x00.jobpulse.repository;

import com.psh0x00.jobpulse.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Page<Contact> findAllByUserId(Long userId, Pageable pageable);
    Optional<Contact> findByEmailAndUserId(String email, Long userId);
}
