package server.server.business.facade.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.finance.*;
import server.server.business.entity.enums.GoalStatus;
import server.server.business.entity.finance.Goal;
import server.server.business.entity.profile.Profile;
import server.server.business.mapper.finance.GoalMapper;
import server.server.business.service.finance.GoalService;
import server.server.business.service.profile.ProfileService;
import server.server.common.client.NbrbExchangeRateClient;
import server.server.common.util.SecurityUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Transactional
public class GoalFacade {

    private final GoalService            goalService;
    private final ProfileService         profileService;
    private final GoalMapper             mapper;
    private final NbrbExchangeRateClient nbrbClient;

    public GoalFacade(GoalService goalService,
                      ProfileService profileService,
                      GoalMapper mapper,
                      NbrbExchangeRateClient nbrbClient) {
        this.goalService    = goalService;
        this.profileService = profileService;
        this.mapper         = mapper;
        this.nbrbClient     = nbrbClient;
    }

    public Page<GoalResponse> getMyGoals(GoalStatus status, Pageable pageable) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return goalService.findByProfile(profile.getId(), status, pageable).map(mapper::toDto);
    }

    public GoalResponse getById(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Goal goal = goalService.findById(id);
        goalService.assertOwnership(goal, profile.getId());
        return mapper.toDto(goal);
    }

    public GoalResponse create(CreateGoalRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Goal goal = Goal.builder()
                .profile(profile)
                .name(request.name())
                .targetAmount(request.targetAmount())
                .currency(request.currency())
                .deadline(request.deadline())
                .build();
        return mapper.toDto(goalService.save(goal));
    }

    public GoalResponse update(Long id, UpdateGoalRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Goal goal = goalService.findById(id);
        goalService.assertOwnership(goal, profile.getId());

        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        if (request.deadline() != null) goal.setDeadline(request.deadline());

        // If currency is changing, convert the already-accumulated amount to the new currency
        String oldCurrency = goal.getCurrency();
        String newCurrency = request.currency();
        if (!oldCurrency.equalsIgnoreCase(newCurrency)) {
            BigDecimal convertedCurrent = convert(goal.getCurrentAmount(), oldCurrency, newCurrency);
            goal.setCurrentAmount(convertedCurrent);
        }
        goal.setCurrency(newCurrency);

        goalService.recalculateStatus(goal);

        return mapper.toDto(goalService.save(goal));
    }

    public GoalResponse contribute(Long id, ContributeToGoalRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Goal goal = goalService.findById(id);
        goalService.assertOwnership(goal, profile.getId());

        // Convert the contribution into the goal's own currency before adding
        BigDecimal amountInGoalCurrency = convert(request.amount(), request.currency(), goal.getCurrency());
        return mapper.toDto(goalService.contribute(goal, amountInGoalCurrency));
    }

    public void delete(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Goal goal = goalService.findById(id);
        goalService.assertOwnership(goal, profile.getId());
        goalService.delete(goal);
    }

    // ---- helpers ----

    /**
     * Converts {@code amount} from {@code from} currency to {@code to} currency
     * using NBRB official rates.  If both currencies are the same, returns the
     * amount unchanged (no API call).
     */
    private BigDecimal convert(BigDecimal amount, String from, String to) {
        if (from.equalsIgnoreCase(to)) return amount;
        BigDecimal fromRate = nbrbClient.getRateToByn(from);   // rate of 'from' to BYN
        BigDecimal toRate   = nbrbClient.getRateToByn(to);     // rate of 'to'   to BYN
        // amount_in_to = amount * (fromRate / toRate)
        return amount
                .multiply(fromRate)
                .divide(toRate, 4, RoundingMode.HALF_UP);
    }
}
