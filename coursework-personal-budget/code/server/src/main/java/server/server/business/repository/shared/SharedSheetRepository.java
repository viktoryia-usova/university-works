package server.server.business.repository.shared;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.server.business.entity.shared.SharedSheet;

public interface SharedSheetRepository extends JpaRepository<SharedSheet, Long> {

    /** All sheets where the profile is creator OR member */
    @Query("""
        SELECT DISTINCT s FROM SharedSheet s
        LEFT JOIN s.members m
        WHERE (s.creator.id = :profileId OR m.profile.id = :profileId)
          AND s.deleted = false
        """)
    Page<SharedSheet> findAllForProfile(Long profileId, Pageable pageable);
}
