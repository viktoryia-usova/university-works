package server.server.business.service.social;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.enums.ContactStatus;
import server.server.business.entity.profile.Profile;
import server.server.business.entity.social.Contact;
import server.server.business.event.ContactRequestEvent;
import server.server.business.repository.social.ContactRepository;
import server.server.common.exception.BaseKeyException;
import server.server.common.exception.common.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<Contact> findAccepted(Long profileId, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return contactRepository.searchAcceptedContacts(profileId, search, pageable);
        }
        return contactRepository.findAllByProfileAndStatus(profileId, ContactStatus.ACCEPTED, pageable);
    }

    public Page<Contact> findPendingRequests(Long profileId, Pageable pageable) {
        return contactRepository.findPendingRequestsForProfile(profileId, pageable);
    }

    public Contact findById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "CONTACT_NOT_FOUND", Contact.class));
    }

    public Contact sendRequest(Profile requester, Profile addressee) {
        if (requester.getId().equals(addressee.getId())) {
            throw new ContactSelfRequestException();
        }

        var existing = contactRepository.findByParticipants(requester.getId(), addressee.getId());
        if (existing.isPresent()) {
            Contact contact = existing.get();
            if (contact.getStatus() != ContactStatus.DECLINED) {
                throw new ContactAlreadyExistsException();
            }
            // Reuse the existing row to avoid delete+insert constraint issues:
            // reset requester/addressee and set status back to PENDING
            contact.setRequester(requester);
            contact.setAddressee(addressee);
            contact.setStatus(ContactStatus.PENDING);
            Contact saved = contactRepository.save(contact);
            eventPublisher.publishEvent(new ContactRequestEvent(this, saved));
            return saved;
        }

        Contact contact = contactRepository.save(Contact.builder()
                .requester(requester)
                .addressee(addressee)
                .status(ContactStatus.PENDING)
                .build());

        // Observer pattern: publish event for email notification
        eventPublisher.publishEvent(new ContactRequestEvent(this, contact));
        return contact;
    }

    public Contact accept(Contact contact, Long addresseeProfileId) {
        assertAddresseeOwnership(contact, addresseeProfileId);
        contact.setStatus(ContactStatus.ACCEPTED);
        return contactRepository.save(contact);
    }

    public Contact decline(Contact contact, Long addresseeProfileId) {
        assertAddresseeOwnership(contact, addresseeProfileId);
        contact.setStatus(ContactStatus.DECLINED);
        return contactRepository.save(contact);
    }

    public void remove(Contact contact, Long profileId) {
        assertParticipant(contact, profileId);
        contactRepository.delete(contact);
    }

    public boolean areContacts(Long profileId1, Long profileId2) {
        return contactRepository.areContacts(profileId1, profileId2);
    }

    private void assertAddresseeOwnership(Contact contact, Long profileId) {
        if (!contact.getAddressee().getId().equals(profileId)) {
            throw new ContactAccessDeniedException();
        }
    }

    private void assertParticipant(Contact contact, Long profileId) {
        boolean isRequester = contact.getRequester().getId().equals(profileId);
        boolean isAddressee = contact.getAddressee().getId().equals(profileId);
        if (!isRequester && !isAddressee) {
            throw new ContactAccessDeniedException();
        }
    }

    public static class ContactAccessDeniedException extends BaseKeyException {
        public ContactAccessDeniedException() { super("Not a participant of this contact", "CONTACT_ACCESS_DENIED"); }
    }
    public static class ContactAlreadyExistsException extends BaseKeyException {
        public ContactAlreadyExistsException() { super("Contact request already exists", "CONTACT_EXISTS"); }
    }
    public static class ContactSelfRequestException extends BaseKeyException {
        public ContactSelfRequestException() { super("Cannot send contact request to yourself", "CONTACT_SELF"); }
    }
}
