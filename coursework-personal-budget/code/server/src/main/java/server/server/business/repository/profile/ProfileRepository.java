package server.server.business.repository.profile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.server.business.entity.profile.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByAccount_Id(Long accountId);
    Optional<Profile> findByAccount_Username(String username);
    Optional<Profile> findByFriendCode(String friendCode);
    boolean existsByFriendCode(String friendCode);

    /** Search respecting privacy settings */
    @Query("""
        SELECT p FROM Profile p
        JOIN p.account a
        LEFT JOIN p.privacySettings ps
        WHERE a.active = true
          AND (
              (ps.searchableByEmail      = true AND LOWER(a.username)   LIKE LOWER(CONCAT('%', :query, '%')))
           OR (ps.searchableByPhone      = true AND LOWER(p.phone)      LIKE LOWER(CONCAT('%', :query, '%')))
           OR (ps.searchableByFriendCode = true AND LOWER(p.friendCode) LIKE LOWER(CONCAT('%', :query, '%')))
           OR (ps.showFullName           = true AND (
                  LOWER(p.firstName)                          LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(p.lastName)                           LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))
           ))
          )
          AND p.id <> :excludeProfileId
        """)
    Page<Profile> searchPublicProfiles(
            @org.springframework.data.repository.query.Param("query") String query,
            @org.springframework.data.repository.query.Param("excludeProfileId") Long excludeProfileId,
            Pageable pageable);
}
