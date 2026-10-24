package server.server.business.service.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.enums.TransactionType;
import server.server.business.entity.finance.Transaction;
import server.server.business.entity.profile.Profile;
import server.server.business.repository.finance.TransactionRepository;
import server.server.common.exception.BaseKeyException;
import server.server.common.exception.common.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Page<Transaction> findByProfile(Long profileId,
                                           TransactionType type,
                                           Long categoryId,
                                           LocalDate dateFrom,
                                           LocalDate dateTo,
                                           String currency,
                                           Pageable pageable) {
        Specification<Transaction> spec = buildSpec(profileId, type, categoryId, dateFrom, dateTo, currency);
        return transactionRepository.findAll(spec, pageable);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "TRANSACTION_NOT_FOUND", Transaction.class));
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void delete(Transaction transaction) {
        transaction.setDeleted(true);
        transactionRepository.save(transaction);
    }

    public void assertOwnership(Transaction transaction, Long profileId) {
        if (!transaction.getProfile().getId().equals(profileId)) {
            throw new TransactionAccessDeniedException();
        }
    }

    public BigDecimal sumIncomeByn(Long profileId, LocalDate from, LocalDate to) {
        return transactionRepository.sumByProfileAndTypeAndDateRange(profileId, TransactionType.INCOME, from, to);
    }

    public BigDecimal sumExpenseByn(Long profileId, LocalDate from, LocalDate to) {
        return transactionRepository.sumByProfileAndTypeAndDateRange(profileId, TransactionType.EXPENSE, from, to);
    }

    public List<Object[]> groupByCategoryByn(Long profileId, TransactionType type, LocalDate from, LocalDate to) {
        return transactionRepository.groupByCategoryAndDateRange(profileId, type, from, to);
    }

    public List<Object[]> groupByMonthByn(Long profileId, TransactionType type, LocalDate from, LocalDate to) {
        return transactionRepository.groupByMonthAndDateRange(profileId, type.name(), from, to);
    }

    public BigDecimal sumExpensesForCategoryAndPeriod(Long profileId, Long categoryId, LocalDate from, LocalDate to) {
        return transactionRepository.sumExpensesForCategoryAndPeriod(profileId, categoryId, from, to);
    }

    // ---- JPA Specification builder (demonstrates functional interfaces + lambdas) ----

    private Specification<Transaction> buildSpec(Long profileId, TransactionType type,
                                                  Long categoryId, LocalDate dateFrom,
                                                  LocalDate dateTo, String currency) {
        // Uses Stream-style chaining of lambda-based specifications
        return Specification
                .<Transaction>where((root, q, cb) ->
                        cb.and(
                                cb.equal(root.get("profile").get("id"), profileId),
                                cb.equal(root.get("deleted"), false)
                        ))
                .and(type != null ? (root, q, cb) -> cb.equal(root.get("type"), type) : null)
                .and(categoryId != null ? (root, q, cb) -> cb.equal(root.get("category").get("id"), categoryId) : null)
                .and(dateFrom != null ? (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("date"), dateFrom) : null)
                .and(dateTo != null ? (root, q, cb) -> cb.lessThanOrEqualTo(root.get("date"), dateTo) : null)
                .and(currency != null ? (root, q, cb) -> cb.equal(cb.upper(root.get("currency")), currency.toUpperCase()) : null);
    }

    public static class TransactionAccessDeniedException extends BaseKeyException {
        public TransactionAccessDeniedException() {
            super("You do not own this transaction", "TRANSACTION_ACCESS_DENIED");
        }
    }
}
