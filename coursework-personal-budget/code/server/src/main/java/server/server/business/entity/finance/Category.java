package server.server.business.entity.finance;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.business.entity.enums.CategoryType;
import server.server.business.entity.profile.Profile;
import server.server.common.entity.classes.BaseSoftDeletingEntity;

@Entity
@Table(name = "categories")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseSoftDeletingEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 7)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CategoryType type;
}
