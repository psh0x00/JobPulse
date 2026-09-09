package com.psh0x00.jobpulse.repository;

import com.psh0x00.jobpulse.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findAllByUserId(Long userId, Pageable pageable);
}
