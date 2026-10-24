package server.server.business.factory;

import org.springframework.stereotype.Component;
import server.server.business.entity.enums.TransactionType;
import server.server.business.entity.finance.Deposit;
import server.server.business.entity.finance.Transaction;
import server.server.common.client.NbrbExchangeRateClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Factory pattern: centralises creation of {@link Transaction} instances
 * from domain-specific contexts (e.g. deposit maturity).
 */
@Component
public class TransactionFactory {

    private final NbrbExchangeRateClient nbrbClient;

    public TransactionFactory(NbrbExchangeRateClient nbrbClient) {
        this.nbrbClient = nbrbClient;
    }

    /**
     * Creates an INCOME transaction for a matured deposit.
     * Amount = principal + accrued interest (simple annual interest).
     */
    public Transaction createFromMaturedDeposit(Deposit deposit) {
        BigDecimal income = computeIncome(deposit);
        BigDecimal exchangeRate = nbrbClient.getRateToByn(deposit.getCurrency());
        BigDecimal amountByn = income.multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);

        return Transaction.builder()
                .profile(deposit.getProfile())
                .category(null)
                .amount(income)
                .currency(deposit.getCurrency())
                .exchangeRateToByn(exchangeRate)
                .amountByn(amountByn)
                .type(TransactionType.INCOME)
                .date(LocalDate.now())
                .description("Вклад: " + deposit.getName())
                .build();
    }

    private BigDecimal computeIncome(Deposit deposit) {
        if (deposit.getInterestRate() == null || deposit.getEndDate() == null || deposit.getStartDate() == null) {
            return deposit.getCurrentAmount();
        }
        long days = ChronoUnit.DAYS.between(deposit.getStartDate(), deposit.getEndDate());
        BigDecimal interest = deposit.getInitialAmount()
                .multiply(deposit.getInterestRate())
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(days))
                .divide(BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);
        return deposit.getInitialAmount().add(interest);
    }
}
