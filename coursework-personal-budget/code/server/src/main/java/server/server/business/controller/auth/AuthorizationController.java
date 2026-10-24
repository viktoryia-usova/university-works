package server.server.business.controller.auth;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.business.service.auth.AuthorizationService;
import server.server.common.dto.auth.key.CheckSignUpKeyRequest;
import server.server.common.dto.auth.sign_in.SignInRequest;
import server.server.common.dto.auth.sign_up.SignUpRequest;
import server.server.common.dto.util.MessageResponse;
import server.server.common.util.jwt.JwtInfo;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthorizationController {

    private final AuthorizationService service;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        service.signUp(request);
        return ResponseEntity.ok(new MessageResponse("Sign up code sent to " + request.getUsername()));
    }

    @PostMapping("/check-key")
    public ResponseEntity<JwtInfo> checkKey(@Valid @RequestBody CheckSignUpKeyRequest request) {
        return ResponseEntity.ok(service.checkKey(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtInfo> signIn(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(service.signIn(request));
    }
}
