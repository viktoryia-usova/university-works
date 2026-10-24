package server.server.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.server.business.dto.social.ContactResponse;
import server.server.business.dto.social.ProfileSearchResponse;
import server.server.business.dto.social.SendContactRequestRequest;
import server.server.business.facade.social.ContactFacade;

@Tag(name = "Contacts", description = "Контакты и поиск пользователей")
@RestController
@RequestMapping("/api/v1/contacts")
@PreAuthorize("isAuthenticated()")
public class ContactController {

    private final ContactFacade contactFacade;

    public ContactController(ContactFacade contactFacade) {
        this.contactFacade = contactFacade;
    }

    @Operation(summary = "Список принятых контактов")
    @GetMapping
    public ResponseEntity<Page<ContactResponse>> getContacts(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(contactFacade.getMyContacts(search, pageable));
    }

    @Operation(summary = "Входящие запросы в контакты")
    @GetMapping("/requests")
    public ResponseEntity<Page<ContactResponse>> getPendingRequests(Pageable pageable) {
        return ResponseEntity.ok(contactFacade.getPendingRequests(pageable));
    }

    @Operation(summary = "Отправить запрос в контакты")
    @PostMapping("/requests")
    public ResponseEntity<ContactResponse> sendRequest(@Valid @RequestBody SendContactRequestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactFacade.sendRequest(request));
    }

    @Operation(summary = "Принять запрос")
    @PostMapping("/requests/{contactId}/accept")
    public ResponseEntity<ContactResponse> accept(@PathVariable Long contactId) {
        return ResponseEntity.ok(contactFacade.accept(contactId));
    }

    @Operation(summary = "Отклонить запрос")
    @PostMapping("/requests/{contactId}/decline")
    public ResponseEntity<ContactResponse> decline(@PathVariable Long contactId) {
        return ResponseEntity.ok(contactFacade.decline(contactId));
    }

    @Operation(summary = "Удалить контакт")
    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> remove(@PathVariable Long contactId) {
        contactFacade.remove(contactId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск пользователей (по email / телефону / friend code)")
    @GetMapping("/search")
    public ResponseEntity<Page<ProfileSearchResponse>> search(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.ok(contactFacade.searchProfiles(query, pageable));
    }
}
