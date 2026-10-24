package server.server.common.service.entity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import server.server.common.entity.auth.Account;
import server.server.common.entity.enums.Role;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.exception.BaseKeyException;
import server.server.common.service.common.BaseCRUDHardDeletingService;
import server.server.common.util.log.AppLogger;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService extends BaseCRUDHardDeletingService<Account, Long, AccountRepository> {

    private static final String LOG = AppLogger.name(AccountService.class);

    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, PasswordEncoder passwordEncoder) {
        super(repository, Account.class);
        this.passwordEncoder = passwordEncoder;
    }

    public Account changePassword(Long id, String oldRawPassword, String newRawPassword) {
        AppLogger.info("Changing password for account id=" + id, LOG);
        Account account = findById(id);
        if (!passwordEncoder.matches(oldRawPassword, account.getPassword())) {
            AppLogger.warn("Password change rejected - wrong old password for account id=" + id, LOG);
            throw new WrongPasswordException();
        }
        account.setPassword(passwordEncoder.encode(newRawPassword));
        return update(account);
    }

    public static class WrongPasswordException extends BaseKeyException {
        public WrongPasswordException() {
            super("Неверный текущий пароль", "PASSWORD");
        }
    }

    public Account changeActive(Long id, Boolean active) {
        AppLogger.info("Changing active status for account id=" + id + " to " + active, LOG);
        Account account = findById(id);
        account.setActive(active);
        return update(account);
    }

    public Optional<Account> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public List<Account> findByRole(Role role) {
        return repository.findByRole(role);
    }
}
