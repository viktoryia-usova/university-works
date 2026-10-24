package server.server.business.facade.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.finance.*;
import server.server.business.entity.finance.Category;
import server.server.business.entity.finance.PlannedExpense;
import server.server.business.entity.profile.Profile;
import server.server.business.mapper.finance.PlannedExpenseMapper;
import server.server.business.service.finance.CategoryService;
import server.server.business.service.finance.PlannedExpenseService;
import server.server.business.service.profile.ProfileService;
import server.server.common.util.SecurityUtils;

@Component
@Transactional
public class PlannedExpenseFacade {

    private final PlannedExpenseService service;
    private final ProfileService        profileService;
    private final CategoryService       categoryService;
    private final PlannedExpenseMapper  mapper;

    public PlannedExpenseFacade(PlannedExpenseService service,
                                ProfileService profileService,
                                CategoryService categoryService,
                                PlannedExpenseMapper mapper) {
        this.service         = service;
        this.profileService  = profileService;
        this.categoryService = categoryService;
        this.mapper          = mapper;
    }

    public Page<PlannedExpenseResponse> getMyExpenses(Pageable pageable) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return service.findByProfile(profile.getId(), null, pageable).map(mapper::toDto);
    }

    public PlannedExpenseResponse getById(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        PlannedExpense expense = service.findById(id);
        service.assertOwnership(expense, profile.getId());
        return mapper.toDto(expense);
    }

    public PlannedExpenseResponse create(CreatePlannedExpenseRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Category category = request.categoryId() != null
                ? categoryService.findById(request.categoryId()) : null;
        PlannedExpense expense = PlannedExpense.builder()
                .profile(profile)
                .category(category)
                .name(request.name())
                .amount(request.amount())
                .currency(request.currency())
                .type(request.type())
                .periodType(request.periodType())
                .plannedDate(request.plannedDate())
                .build();
        return mapper.toDto(service.save(expense));
    }

    public PlannedExpenseResponse update(Long id, UpdatePlannedExpenseRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        PlannedExpense expense = service.findById(id);
        service.assertOwnership(expense, profile.getId());
        if (request.name()        != null) expense.setName(request.name());
        if (request.amount()      != null) expense.setAmount(request.amount());
        if (request.currency()    != null) expense.setCurrency(request.currency());
        if (request.periodType()  != null) expense.setPeriodType(request.periodType());
        if (request.plannedDate() != null) expense.setPlannedDate(request.plannedDate());
        if (request.isFulfilled() != null) expense.setIsFulfilled(request.isFulfilled());
        if (request.categoryId()  != null) expense.setCategory(categoryService.findById(request.categoryId()));
        return mapper.toDto(service.save(expense));
    }

    public void delete(Long id) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        PlannedExpense expense = service.findById(id);
        service.assertOwnership(expense, profile.getId());
        service.delete(expense);
    }
}
