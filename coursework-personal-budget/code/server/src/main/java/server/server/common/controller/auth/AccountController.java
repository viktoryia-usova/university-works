package server.server.common.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.common.dto.entity.account.AccountResponse;
import server.server.common.dto.entity.account.ChangeActiveRequest;
import server.server.common.dto.entity.account.ChangePasswordRequest;
import server.server.common.facade.entity.AccountFacade;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountFacade facade;

    @PatchMapping("/{id}/password")
    public ResponseEntity<AccountResponse> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(facade.changePassword(id, request));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<AccountResponse> changeActive(
            @PathVariable Long id,
            @Valid @RequestBody ChangeActiveRequest request) {
        return ResponseEntity.ok(facade.changeActive(id, request));
    }
}
