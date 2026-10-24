package server.server.business.entity.shared;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.business.entity.profile.Profile;
import server.server.common.entity.classes.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "shared_sheet_members",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_sheet_member",
        columnNames = {"sheet_id", "profile_id"}
    )
)
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SharedSheetMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_id", nullable = false)
    private SharedSheet sheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}
