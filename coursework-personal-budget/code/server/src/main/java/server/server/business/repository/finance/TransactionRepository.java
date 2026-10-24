package server.server.business.repository.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.server.business.entity.enums.TransactionType;
import server.server.business.entity.finance.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByProfile_Id(Long profileId, Pageable pageable);

    /** Aggregated income/expense sum in BYN for analytics KPI cards */
    @Query("""
        SELECT COALESCE(SUM(t.amountByn), 0)
        FROM Transaction t
        WHERE t.profile.id = :profileId
          AND t.type = :type
          AND t.date BETWEEN :from AND :to
        """)
    BigDecimal sumByProfileAndTypeAndDateRange(
            @Param("profileId") Long profileId,
            @Param("type")      TransactionType type,
            @Param("from")      LocalDate from,
            @Param("to")        LocalDate to);

    /** Group by category for pie-chart analytics.
     *  Returns rows: [category.id, category.name, category.color, SUM(amountByn)] */
    @Query("""
        SELECT c.id, c.name, c.color, SUM(t.amountByn)
        FROM Transaction t
        INNER JOIN t.category c
        WHERE t.profile.id = :profileId
          AND t.type = :type
          AND t.date BETWEEN :from AND :to
        GROUP BY c.id, c.name, c.color
        ORDER BY SUM(t.amountByn) DESC
        """)
    List<Object[]> groupByCategoryAndDateRange(
            @Param("profileId") Long profileId,
            @Param("type")      TransactionType type,
            @Param("from")      LocalDate from,
            @Param("to")        LocalDate to);

    /** Group by month for monthly dynamics chart.
     *  Returns rows: [year, month, SUM(amount_byn)] */
    @Query(value = """
        SELECT EXTRACT(YEAR  FROM t.date) AS yr,
               EXTRACT(MONTH FROM t.date) AS mo,
               COALESCE(SUM(t.amount_byn), 0)
        FROM transactions t
        WHERE t.profile_id = :profileId
          AND t.type = :type
          AND t.date BETWEEN :from AND :to
          AND t.deleted = false
        GROUP BY yr, mo
        ORDER BY yr, mo
        """, nativeQuery = true)
    List<Object[]> groupByMonthAndDateRange(
            @Param("profileId") Long profileId,
            @Param("type")      String type,
            @Param("from")      LocalDate from,
            @Param("to")        LocalDate to);

    List<Transaction> findByProfile_IdAndDateBetween(Long profileId, LocalDate from, LocalDate to);

    /** For planned expense budget limit check */
    @Query("""
        SELECT COALESCE(SUM(t.amountByn), 0)
        FROM Transaction t
        WHERE t.profile.id = :profileId
          AND t.type = 'EXPENSE'
          AND t.category.id = :categoryId
          AND t.date BETWEEN :from AND :to
        """)
    BigDecimal sumExpensesForCategoryAndPeriod(
            @Param("profileId")  Long profileId,
            @Param("categoryId") Long categoryId,
            @Param("from")       LocalDate from,
            @Param("to")         LocalDate to);
}
