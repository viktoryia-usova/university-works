package server.server.common.service.entity;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import server.server.common.entity.auth.SignUpKey;
import server.server.common.entity.enums.Role;
import server.server.common.exception.auth.AuthException;
import server.server.common.repository.auth.SignUpKeyRepository;
import server.server.common.service.common.BaseCRUDHardDeletingService;
import server.server.common.util.log.AppLogger;

import java.security.SecureRandom;
import java.util.List;

@Service
public class SignUpKeyService extends BaseCRUDHardDeletingService<SignUpKey, Long, SignUpKeyRepository> {

    private static final String LOG = AppLogger.name(SignUpKeyService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int KEY_BOUND = 100_000_000; // 8 digits: 00000000–99999999
    public SignUpKeyService(SignUpKeyRepository repository) {
        super(repository, SignUpKey.class);
    }

    public String generate(String username, Role role) {
        SignUpKey entity = buildAndSave(username, role);
        AppLogger.info("Registration key generated for: " + username, LOG);
        AppLogger.debug("Registration key: " + entity.getKey(), LOG);
        return entity.getKey();
    }

    public Role verify(String username, String key) {
        return repository.findByUsernameAndKey(username, key)
                .map(SignUpKey::getRole)
                .orElseThrow(() -> {
                    AppLogger.warn("Invalid registration key for: " + username, LOG);
                    return new AuthException("Invalid or expired registration key", "KEY");
                });
    }

    @Transactional
    public void clear(String username) {
        repository.deleteAllByUsername(username);
        AppLogger.info("Registration keys cleared for: " + username, LOG);
    }

    public SignUpKey create(String username, Role role) {
        SignUpKey entity = buildAndSave(username, role);
        AppLogger.info("Created registration key for: " + username + " [" + role + "]", LOG);
        return entity;
    }

    public List<SignUpKey> findByRole(Role role) {
        return  role != null
                ? repository.findByRole(role)
                : repository.findAll();
    }

    private SignUpKey buildAndSave(String username, Role role) {
        String key = String.format("%08d", RANDOM.nextInt(KEY_BOUND));
        return repository.save(SignUpKey.builder()
                .username(username)
                .key(key)
                .role(role)
                .build());
    }
}
