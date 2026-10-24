package server.server.business.entity.profile;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.common.entity.classes.BaseEntity;

@Entity
@Table(name = "privacy_settings")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySettings extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @Column(name = "searchable_by_email", nullable = false)
    @Builder.Default
    private Boolean searchableByEmail = true;

    @Column(name = "searchable_by_phone", nullable = false)
    @Builder.Default
    private Boolean searchableByPhone = false;

    @Column(name = "searchable_by_friend_code", nullable = false)
    @Builder.Default
    private Boolean searchableByFriendCode = true;

    @Column(name = "show_full_name", nullable = false)
    @Builder.Default
    private Boolean showFullName = true;
}
