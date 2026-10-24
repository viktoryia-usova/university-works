package server.server.business.service.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.enums.SharedSheetStatus;
import server.server.business.entity.enums.SharedTransactionType;
import server.server.business.entity.profile.Profile;
import server.server.business.entity.shared.SharedSheet;
import server.server.business.entity.shared.SharedSheetMember;
import server.server.business.entity.shared.SharedTransaction;
import server.server.business.event.SharedTransactionAddedEvent;
import server.server.business.repository.shared.SharedSheetMemberRepository;
import server.server.business.repository.shared.SharedSheetRepository;
import server.server.business.repository.shared.SharedTransactionRepository;
import server.server.common.exception.BaseKeyException;
import server.server.common.exception.common.EntityNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SharedSheetService {

    private final SharedSheetRepository sharedSheetRepository;
    private final SharedSheetMemberRepository memberRepository;
    private final SharedTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<SharedSheet> findForProfile(Long profileId, Pageable pageable) {
        return sharedSheetRepository.findAllForProfile(profileId, pageable);
    }

    public SharedSheet findById(Long id) {
        return sharedSheetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "SHEET_NOT_FOUND", SharedSheet.class));
    }

    public SharedSheet create(Profile creator, String name, String description) {
        SharedSheet sheet = sharedSheetRepository.save(SharedSheet.builder()
                .creator(creator)
                .name(name)
                .description(description)
                .status(SharedSheetStatus.ACTIVE)
                .build());

        // Creator is automatically a member
        memberRepository.save(SharedSheetMember.builder()
                .sheet(sheet)
                .profile(creator)
                .joinedAt(LocalDateTime.now())
                .build());

        return sheet;
    }

    public SharedSheet update(SharedSheet sheet, String name, String description) {
        sheet.setName(name);
        sheet.setDescription(description);
        return sharedSheetRepository.save(sheet);
    }

    public SharedSheet complete(SharedSheet sheet, Long creatorId) {
        assertCreator(sheet, creatorId);
        sheet.setStatus(SharedSheetStatus.COMPLETED);
        sheet.setCompletedAt(LocalDateTime.now());
        return sharedSheetRepository.save(sheet);
    }

    public void softDelete(SharedSheet sheet, Long creatorId) {
        assertCreator(sheet, creatorId);
        sheet.setDeleted(true);
        sharedSheetRepository.save(sheet);
    }

    public void inviteMember(SharedSheet sheet, Profile invitee) {
        assertActive(sheet);
        if (memberRepository.existsBySheet_IdAndProfile_Id(sheet.getId(), invitee.getId())) {
            throw new AlreadyMemberException();
        }
        memberRepository.save(SharedSheetMember.builder()
                .sheet(sheet)
                .profile(invitee)
                .joinedAt(LocalDateTime.now())
                .build());
    }

    public SharedSheetMember findMember(Long sheetId, Long profileId) {
        return memberRepository.findBySheet_IdAndProfile_Id(sheetId, profileId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "sheetId=" + sheetId + ",profileId=" + profileId, "MEMBER_NOT_FOUND", SharedSheetMember.class));
    }

    public void leave(SharedSheet sheet, Long profileId) {
        assertActive(sheet);
        if (transactionRepository.existsByMember_Sheet_IdAndDeletedFalse(sheet.getId())) {
            throw new CannotLeaveException("Cannot leave a sheet that already has transactions");
        }
        SharedSheetMember member = findMember(sheet.getId(), profileId);
        memberRepository.delete(member);
    }

    public SharedTransaction addTransaction(SharedSheet sheet, SharedTransaction tx) {
        assertActive(sheet);
        SharedTransaction saved = transactionRepository.save(tx);
        eventPublisher.publishEvent(new SharedTransactionAddedEvent(this, saved, sheet));
        return saved;
    }

    public Page<SharedTransaction> findTransactions(Long sheetId, Pageable pageable) {
        return transactionRepository.findByMember_Sheet_IdAndDeletedFalse(sheetId, pageable);
    }

    public SharedTransaction findTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "SHARED_TX_NOT_FOUND", SharedTransaction.class));
    }

    public SharedTransaction updateTransaction(SharedTransaction tx) {
        return transactionRepository.save(tx);
    }

    public void deleteTransaction(SharedTransaction tx) {
        tx.setDeleted(true);
        transactionRepository.save(tx);
    }

    // ---- Analytics ----

    public record SheetAnalytics(
            BigDecimal totalContributedByn,
            BigDecimal totalSpentByn,
            Map<Long, BigDecimal> contributedByProfile,
            Map<Long, BigDecimal> spentByProfile,
            int memberCount
    ) {}

    public SheetAnalytics computeAnalytics(Long sheetId) {
        BigDecimal contributed = transactionRepository.sumBySheetAndType(sheetId, SharedTransactionType.CONTRIBUTION);
        BigDecimal spent = transactionRepository.sumBySheetAndType(sheetId, SharedTransactionType.EXPENSE);

        Map<Long, BigDecimal> contributedByProfile = toMap(
                transactionRepository.sumByProfileAndType(sheetId, SharedTransactionType.CONTRIBUTION));
        Map<Long, BigDecimal> spentByProfile = toMap(
                transactionRepository.sumByProfileAndType(sheetId, SharedTransactionType.EXPENSE));

        int memberCount = (int) memberRepository.countBySheet_Id(sheetId);
        return new SheetAnalytics(contributed, spent, contributedByProfile, spentByProfile, memberCount);
    }

    private Map<Long, BigDecimal> toMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                r -> (Long) r[0],
                r -> (BigDecimal) r[1]
        ));
    }

    // ---- Guards ----

    public void assertMember(SharedSheet sheet, Long profileId) {
        if (!memberRepository.existsBySheet_IdAndProfile_Id(sheet.getId(), profileId)) {
            throw new NotMemberException();
        }
    }

    private void assertCreator(SharedSheet sheet, Long profileId) {
        if (!sheet.getCreator().getId().equals(profileId)) {
            throw new SheetAccessDeniedException();
        }
    }

    private void assertActive(SharedSheet sheet) {
        if (sheet.getStatus() != SharedSheetStatus.ACTIVE) {
            throw new SheetNotActiveException();
        }
    }

    public static class SheetAccessDeniedException extends BaseKeyException {
        public SheetAccessDeniedException() { super("Only the creator can perform this action", "SHEET_ACCESS_DENIED"); }
    }
    public static class NotMemberException extends BaseKeyException {
        public NotMemberException() { super("You are not a member of this sheet", "NOT_MEMBER"); }
    }
    public static class AlreadyMemberException extends BaseKeyException {
        public AlreadyMemberException() { super("Profile is already a member", "ALREADY_MEMBER"); }
    }
    public static class SheetNotActiveException extends BaseKeyException {
        public SheetNotActiveException() { super("Sheet is not active", "SHEET_INACTIVE"); }
    }
    public static class CannotLeaveException extends BaseKeyException {
        public CannotLeaveException(String msg) { super(msg, "CANNOT_LEAVE"); }
    }
}
