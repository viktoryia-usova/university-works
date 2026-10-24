package server.server.business.facade.auth;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.auth.RefreshTokenRequest;
import server.server.business.service.auth.AuthorizationService;
import server.server.common.dto.auth.key.CheckSignUpKeyRequest;
import server.server.common.dto.auth.sign_in.SignInRequest;
import server.server.common.dto.auth.sign_up.SignUpRequest;
import server.server.common.util.SecurityUtils;
import server.server.common.util.jwt.JwtInfo;

@Component
@Transactional
public class AuthFacade {

    private final AuthorizationService authorizationService;

    public AuthFacade(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void signUp(SignUpRequest request) {
        authorizationService.signUp(request);
    }

    public JwtInfo confirmRegistration(CheckSignUpKeyRequest request) {
        return authorizationService.checkKey(request);
    }

    public JwtInfo signIn(SignInRequest request) {
        return authorizationService.signIn(request);
    }

    public JwtInfo refresh(RefreshTokenRequest request) {
        return authorizationService.refresh(request.refreshToken());
    }

    public void signOut() {
        authorizationService.signOut(SecurityUtils.currentAccountId());
    }
}
