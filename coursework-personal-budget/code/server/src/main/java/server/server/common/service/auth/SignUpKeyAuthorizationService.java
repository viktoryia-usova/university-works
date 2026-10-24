package server.server.common.service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import server.server.common.dto.auth.key.ICheckSignUpKeyRequest;
import server.server.common.entity.enums.Role;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.repository.auth.RefreshTokenRepository;
import server.server.common.service.entity.SignUpKeyService;
import server.server.common.service.util.MailService;
import server.server.common.util.jwt.JwtUtils;

public abstract class SignUpKeyAuthorizationService<ConfirmR extends ICheckSignUpKeyRequest>
        extends BaseSignUpKeyAuthorizationService<ConfirmR> {

    protected final SignUpKeyService signUpKeyService;

    protected SignUpKeyAuthorizationService(AccountRepository repository,
                                            PasswordEncoder passwordEncoder,
                                            AuthenticationManager authenticationManager,
                                            JwtUtils jwtUtils,
                                            RefreshTokenRepository refreshTokenRepository,
                                            MailService mailService,
                                            SignUpKeyService signUpKeyService) {
        super(repository, passwordEncoder, authenticationManager, jwtUtils, refreshTokenRepository, mailService);
        this.signUpKeyService = signUpKeyService;
    }

    @Override
    protected String generateAndSaveKey(String username, Role role) {
        return signUpKeyService.generate(username, role);
    }

    @Override
    protected Role verifyKey(String username, String key) {
        return signUpKeyService.verify(username, key);
    }

    @Override
    protected void clearKeys(String username) {
        signUpKeyService.clear(username);
    }
}
