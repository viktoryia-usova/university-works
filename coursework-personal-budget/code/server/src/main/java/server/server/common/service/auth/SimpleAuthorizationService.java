package server.server.common.service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import server.server.common.dto.auth.sign_up.ISignUpRequest;
import server.server.common.entity.auth.Account;
import server.server.common.entity.enums.Role;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.repository.auth.RefreshTokenRepository;
import server.server.common.util.jwt.JwtUtils;
import server.server.common.util.log.AppLogger;


public abstract class SimpleAuthorizationService extends BaseAuthorizationService {

    private static final String LOG = AppLogger.name(SimpleAuthorizationService.class);

    protected SimpleAuthorizationService(AccountRepository repository,
                                         PasswordEncoder passwordEncoder,
                                         AuthenticationManager authenticationManager,
                                         JwtUtils jwtUtils,
                                         RefreshTokenRepository refreshTokenRepository) {
        super(repository, passwordEncoder, authenticationManager, jwtUtils, refreshTokenRepository);
    }

    @Override
    public void signUp(ISignUpRequest request) {
        AppLogger.info("Simple sign-up: " + request.getUsername(), LOG);
        checkUsernameUniqueness(request.getUsername());
        Account account = buildAndSaveAccount(request.getUsername(), request.getPassword(), Role.USER);
        createRelatedEntities(account);
        AppLogger.info("User registered successfully: " + request.getUsername(), LOG);
    }
}
