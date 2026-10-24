package server.server.business.entity.finance;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.business.entity.enums.PeriodType;
import server.server.business.entity.enums.PlannedExpenseType;
import server.server.business.entity.profile.Profile;
import server.server.common.entity.classes.BaseSoftDeletingEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "planned_expenses")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PlannedExpense extends BaseSoftDeletingEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlannedExpenseType type;

    /** Only for BUDGET_LIMIT */
    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", length = 15)
    private PeriodType periodType;

    /** Only for PLANNED_PURCHASE */
    @Column(name = "planned_date")
    private LocalDate plannedDate;

    /** Only for PLANNED_PURCHASE */
    @Column(name = "is_fulfilled", nullable = false)
    @Builder.Default
    private Boolean isFulfilled = false;
}
