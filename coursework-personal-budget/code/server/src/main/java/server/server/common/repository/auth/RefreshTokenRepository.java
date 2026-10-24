package server.server.common.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import server.server.common.entity.auth.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByAccount_Id(Long accountId);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.account.id = :accountId")
    void deleteByAccountId(Long accountId);
}
