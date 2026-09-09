package com.psh0x00.jobpulse.specification;

import com.psh0x00.jobpulse.model.Application;
import com.psh0x00.jobpulse.model.enums.ApplicationStatus;
import org.springframework.data.jpa.domain.Specification;

public class ApplicationSpecification {

    public static Specification<Application> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Application> hasStatus(ApplicationStatus status) {
        return ((root, query, criteriaBuilder) ->  criteriaBuilder.equal(root.get("applicationStatus"), status));
    }

    public static Specification<Application> hasCompanyName(String companyName) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("company").get("name"), "%" + companyName + "%"));
    }
}
