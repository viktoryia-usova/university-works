package server.server.business.facade.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.finance.*;
import server.server.business.entity.enums.DepositStatus;
import server.server.business.entity.enums.DepositType;
import server.server.business.entity.finance.Deposit;
import server.server.business.entity.profile.Profile;
import server.server.business.mapper.finance.DepositMapper;
import server.server.business.service.finance.DepositService;
import server.server.business.service.profile.ProfileService;
import server.server.common.util.SecurityUtils;

@Component
@Transactional
public class DepositFacade {

    private final DepositService  depositService;
    private final ProfileService  profileService;
    private final DepositMapper   mapper;

    public DepositFacade(DepositService depositService,
                         ProfileService profileService,
                         DepositMapper mapper) {
        this.depositService = depositService;
        this.profileService = profileService;
        this.mapper         = mapper;
    }

    public Page<DepositResponse> getMyDeposits(DepositStatus status, DepositType type, Pageable pageable) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return depositService.findByProfile(profile.getId(), status, type, pageable).map(mapper::toDto);
    }

    public DepositResponse getById(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Deposit deposit = depositService.findById(id);
        depositService.assertOwnership(deposit, profile.getId());
        return mapper.toDto(deposit);
    }

    public DepositResponse create(CreateDepositRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Deposit deposit = Deposit.builder()
                .profile(profile)
                .name(request.name())
                .type(request.type())
                .initialAmount(request.initialAmount())
                .currency(request.currency())
                .interestRate(request.interestRate())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .currentAmount(request.initialAmount())
                .build();
        return mapper.toDto(depositService.save(deposit));
    }

    public DepositResponse update(Long id, UpdateDepositRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Deposit deposit = depositService.findById(id);
        depositService.assertOwnership(deposit, profile.getId());
        if (request.name()         != null) deposit.setName(request.name());
        if (request.interestRate() != null) deposit.setInterestRate(request.interestRate());
        if (request.endDate()      != null) deposit.setEndDate(request.endDate());
        return mapper.toDto(depositService.save(deposit));
    }

    public DepositResponse addFunds(Long id, AddFundsRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Deposit deposit = depositService.findById(id);
        depositService.assertOwnership(deposit, profile.getId());
        return mapper.toDto(depositService.addFunds(deposit, request.amount()));
    }

    public void delete(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Deposit deposit = depositService.findById(id);
        depositService.assertOwnership(deposit, profile.getId());
        depositService.delete(deposit);
    }
}
