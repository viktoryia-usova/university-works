package server.server.common.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import server.server.common.dto.auth.init.IInitRequest;
import server.server.common.entity.auth.Account;
import server.server.common.entity.enums.Role;
import server.server.common.exception.auth.AlreadyInitializedException;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.util.log.AppLogger;

public abstract class BaseInitService {

    private static final String LOG = AppLogger.name(BaseInitService.class);

    protected final AccountRepository repository;
    protected final PasswordEncoder passwordEncoder;

    protected BaseInitService(AccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void init(IInitRequest request) {
        AppLogger.info("Init attempt: " + request.getUsername(), LOG);
        if (!repository.findByRole(Role.ADMIN).isEmpty()) {
            AppLogger.warn("Init rejected - application already initialized", LOG);
            throw new AlreadyInitializedException();
        }
        Account admin = buildAndSaveAccount(request.getUsername(), request.getPassword(), Role.ADMIN);
        afterAdminCreated(admin);
        AppLogger.info("Admin account created: " + request.getUsername(), LOG);
    }

    // Переопредели в business-слое для доп. действий при инициализации
    protected void afterAdminCreated(Account admin) {}

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
