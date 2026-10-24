package server.server.business.facade.social;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.social.ContactResponse;
import server.server.business.dto.social.ProfileSearchResponse;
import server.server.business.dto.social.SendContactRequestRequest;
import server.server.business.entity.profile.Profile;
import server.server.business.entity.social.Contact;
import server.server.business.mapper.social.ContactMapper;
import server.server.business.mapper.social.ProfileSearchMapper;
import server.server.business.service.profile.ProfileService;
import server.server.business.service.social.ContactService;
import server.server.common.util.SecurityUtils;

@Component
@Transactional
public class ContactFacade {

    private final ContactService      contactService;
    private final ProfileService      profileService;
    private final ContactMapper       contactMapper;
    private final ProfileSearchMapper searchMapper;

    public ContactFacade(ContactService contactService,
                         ProfileService profileService,
                         ContactMapper contactMapper,
                         ProfileSearchMapper searchMapper) {
        this.contactService = contactService;
        this.profileService = profileService;
        this.contactMapper  = contactMapper;
        this.searchMapper   = searchMapper;
    }

    public Page<ContactResponse> getMyContacts(String search, Pageable pageable) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return contactService.findAccepted(profile.getId(), search, pageable)
                .map(c -> contactMapper.toDto(c, profile.getId()));
    }

    public Page<ContactResponse> getPendingRequests(Pageable pageable) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return contactService.findPendingRequests(profile.getId(), pageable)
                .map(c -> contactMapper.toDto(c, profile.getId()));
    }

    public ContactResponse sendRequest(SendContactRequestRequest request) {
        Profile requester = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Profile addressee = profileService.findByQuery(request.query());
        Contact contact = contactService.sendRequest(requester, addressee);
        return contactMapper.toDto(contact, requester.getId());
    }

    public ContactResponse accept(Long contactId) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Contact contact = contactService.findById(contactId);
        return contactMapper.toDto(contactService.accept(contact, profile.getId()), profile.getId());
    }

    public ContactResponse decline(Long contactId) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Contact contact = contactService.findById(contactId);
        return contactMapper.toDto(contactService.decline(contact, profile.getId()), profile.getId());
    }

    public void remove(Long contactId) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Contact contact = contactService.findById(contactId);
        contactService.remove(contact, profile.getId());
    }

    public Page<ProfileSearchResponse> searchProfiles(String query, Pageable pageable) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return profileService.searchPublic(query, profile.getId(), pageable)
                .map(searchMapper::toDto);
    }
}
