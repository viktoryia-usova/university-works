package server.server.business.repository.shared;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.server.business.entity.enums.SharedTransactionType;
import server.server.business.entity.shared.SharedTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface SharedTransactionRepository extends JpaRepository<SharedTransaction, Long> {

    Page<SharedTransaction> findByMember_Sheet_IdAndDeletedFalse(Long sheetId, Pageable pageable);
    List<SharedTransaction> findByMember_Sheet_IdAndDeletedFalse(Long sheetId);
    boolean existsByMember_Sheet_IdAndDeletedFalse(Long sheetId);

    @Query("""
        SELECT COALESCE(SUM(t.amountByn), 0) FROM SharedTransaction t
        WHERE t.member.sheet.id = :sheetId AND t.type = :type AND t.deleted = false
        """)
    BigDecimal sumBySheetAndType(Long sheetId, SharedTransactionType type);

    @Query("""
        SELECT t.member.profile.id, SUM(t.amountByn) FROM SharedTransaction t
        WHERE t.member.sheet.id = :sheetId AND t.type = :type AND t.deleted = false
        GROUP BY t.member.profile.id
        """)
    List<Object[]> sumByProfileAndType(Long sheetId, SharedTransactionType type);
}
