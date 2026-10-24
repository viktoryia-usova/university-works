package server.server.business.facade.shared;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.finance.ContributeToGoalRequest;
import server.server.business.dto.shared.*;
import server.server.business.entity.profile.Profile;
import server.server.business.entity.shared.SharedGoal;
import server.server.business.entity.shared.SharedSheet;
import server.server.business.entity.shared.SharedSheetMember;
import server.server.business.entity.shared.SharedTransaction;
import server.server.business.mapper.shared.SharedGoalMapper;
import server.server.business.mapper.shared.SharedSheetMapper;
import server.server.business.mapper.shared.SharedTransactionMapper;
import server.server.business.service.profile.ProfileService;
import server.server.business.service.shared.SharedGoalService;
import server.server.business.service.shared.SharedSheetService;
import server.server.common.client.NbrbExchangeRateClient;
import server.server.common.util.SecurityUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class SharedSheetFacade {

    private final SharedSheetService      sheetService;
    private final SharedGoalService       goalService;
    private final ProfileService          profileService;
    private final NbrbExchangeRateClient  nbrbClient;
    private final SharedSheetMapper       sheetMapper;
    private final SharedTransactionMapper txMapper;
    private final SharedGoalMapper        goalMapper;

    public SharedSheetFacade(SharedSheetService sheetService,
                             SharedGoalService goalService,
                             ProfileService profileService,
                             NbrbExchangeRateClient nbrbClient,
                             SharedSheetMapper sheetMapper,
                             SharedTransactionMapper txMapper,
                             SharedGoalMapper goalMapper) {
        this.sheetService  = sheetService;
        this.goalService   = goalService;
        this.profileService = profileService;
        this.nbrbClient    = nbrbClient;
        this.sheetMapper   = sheetMapper;
        this.txMapper      = txMapper;
        this.goalMapper    = goalMapper;
    }

    // ---- Sheet CRUD ----

    public Page<SharedSheetResponse> getMySheets(Pageable pageable) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return sheetService.findForProfile(profile.getId(), pageable).map(sheetMapper::toDto);
    }

    public SharedSheetResponse getById(Long id) {
        SharedSheet sheet = sheetService.findById(id);
        sheetService.assertMember(sheet, profileService.getByAccountId(SecurityUtils.currentAccountId()).getId());
        return sheetMapper.toDto(sheet);
    }

    public SharedSheetResponse create(CreateSharedSheetRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return sheetMapper.toDto(sheetService.create(profile, request.name(), request.description()));
    }

    public SharedSheetResponse update(Long id, UpdateSharedSheetRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        SharedSheet sheet = sheetService.findById(id);
        String name = request.name() != null ? request.name() : sheet.getName();
        String desc = request.description() != null ? request.description() : sheet.getDescription();
        return sheetMapper.toDto(sheetService.update(sheet, name, desc));
    }

    public SharedSheetResponse complete(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return sheetMapper.toDto(sheetService.complete(sheetService.findById(id), profile.getId()));
    }

    public void delete(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        sheetService.softDelete(sheetService.findById(id), profile.getId());
    }

    // ---- Members ----

    public void inviteMember(Long sheetId, InviteMemberRequest request) {
        SharedSheet sheet = sheetService.findById(sheetId);
        Profile invitee = profileService.getById(request.profileId());
        sheetService.inviteMember(sheet, invitee);
    }

    public void leaveSheet(Long sheetId) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        sheetService.leave(sheetService.findById(sheetId), profile.getId());
    }

    // ---- Transactions ----

    public Page<SharedTransactionResponse> getTransactions(Long sheetId, Pageable pageable) {
        sheetService.assertMember(sheetService.findById(sheetId),
                profileService.getByAccountId(SecurityUtils.currentAccountId()).getId());
        return sheetService.findTransactions(sheetId, pageable).map(txMapper::toDto);
    }

    public SharedTransactionResponse addTransaction(Long sheetId, CreateSharedTransactionRequest request) {
        Profile author = profileService.getByAccountId(SecurityUtils.currentAccountId());
        SharedSheet sheet = sheetService.findById(sheetId);
        SharedSheetMember member = sheetService.findMember(sheetId, author.getId());
        BigDecimal rate = nbrbClient.getRateToByn(request.currency());
        BigDecimal amountByn = request.amount().multiply(rate).setScale(4);

        SharedTransaction tx = SharedTransaction.builder()
                .member(member)
                .amount(request.amount())
                .currency(request.currency())
                .exchangeRateToByn(rate)
                .amountByn(amountByn)
                .type(request.type())
                .description(request.description())
                .date(request.date())
                .build();

        return txMapper.toDto(sheetService.addTransaction(sheet, tx));
    }

    public void deleteTransaction(Long sheetId, Long txId) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        SharedSheet sheet = sheetService.findById(sheetId);
        sheetService.assertMember(sheet, profile.getId());
        SharedTransaction tx = sheetService.findTransactionById(txId);
        sheetService.deleteTransaction(tx);
    }

    // ---- Goals ----

    public Page<SharedGoalResponse> getGoals(Long sheetId, Pageable pageable) {
        sheetService.assertMember(sheetService.findById(sheetId),
                profileService.getByAccountId(SecurityUtils.currentAccountId()).getId());
        return goalService.findBySheet(sheetId, pageable).map(goalMapper::toDto);
    }

    public SharedGoalResponse createGoal(Long sheetId, CreateSharedGoalRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        SharedSheet sheet = sheetService.findById(sheetId);
        sheetService.assertMember(sheet, profile.getId());
        SharedGoal goal = SharedGoal.builder()
                .sheet(sheet)
                .name(request.name())
                .targetAmount(request.targetAmount())
                .currency(request.currency())
                .deadline(request.deadline())
                .build();
        return goalMapper.toDto(goalService.save(goal));
    }

    public SharedGoalResponse contributeToGoal(Long sheetId, Long goalId, ContributeToGoalRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        sheetService.assertMember(sheetService.findById(sheetId), profile.getId());
        SharedGoal goal = goalService.findById(goalId);
        return goalMapper.toDto(goalService.contribute(goal, request.amount()));
    }

    public void deleteGoal(Long sheetId, Long goalId) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        sheetService.assertMember(sheetService.findById(sheetId), profile.getId());
        goalService.delete(goalService.findById(goalId));
    }

    // ---- Analytics ----

    public SharedSheetAnalyticsResponse getAnalytics(Long sheetId) {
        Profile currentProfile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        SharedSheet sheet = sheetService.findById(sheetId);
        sheetService.assertMember(sheet, currentProfile.getId());
        SharedSheetService.SheetAnalytics analytics = sheetService.computeAnalytics(sheetId);

        BigDecimal equalShare = analytics.memberCount() > 0
                ? analytics.totalSpentByn()
                        .divide(BigDecimal.valueOf(analytics.memberCount()), 4)
                : BigDecimal.ZERO;

        // Build a map profileId -> Profile for member info
        java.util.Map<Long, server.server.business.entity.profile.Profile> profileMap =
                sheet.getMembers().stream()
                        .collect(Collectors.toMap(
                                m -> m.getProfile().getId(),
                                server.server.business.entity.shared.SharedSheetMember::getProfile));

        List<MemberContributionResponse> contributions = sheet.getMembers().stream()
                .map(m -> {
                    Long pid = m.getProfile().getId();
                    BigDecimal contributed = analytics.contributedByProfile().getOrDefault(pid, BigDecimal.ZERO);
                    BigDecimal spent       = analytics.spentByProfile().getOrDefault(pid, BigDecimal.ZERO);
                    BigDecimal net         = contributed.subtract(equalShare);
                    return new MemberContributionResponse(
                            pid,
                            m.getProfile().getAccount().getUsername(),
                            m.getProfile().getFirstName(),
                            contributed,
                            spent,
                            net);
                })
                .collect(Collectors.toList());

        BigDecimal balance = analytics.totalContributedByn().subtract(analytics.totalSpentByn());

        return new SharedSheetAnalyticsResponse(
                sheet.getId(),
                sheet.getName(),
                analytics.totalContributedByn(),
                analytics.totalSpentByn(),
                balance,
                equalShare,
                analytics.memberCount(),
                contributions);
    }
}
