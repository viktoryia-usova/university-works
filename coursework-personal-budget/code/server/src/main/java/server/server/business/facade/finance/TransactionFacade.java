package server.server.business.facade.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.finance.CreateTransactionRequest;
import server.server.business.dto.finance.TransactionResponse;
import server.server.business.dto.finance.UpdateTransactionRequest;
import server.server.business.entity.enums.TransactionType;
import server.server.business.entity.finance.Category;
import server.server.business.entity.finance.Transaction;
import server.server.business.entity.profile.Profile;
import server.server.business.mapper.finance.TransactionMapper;
import server.server.business.service.finance.CategoryService;
import server.server.business.service.finance.PlannedExpenseService;
import server.server.business.service.finance.TransactionService;
import server.server.business.service.profile.ProfileService;
import server.server.common.client.NbrbExchangeRateClient;
import server.server.common.util.SecurityUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
@Transactional
public class TransactionFacade {

    private final TransactionService     transactionService;
    private final ProfileService         profileService;
    private final CategoryService        categoryService;
    private final PlannedExpenseService  plannedExpenseService;
    private final NbrbExchangeRateClient nbrbClient;
    private final TransactionMapper      mapper;

    public TransactionFacade(TransactionService transactionService,
                             ProfileService profileService,
                             CategoryService categoryService,
                             PlannedExpenseService plannedExpenseService,
                             NbrbExchangeRateClient nbrbClient,
                             TransactionMapper mapper) {
        this.transactionService    = transactionService;
        this.profileService        = profileService;
        this.categoryService       = categoryService;
        this.plannedExpenseService = plannedExpenseService;
        this.nbrbClient            = nbrbClient;
        this.mapper                = mapper;
    }

    public Page<TransactionResponse> getMyTransactions(TransactionType type,
                                                       Long categoryId,
                                                       LocalDate dateFrom,
                                                       LocalDate dateTo,
                                                       String currency,
                                                       Pageable pageable) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return transactionService.findByProfile(profile.getId(), type, categoryId, dateFrom, dateTo, currency, pageable)
                .map(mapper::toDto);
    }

    public TransactionResponse getById(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Transaction tx = transactionService.findById(id);
        transactionService.assertOwnership(tx, profile.getId());
        return mapper.toDto(tx);
    }

    public TransactionResponse create(CreateTransactionRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Category category = request.categoryId() != null
                ? categoryService.findById(request.categoryId()) : null;

        BigDecimal rate = nbrbClient.getRateToByn(request.currency());
        BigDecimal amountByn = request.amount().multiply(rate).setScale(4, RoundingMode.HALF_UP);

        Transaction tx = Transaction.builder()
                .profile(profile)
                .category(category)
                .amount(request.amount())
                .currency(request.currency())
                .exchangeRateToByn(rate)
                .amountByn(amountByn)
                .type(request.type())
                .date(request.date())
                .description(request.description())
                .build();

        TransactionResponse saved = mapper.toDto(transactionService.save(tx));

        // Check budget limits if this is an expense (Observer pattern via Spring events)
        if (request.type() == TransactionType.EXPENSE) {
            plannedExpenseService.checkBudgetLimits(
                    profile.getId(),
                    request.categoryId(),
                    request.date());
        }

        return saved;
    }

    public TransactionResponse update(Long id, UpdateTransactionRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Transaction tx = transactionService.findById(id);
        transactionService.assertOwnership(tx, profile.getId());

        if (request.categoryId() != null) tx.setCategory(categoryService.findById(request.categoryId()));
        if (request.amount()     != null) {
            String currency = request.currency() != null ? request.currency() : tx.getCurrency();
            BigDecimal rate = nbrbClient.getRateToByn(currency);
            tx.setAmount(request.amount());
            tx.setCurrency(currency);
            tx.setExchangeRateToByn(rate);
            tx.setAmountByn(request.amount().multiply(rate).setScale(4, RoundingMode.HALF_UP));
        } else if (request.currency() != null) {
            BigDecimal rate = nbrbClient.getRateToByn(request.currency());
            tx.setCurrency(request.currency());
            tx.setExchangeRateToByn(rate);
            tx.setAmountByn(tx.getAmount().multiply(rate).setScale(4, RoundingMode.HALF_UP));
        }
        if (request.type()        != null) tx.setType(request.type());
        if (request.date()        != null) tx.setDate(request.date());
        if (request.description() != null) tx.setDescription(request.description());

        return mapper.toDto(transactionService.save(tx));
    }

    public void delete(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Transaction tx = transactionService.findById(id);
        transactionService.assertOwnership(tx, profile.getId());
        transactionService.delete(tx);
    }
}
