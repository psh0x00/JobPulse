package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.CompanyRequest;
import com.psh0x00.jobpulse.dto.CompanyResponse;
import com.psh0x00.jobpulse.exception.ResourceNotFoundException;
import com.psh0x00.jobpulse.exception.UnauthorizedAccessException;
import com.psh0x00.jobpulse.model.Company;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void testGetUserCompanies_ReturnsPage() {
        User user = new User();
        user.setId(1L);

        Company company = new Company();
        company.setId(10L);
        company.setName("Tech Corp");
        company.setUser(user);

        when(companyRepository.findAllByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(company)));

        Page<CompanyResponse> response = companyService.getUserCompanies(user, PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
        assertEquals("Tech Corp", response.getContent().get(0).getName());
    }

    @Test
    void testUpdateCompany_Success() {
        User user = new User();
        user.setId(1L);

        Company existingCompany = new Company();
        existingCompany.setId(10L);
        existingCompany.setUser(user);
        existingCompany.setName("Old Name");

        CompanyRequest request = new CompanyRequest();
        request.setIndustry("Tech");
        request.setWebsite("https://example.com");

        when(companyRepository.findById(10L)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(existingCompany);

        CompanyResponse response = companyService.updateCompany(10L, request, user);

        assertEquals("Tech", response.getIndustry());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void testUpdateCompany_NotFound() {
        User user = new User();
        user.setId(1L);

        CompanyRequest request = new CompanyRequest();

        when(companyRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.updateCompany(10L, request, user));
    }

    @Test
    void testUpdateCompany_Unauthorized() {
        User owner = new User();
        owner.setId(1L);

        User intruder = new User();
        intruder.setId(2L);

        Company existingCompany = new Company();
        existingCompany.setId(10L);
        existingCompany.setUser(owner);

        CompanyRequest request = new CompanyRequest();

        when(companyRepository.findById(10L)).thenReturn(Optional.of(existingCompany));

        assertThrows(UnauthorizedAccessException.class, () -> companyService.updateCompany(10L, request, intruder));
    }
}
