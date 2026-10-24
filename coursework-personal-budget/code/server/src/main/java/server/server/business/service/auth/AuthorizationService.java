package server.server.business.service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import server.server.business.service.profile.ProfileService;
import server.server.common.dto.auth.key.CheckSignUpKeyRequest;
import server.server.common.entity.auth.Account;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.repository.auth.RefreshTokenRepository;
import server.server.common.service.auth.SignUpKeyAuthorizationService;
import server.server.common.service.entity.SignUpKeyService;
import server.server.common.service.util.MailService;
import server.server.common.util.jwt.JwtUtils;

@Service
public class AuthorizationService extends SignUpKeyAuthorizationService<CheckSignUpKeyRequest> {

    private final ProfileService profileService;

    public AuthorizationService(AccountRepository repository,
                                PasswordEncoder passwordEncoder,
                                AuthenticationManager authenticationManager,
                                JwtUtils jwtUtils,
                                RefreshTokenRepository refreshTokenRepository,
                                MailService mailService,
                                SignUpKeyService signUpKeyService,
                                ProfileService profileService) {
        super(repository, passwordEncoder, authenticationManager, jwtUtils,
              refreshTokenRepository, mailService, signUpKeyService);
        this.profileService = profileService;
    }

    @Override
    protected void createRelatedEntities(Account account) {
        profileService.createWithDefaults(account, "", "");
    }
}
