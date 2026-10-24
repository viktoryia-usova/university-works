package server.server.common.config.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import server.server.business.service.profile.ProfileService;
import server.server.common.entity.auth.Account;
import server.server.common.entity.enums.Role;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.util.jwt.JwtInfo;
import server.server.common.util.jwt.JwtUtils;
import server.server.common.util.log.AppLogger;
import server.server.common.entity.auth.RefreshToken;
import server.server.common.repository.auth.RefreshTokenRepository;

import java.io.IOException;
import java.time.Instant;

/**
 * Handles successful Google OAuth2 authentication.
 * <p>
 * On first login: creates {@link Account} + {@link server.server.business.entity.profile.Profile}
 * with defaults. On subsequent logins: reuses the existing account.
 * <p>
 * In both cases issues a JWT access + refresh token pair and redirects to the
 * frontend callback URL with tokens as query parameters.
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String LOG = AppLogger.name(OAuth2AuthenticationSuccessHandler.class);

    private final AccountRepository      accountRepository;
    private final ProfileService         profileService;
    private final JwtUtils               jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    /** Frontend URL that receives the tokens after OAuth2 login. */
    @Value("${app.oauth2.redirect-uri:http://localhost:5173/oauth2/callback}")
    private String redirectUri;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email     = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName  = oAuth2User.getAttribute("family_name");

        if (email == null) {
            AppLogger.error("OAuth2 login: email attribute missing in OAuth2User attributes", LOG);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by OAuth2 provider");
            return;
        }

        Account account = accountRepository.findByUsername(email)
                .orElseGet(() -> registerNewOAuthAccount(email, firstName, lastName));

        if (!account.isEnabled()) {
            AppLogger.warn("OAuth2 login rejected - account disabled: " + email, LOG);
            String errorTarget = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "INACTIVE")
                    .build().toUriString();
            getRedirectStrategy().sendRedirect(request, response, errorTarget);
            return;
        }

        JwtInfo tokens = issueTokenPair(account);

        String target = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken",  tokens.getAccessToken())
                .queryParam("refreshToken", tokens.getRefreshToken())
                .build().toUriString();

        AppLogger.info("OAuth2 login success for: " + email, LOG);
        getRedirectStrategy().sendRedirect(request, response, target);
    }

    // ---- helpers ----

    private Account registerNewOAuthAccount(String email, String firstName, String lastName) {
        Account account = Account.builder()
                .username(email)
                .password("")          // no password for OAuth-only accounts
                .role(Role.USER)
                .active(true)
                .build();
        account = accountRepository.save(account);

        String fn = firstName != null ? firstName : "";
        String ln = lastName  != null ? lastName  : "";
        profileService.createWithDefaults(account, fn, ln);

        AppLogger.info("OAuth2 auto-registered new account: " + email, LOG);
        return account;
    }

    private JwtInfo issueTokenPair(Account account) {
        String accessToken = jwtUtils.generateAccessToken(account);
        String rawRefresh  = jwtUtils.generateRefreshToken();

        refreshTokenRepository.deleteByAccountId(account.getId());
        RefreshToken rt = RefreshToken.builder()
                .account(account)
                .token(rawRefresh)
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(rt);

        return new JwtInfo(account, accessToken, rawRefresh);
    }
}
