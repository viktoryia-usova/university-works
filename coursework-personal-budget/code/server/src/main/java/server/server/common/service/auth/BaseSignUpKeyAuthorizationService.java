package server.server.common.service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import server.server.common.dto.auth.key.ICheckSignUpKeyRequest;
import server.server.common.dto.auth.sign_up.ISignUpRequest;
import server.server.common.entity.auth.Account;
import server.server.common.entity.enums.Role;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.repository.auth.RefreshTokenRepository;
import server.server.common.service.util.MailService;
import server.server.common.util.jwt.JwtInfo;
import server.server.common.util.jwt.JwtUtils;
import server.server.common.util.log.AppLogger;

public abstract class BaseSignUpKeyAuthorizationService<ConfirmR extends ICheckSignUpKeyRequest>
        extends BaseAuthorizationService {

    private static final String LOG = AppLogger.name(BaseSignUpKeyAuthorizationService.class);

    protected final MailService mailService;

    protected BaseSignUpKeyAuthorizationService(AccountRepository repository,
                                                PasswordEncoder passwordEncoder,
                                                AuthenticationManager authenticationManager,
                                                JwtUtils jwtUtils,
                                                RefreshTokenRepository refreshTokenRepository,
                                                MailService mailService) {
        super(repository, passwordEncoder, authenticationManager, jwtUtils, refreshTokenRepository);
        this.mailService = mailService;
    }

    @Override
    public void signUp(ISignUpRequest request) {
        AppLogger.info("Key-based sign-up initiation: " + request.getUsername(), LOG);
        checkUsernameUniqueness(request.getUsername());
        String key = generateAndSaveKey(request.getUsername(), resolveSignUpRole(request));
        mailService.send(buildEmailTitle(), buildEmailBody(key), request.getUsername());
        AppLogger.info("Registration key sent to: " + request.getUsername(), LOG);
    }

    public JwtInfo checkKey(ConfirmR request) {
        AppLogger.info("Sign-up key check: " + request.getUsername(), LOG);
        checkUsernameUniqueness(request.getUsername());
        Role role = verifyKey(request.getUsername(), request.getKey());
        Account account = buildAndSaveAccount(request.getUsername(), request.getPassword(), role);
        clearKeys(request.getUsername());
        createRelatedEntities(account);
        AppLogger.info("User confirmed and registered: " + request.getUsername(), LOG);
        return signIn(request.getUsername(), request.getPassword());
    }

    protected abstract String generateAndSaveKey(String username, Role role);

    protected abstract Role verifyKey(String username, String key);

    protected abstract void clearKeys(String username);

    protected Role resolveSignUpRole(ISignUpRequest request) {
        return Role.USER;
    }

    protected String buildEmailTitle() {
        return "Your registration code";
    }

    protected String buildEmailBody(String key) {
        return "Your registration code: " + key + "\nDo not share this code with anyone.";
    }
}
