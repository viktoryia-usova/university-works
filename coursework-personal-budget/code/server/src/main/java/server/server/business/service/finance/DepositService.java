package server.server.business.service.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.enums.DepositStatus;
import server.server.business.entity.enums.DepositType;
import server.server.business.entity.finance.Deposit;
import server.server.business.repository.finance.DepositRepository;
import server.server.common.exception.BaseKeyException;
import server.server.common.exception.common.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepositService {

    private final DepositRepository depositRepository;

    public Page<Deposit> findByProfile(Long profileId, DepositStatus status, DepositType type, Pageable pageable) {
        if (status != null) return depositRepository.findByProfile_IdAndStatusAndDeletedFalse(profileId, status, pageable);
        if (type != null)   return depositRepository.findByProfile_IdAndTypeAndDeletedFalse(profileId, type, pageable);
        return depositRepository.findByProfile_IdAndDeletedFalse(profileId, pageable);
    }

    public Deposit findById(Long id) {
        Deposit deposit = depositRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "DEPOSIT_NOT_FOUND", Deposit.class));
        if (Boolean.TRUE.equals(deposit.getDeleted())) {
            throw new EntityNotFoundException("id=" + id, "DEPOSIT_NOT_FOUND", Deposit.class);
        }
        return deposit;
    }

    public Deposit save(Deposit deposit) {
        return depositRepository.save(deposit);
    }

    public void delete(Deposit deposit) {
        deposit.setDeleted(true);
        depositRepository.save(deposit);
    }

    public Deposit addFunds(Deposit deposit, BigDecimal amount) {
        if (deposit.getType() != DepositType.PIGGY_BANK) {
            throw new DepositTypeException("Only PIGGY_BANK deposits support manual fund addition");
        }
        deposit.setCurrentAmount(deposit.getCurrentAmount().add(amount));
        return depositRepository.save(deposit);
    }

    /** Returns all ACTIVE deposits with end_date <= today. Used by Quartz job. */
    public List<Deposit> findMaturedDeposits() {
        return depositRepository.findByStatusAndEndDateLessThanEqualAndDeletedFalse(DepositStatus.ACTIVE, LocalDate.now());
    }

    public void assertOwnership(Deposit deposit, Long profileId) {
        if (!deposit.getProfile().getId().equals(profileId)) {
            throw new DepositAccessDeniedException();
        }
    }

    public static class DepositAccessDeniedException extends BaseKeyException {
        public DepositAccessDeniedException() {
            super("You do not own this deposit", "DEPOSIT_ACCESS_DENIED");
        }
    }

    public static class DepositTypeException extends BaseKeyException {
        public DepositTypeException(String msg) {
            super(msg, "DEPOSIT_TYPE_ERROR");
        }
    }
}
