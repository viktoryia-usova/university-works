package server.server.business.entity.resource;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.server.business.entity.profile.Profile;

import java.time.LocalDateTime;

@Entity
@Table(name = "resource_favorites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceFavorite {

    @EmbeddedId
    private ResourceFavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("profileId")
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("resourceId")
    @JoinColumn(name = "resource_id")
    private FinancialResource resource;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
