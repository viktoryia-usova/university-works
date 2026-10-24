package server.server.common.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import server.server.common.dto.auth.sign_in.ISignInRequest;
import server.server.common.dto.auth.sign_up.ISignUpRequest;
import server.server.common.entity.auth.Account;
import server.server.common.entity.auth.RefreshToken;
import server.server.common.entity.enums.Role;
import server.server.common.exception.auth.AuthException;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.repository.auth.RefreshTokenRepository;
import server.server.common.util.jwt.JwtInfo;
import server.server.common.util.jwt.JwtUtils;
import server.server.common.util.log.AppLogger;

import java.time.Instant;

public abstract class BaseAuthorizationService {

    private static final String LOG = AppLogger.name(BaseAuthorizationService.class);

    protected final AccountRepository     repository;
    protected final PasswordEncoder       passwordEncoder;
    protected final AuthenticationManager authenticationManager;
    protected final JwtUtils              jwtUtils;
    protected final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    protected BaseAuthorizationService(AccountRepository repository,
                                       PasswordEncoder passwordEncoder,
                                       AuthenticationManager authenticationManager,
                                       JwtUtils jwtUtils,
                                       RefreshTokenRepository refreshTokenRepository) {
        this.repository             = repository;
        this.passwordEncoder        = passwordEncoder;
        this.authenticationManager  = authenticationManager;
        this.jwtUtils               = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public JwtInfo signIn(ISignInRequest request) {
        AppLogger.info("Sign-in attempt: " + request.getUsername(), LOG);
        JwtInfo info = signIn(request.getUsername(), request.getPassword());
        AppLogger.info("Sign-in successful: " + request.getUsername(), LOG);
        return info;
    }

    protected JwtInfo signIn(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            Account account = (Account) auth.getPrincipal();
            return issueTokenPair(account);
        } catch (DisabledException e) {
            AppLogger.warn("Sign-in rejected - account disabled: " + username, LOG);
            throw new AuthException("Account is disabled", "INACTIVE");
        } catch (BadCredentialsException e) {
            AppLogger.warn("Sign-in rejected - bad credentials: " + username, LOG);
            throw new AuthException("Invalid username or password", "CREDENTIALS");
        }
    }

    /** Issues a new access + refresh token pair and persists the refresh token. */
    protected JwtInfo issueTokenPair(Account account) {
        String accessToken  = jwtUtils.generateAccessToken(account);
        String rawRefresh   = jwtUtils.generateRefreshToken();
        saveRefreshToken(account, rawRefresh);
        return new JwtInfo(account, accessToken, rawRefresh);
    }

    private void saveRefreshToken(Account account, String rawToken) {
        // Delete any existing token for this account (one-per-account policy)
        refreshTokenRepository.deleteByAccountId(account.getId());
        RefreshToken rt = RefreshToken.builder()
                .account(account)
                .token(rawToken)
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(rt);
    }

    /** Rotates refresh token: validates old, issues new pair. */
    public JwtInfo refresh(String rawRefreshToken) {
        RefreshToken rt = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new AuthException("Refresh token not found", "REFRESH_TOKEN"));
        if (rt.getRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(rt);
            throw new AuthException("Refresh token expired or revoked", "REFRESH_TOKEN");
        }
        Account account = rt.getAccount();
        return issueTokenPair(account);
    }

    /** Revokes all refresh tokens for the given account (sign-out). */
    public void signOut(Long accountId) {
        refreshTokenRepository.deleteByAccountId(accountId);
        AppLogger.info("Sign-out: refresh tokens revoked for accountId=" + accountId, LOG);
    }

    public abstract void signUp(ISignUpRequest request);

    protected abstract void createRelatedEntities(Account account);

    protected void checkUsernameUniqueness(String username) {
        if (repository.findByUsername(username).isPresent()) {
            AppLogger.warn("Sign-up rejected - username already exists: " + username, LOG);
            throw new AuthException("Username already taken: " + username, "USERNAME");
        }
    }

    protected Account buildAndSaveAccount(String username, String password, Role role) {
        Account account = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .active(true)
                .build();
        return repository.save(account);
    }
}
