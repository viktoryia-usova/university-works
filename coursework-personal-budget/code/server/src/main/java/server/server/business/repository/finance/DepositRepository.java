package server.server.business.repository.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.enums.DepositStatus;
import server.server.business.entity.enums.DepositType;
import server.server.business.entity.finance.Deposit;

import java.time.LocalDate;
import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    Page<Deposit> findByProfile_IdAndDeletedFalse(Long profileId, Pageable pageable);
    Page<Deposit> findByProfile_IdAndStatusAndDeletedFalse(Long profileId, DepositStatus status, Pageable pageable);
    Page<Deposit> findByProfile_IdAndTypeAndDeletedFalse(Long profileId, DepositType type, Pageable pageable);

    /** Used by Quartz scheduler to find matured deposits */
    List<Deposit> findByStatusAndEndDateLessThanEqualAndDeletedFalse(DepositStatus status, LocalDate date);
}
