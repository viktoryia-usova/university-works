package server.server.common.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.common.entity.auth.Account;
import server.server.common.entity.enums.Role;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);

    List<Account> findByRole(Role role);
}
