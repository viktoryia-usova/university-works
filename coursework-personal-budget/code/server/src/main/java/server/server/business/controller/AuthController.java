package server.server.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.business.dto.auth.RefreshTokenRequest;
import server.server.business.facade.auth.AuthFacade;
import server.server.common.dto.auth.key.CheckSignUpKeyRequest;
import server.server.common.dto.auth.sign_in.SignInRequest;
import server.server.common.dto.auth.sign_up.SignUpRequest;

@Tag(name = "Auth", description = "Регистрация, вход, токены")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @Operation(summary = "Запрос OTP-кода на email (шаг 1 регистрации)")
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        authFacade.signUp(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Подтверждение OTP и создание аккаунта (шаг 2 регистрации)")
    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@Valid @RequestBody CheckSignUpKeyRequest request) {
        return ResponseEntity.ok(authFacade.confirmRegistration(request));
    }

    @Operation(summary = "Вход по email + пароль")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(authFacade.signIn(request));
    }

    @Operation(summary = "Обновление access-токена по refresh-токену")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authFacade.refresh(request));
    }

    @Operation(summary = "Выход — инвалидирует refresh-токен")
    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut() {
        authFacade.signOut();
        return ResponseEntity.ok().build();
    }
}
