package server.server.business.entity.social;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.business.entity.enums.ContactStatus;
import server.server.business.entity.profile.Profile;
import server.server.common.entity.classes.BaseEntity;

@Entity
@Table(
    name = "contacts",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_contacts_requester_addressee",
        columnNames = {"requester_id", "addressee_id"}
    )
)
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Contact extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Profile requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressee_id", nullable = false)
    private Profile addressee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ContactStatus status = ContactStatus.PENDING;
}
