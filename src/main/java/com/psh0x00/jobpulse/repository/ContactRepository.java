package com.psh0x00.jobpulse.repository;

import com.psh0x00.jobpulse.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByUserId(Long userId);
    Optional<Contact> findByEmailAndUserId(String email, Long userId);
}
