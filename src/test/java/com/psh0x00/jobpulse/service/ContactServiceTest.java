package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.ContactRequest;
import com.psh0x00.jobpulse.dto.ContactResponse;
import com.psh0x00.jobpulse.exception.DuplicateResourceException;
import com.psh0x00.jobpulse.exception.ResourceNotFoundException;
import com.psh0x00.jobpulse.exception.UnauthorizedAccessException;
import com.psh0x00.jobpulse.model.Company;
import com.psh0x00.jobpulse.model.Contact;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.repository.CompanyRepository;
import com.psh0x00.jobpulse.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ContactService contactService;

    @Test
    void testCreateContact_Success() {
        User user = new User();
        user.setId(1L);

        Company company = new Company();
        company.setId(10L);

        ContactRequest request = new ContactRequest();
        request.setEmail("test@test.com");
        request.setCompanyId(10L);
        request.setName("John Doe");

        Contact contact = new Contact();
        contact.setId(100L);
        contact.setCompany(company);
        contact.setUser(user);
        contact.setName("John Doe");

        when(contactRepository.findByEmailAndUserId("test@test.com", 1L)).thenReturn(Optional.empty());
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        ContactResponse response = contactService.createContact(request, user);

        assertEquals("John Doe", response.getName());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void testCreateContact_DuplicateEmail() {
        User user = new User();
        user.setId(1L);

        ContactRequest request = new ContactRequest();
        request.setEmail("duplicate@test.com");

        when(contactRepository.findByEmailAndUserId("duplicate@test.com", 1L)).thenReturn(Optional.of(new Contact()));

        assertThrows(DuplicateResourceException.class, () -> contactService.createContact(request, user));
    }

    @Test
    void testUpdateContact_Unauthorized() {
        User owner = new User();
        owner.setId(1L);
        User intruder = new User();
        intruder.setId(2L);

        Contact contact = new Contact();
        contact.setUser(owner);

        when(contactRepository.findById(100L)).thenReturn(Optional.of(contact));

        assertThrows(UnauthorizedAccessException.class, () -> contactService.updateContact(100L, new ContactRequest(), intruder));
    }
}
