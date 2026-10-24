package server.server.business.repository.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.server.business.entity.resource.FinancialResource;
import server.server.business.entity.resource.ResourceFavorite;
import server.server.business.entity.resource.ResourceFavoriteId;

import java.util.Set;

public interface ResourceFavoriteRepository extends JpaRepository<ResourceFavorite, ResourceFavoriteId> {

    boolean existsByIdProfileIdAndIdResourceId(Long profileId, Long resourceId);

    @Modifying
    @Query("DELETE FROM ResourceFavorite f WHERE f.id.profileId = :profileId AND f.id.resourceId = :resourceId")
    void deleteByProfileIdAndResourceId(@Param("profileId") Long profileId,
                                        @Param("resourceId") Long resourceId);

    @Query("SELECT f.id.resourceId FROM ResourceFavorite f WHERE f.id.profileId = :profileId")
    Set<Long> findResourceIdsByProfileId(@Param("profileId") Long profileId);

    @Query("""
        SELECT f.resource FROM ResourceFavorite f
        WHERE f.id.profileId = :profileId
          AND f.resource.deleted = false
        ORDER BY f.createdAt DESC
        """)
    Page<FinancialResource> findFavoriteResourcesByProfileId(
            @Param("profileId") Long profileId,
            Pageable pageable);
}
