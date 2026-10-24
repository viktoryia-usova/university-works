package server.server.business.entity.profile;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.common.entity.classes.BaseEntity;

@Entity
@Table(name = "notification_settings")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @Column(name = "on_goal_reached", nullable = false)
    @Builder.Default
    private Boolean onGoalReached = true;

    @Column(name = "on_budget_exceeded", nullable = false)
    @Builder.Default
    private Boolean onBudgetExceeded = true;

    @Column(name = "on_shared_transaction", nullable = false)
    @Builder.Default
    private Boolean onSharedTransaction = true;

    @Column(name = "on_contact_request", nullable = false)
    @Builder.Default
    private Boolean onContactRequest = true;

    @Column(name = "on_deposit_matured", nullable = false)
    @Builder.Default
    private Boolean onDepositMatured = true;
}
