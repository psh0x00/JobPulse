package com.psh0x00.jobpulse.repository;

import com.psh0x00.jobpulse.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByNameAndUserId(String name, Long userId);
}
