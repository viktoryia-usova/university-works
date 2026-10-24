package server.server.business.entity.shared;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.business.entity.enums.SharedTransactionType;
import server.server.common.entity.classes.BaseSoftDeletingEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "shared_transactions")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SharedTransaction extends BaseSoftDeletingEntity {

    /** Single FK replaces the previous (sheet_id, profile_id) pair.
     *  Sheet  → member.sheet
     *  Author → member.profile
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private SharedSheetMember member;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "exchange_rate_to_byn", nullable = false, precision = 19, scale = 6)
    private BigDecimal exchangeRateToByn;

    @Column(name = "amount_byn", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountByn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SharedTransactionType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate date;
}
