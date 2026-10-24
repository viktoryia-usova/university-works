package server.server.common.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.common.entity.auth.SignUpKey;
import server.server.common.entity.enums.Role;

import java.util.List;
import java.util.Optional;

public interface SignUpKeyRepository extends JpaRepository<SignUpKey, Long> {

    Optional<SignUpKey> findByUsernameAndKey(String username, String key);

    List<SignUpKey> findByRole(Role role);

    void deleteAllByUsername(String username);
}
