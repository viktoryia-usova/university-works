package server.server.business.entity.finance;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.business.entity.enums.DepositStatus;
import server.server.business.entity.enums.DepositType;
import server.server.business.entity.profile.Profile;
import server.server.common.entity.classes.BaseSoftDeletingEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "deposits")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Deposit extends BaseSoftDeletingEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private DepositType type;

    @Column(name = "initial_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    /** Annual interest rate in percent. Null for PIGGY_BANK. */
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** Null means open-ended (PIGGY_BANK). */
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "current_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private DepositStatus status = DepositStatus.ACTIVE;

    /** Set when the scheduled job auto-creates the income transaction on maturity. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_transaction_id")
    private Transaction incomeTransaction;
}
