package server.server.business.facade.analytics;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.analytics.AnalyticsEntryResponse;
import server.server.business.dto.analytics.AnalyticsSummaryResponse;
import server.server.business.dto.analytics.MonthlyEntryResponse;
import server.server.business.entity.enums.TransactionType;
import server.server.business.entity.profile.Profile;
import server.server.business.service.finance.TransactionService;
import server.server.business.service.profile.ProfileService;
import server.server.common.util.SecurityUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class AnalyticsFacade {

    private final TransactionService transactionService;
    private final ProfileService     profileService;

    public AnalyticsFacade(TransactionService transactionService, ProfileService profileService) {
        this.transactionService = transactionService;
        this.profileService     = profileService;
    }

    /** Overall income / expense / balance summary — no category breakdown (KPI cards only). */
    public AnalyticsSummaryResponse getSummary(LocalDate from, LocalDate to) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Long profileId = profile.getId();

        BigDecimal totalIncome  = coalesce(transactionService.sumIncomeByn(profileId, from, to));
        BigDecimal totalExpense = coalesce(transactionService.sumExpenseByn(profileId, from, to));
        BigDecimal balance      = totalIncome.subtract(totalExpense);

        return new AnalyticsSummaryResponse(totalIncome, totalExpense, balance, List.of());
    }

    /** Income breakdown by category. */
    public AnalyticsSummaryResponse getIncome(LocalDate from, LocalDate to) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        BigDecimal total = coalesce(transactionService.sumIncomeByn(profile.getId(), from, to));

        List<AnalyticsEntryResponse> entries = transactionService
                .groupByCategoryByn(profile.getId(), TransactionType.INCOME, from, to).stream()
                .map(row -> toEntry(row, total))
                .collect(Collectors.toList());

        return new AnalyticsSummaryResponse(total, BigDecimal.ZERO, total, entries);
    }

    /** Expense breakdown by category. */
    public AnalyticsSummaryResponse getExpenses(LocalDate from, LocalDate to) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        BigDecimal total = coalesce(transactionService.sumExpenseByn(profile.getId(), from, to));

        List<AnalyticsEntryResponse> entries = transactionService
                .groupByCategoryByn(profile.getId(), TransactionType.EXPENSE, from, to).stream()
                .map(row -> toEntry(row, total))
                .collect(Collectors.toList());

        return new AnalyticsSummaryResponse(BigDecimal.ZERO, total, total.negate(), entries);
    }

    /**
     * Monthly income / expense / balance dynamics.
     * Returns one entry per calendar month in [from, to], even if the month has no transactions.
     */
    public List<MonthlyEntryResponse> getMonthly(LocalDate from, LocalDate to) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Long profileId = profile.getId();

        Map<String, BigDecimal> incomeMap  = buildMonthMap(
                transactionService.groupByMonthByn(profileId, TransactionType.INCOME,  from, to));
        Map<String, BigDecimal> expenseMap = buildMonthMap(
                transactionService.groupByMonthByn(profileId, TransactionType.EXPENSE, from, to));

        List<MonthlyEntryResponse> result = new ArrayList<>();
        LocalDate cursor  = from.withDayOfMonth(1);
        LocalDate lastMonth = to.withDayOfMonth(1);

        while (!cursor.isAfter(lastMonth)) {
            int y = cursor.getYear();
            int m = cursor.getMonthValue();
            String key = y + "-" + m;

            BigDecimal income  = incomeMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal expense = expenseMap.getOrDefault(key, BigDecimal.ZERO);

            result.add(new MonthlyEntryResponse(
                    y, m,
                    MONTH_NAMES[m - 1] + " " + y,
                    income,
                    expense,
                    income.subtract(expense)));

            cursor = cursor.plusMonths(1);
        }
        return result;
    }

    // ---- helpers ----

    private static final String[] MONTH_NAMES = {
        "Янв","Фев","Мар","Апр","Май","Июн",
        "Июл","Авг","Сен","Окт","Ноя","Дек"
    };

    /** Query returns rows: [c.id, c.name, c.color, SUM(amountByn)] — indices 1 (name) and 3 (sum). */
    private AnalyticsEntryResponse toEntry(Object[] row, BigDecimal total) {
        String label   = row[1] != null ? row[1].toString() : "Без категории";
        BigDecimal amt = toBigDecimal(row[3]);
        double percent = total.compareTo(BigDecimal.ZERO) == 0 ? 0.0
                : amt.multiply(BigDecimal.valueOf(100))
                     .divide(total, 2, RoundingMode.HALF_UP)
                     .doubleValue();
        return new AnalyticsEntryResponse(label, amt, percent);
    }

    /** Native query returns: {YEAR, MONTH, SUM} */
    private Map<String, BigDecimal> buildMonthMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            int y = ((Number) r[0]).intValue();
            int m = ((Number) r[1]).intValue();
            map.put(y + "-" + m, toBigDecimal(r[2]));
        }
        return map;
    }

    /** Safely converts whatever numeric type Hibernate/JDBC returns to BigDecimal. */
    private BigDecimal toBigDecimal(Object value) {
        if (value == null)              return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n)  return new BigDecimal(n.toString());
        return new BigDecimal(value.toString());
    }

    private BigDecimal coalesce(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
