package server.server.business.service.finance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.enums.DepositStatus;
import server.server.business.entity.finance.Deposit;
import server.server.business.entity.finance.Transaction;
import server.server.business.event.DepositMaturedEvent;
import server.server.business.factory.TransactionFactory;
import server.server.business.repository.finance.DepositRepository;
import server.server.business.repository.finance.TransactionRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Encapsulates the deposit-maturity processing logic so it can be called
 * both by the Quartz job (daily at 01:00) and at application startup
 * (to catch deposits that matured while the server was offline).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositMaturityService {

    private final DepositRepository      depositRepository;
    private final TransactionRepository  transactionRepository;
    private final TransactionFactory     transactionFactory;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Finds all ACTIVE deposits whose end_date ≤ today, closes them
     * and creates an INCOME transaction for each.
     *
     * @return number of deposits processed
     */
    @Transactional
    public int processMatureDeposits() {
        List<Deposit> matured = depositRepository
                .findByStatusAndEndDateLessThanEqualAndDeletedFalse(DepositStatus.ACTIVE, LocalDate.now());

        int count = 0;
        for (Deposit deposit : matured) {
            try {
                Transaction income = transactionFactory.createFromMaturedDeposit(deposit);
                transactionRepository.save(income);

                deposit.setStatus(DepositStatus.COMPLETED);
                deposit.setIncomeTransaction(income);
                depositRepository.save(deposit);

                eventPublisher.publishEvent(new DepositMaturedEvent(this, deposit));
                log.info("Deposit id={} matured → income transaction id={} created", deposit.getId(), income.getId());
                count++;
            } catch (Exception ex) {
                log.error("Failed to process matured deposit id={}: {}", deposit.getId(), ex.getMessage(), ex);
            }
        }
        return count;
    }
}
