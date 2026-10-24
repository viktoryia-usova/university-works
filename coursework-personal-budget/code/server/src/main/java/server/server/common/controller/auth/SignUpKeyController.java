package server.server.common.controller.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.server.common.controller.common.BaseCreateOnlyController;
import server.server.common.dto.entity.key.CreateSignUpKeyRequest;
import server.server.common.dto.entity.key.SignUpKeyResponse;
import server.server.common.entity.auth.SignUpKey;
import server.server.common.entity.enums.Role;
import server.server.common.facade.entity.SignUpKeyFacade;

@RestController
@RequestMapping("/api/sign-up-key")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class SignUpKeyController extends BaseCreateOnlyController<SignUpKey, Long, SignUpKeyResponse, CreateSignUpKeyRequest, SignUpKeyFacade> {
    public SignUpKeyController(SignUpKeyFacade facade) {
        super(facade);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateSignUpKeyRequest request) {
        return ResponseEntity.ok(doCreate(request));
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) Role role) {
        return ResponseEntity.ok(facade.findByRole(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResponseEntity.ok(doDeleteById(id));
    }
}
