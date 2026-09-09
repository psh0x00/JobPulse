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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;



    public ContactService(ContactRepository contactRepository, CompanyRepository companyRepository) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
    }

    public ContactResponse createContact(ContactRequest contactRequest, User currentUser) {

        if (contactRepository.findByEmailAndUserId(contactRequest.getEmail(), currentUser.getId()).isPresent()) {
            throw new DuplicateResourceException("Contact with this email already exists for the user");
        }

        Company company = companyRepository.findById(contactRequest.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Contact contact = new Contact();
        contact.setCompany(company);
        contact.setUser(currentUser);
        setApplicationAttributes(contactRequest, contact);

        return new ContactResponse(contactRepository.save(contact));
    }

    public List<ContactResponse> getUserContacts(User currentUser) {
        List<Contact> contacts = contactRepository.findAllByUserId(currentUser.getId());
        return contacts.stream().map(ContactResponse::new).toList();
    }

    public ContactResponse updateContact(Long contactId, ContactRequest contactRequest, User currentUser) {

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        if (!contact.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You do not have permission to update this contact");
        }

        setApplicationAttributes(contactRequest, contact);

        contactRepository.save(contact);

        return new ContactResponse(contact);
    }

    public void deleteContact(Long contactId, User currentUser) {

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        if (!contact.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You do not have permission to delete this contact");
        }

        contactRepository.delete(contact);
    }



    private void setApplicationAttributes(ContactRequest contactRequest, Contact contact) {
        contact.setName(contactRequest.getName());
        contact.setEmail(contactRequest.getEmail());
        contact.setRole(contactRequest.getRole());
        contact.setLinkedinUrl(contactRequest.getLinkedinUrl());
        contact.setNotes(contactRequest.getNotes());
        contact.setRelationshipType(contactRequest.getRelationshipType());
    }
}
