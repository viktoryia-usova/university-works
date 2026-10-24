package server.server.business.facade.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.finance.CategoryResponse;
import server.server.business.dto.finance.CreateCategoryRequest;
import server.server.business.dto.finance.UpdateCategoryRequest;
import server.server.business.entity.enums.CategoryType;
import server.server.business.entity.finance.Category;
import server.server.business.entity.profile.Profile;
import server.server.business.mapper.finance.CategoryMapper;
import server.server.business.service.finance.CategoryService;
import server.server.business.service.profile.ProfileService;
import server.server.common.util.SecurityUtils;

@Component
@Transactional
public class CategoryFacade {

    private final CategoryService categoryService;
    private final ProfileService  profileService;
    private final CategoryMapper  mapper;

    public CategoryFacade(CategoryService categoryService,
                          ProfileService profileService,
                          CategoryMapper mapper) {
        this.categoryService = categoryService;
        this.profileService  = profileService;
        this.mapper          = mapper;
    }

    public Page<CategoryResponse> getMyCategories(CategoryType type, Pageable pageable) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return categoryService.findByProfile(profile.getId(), type, pageable)
                .map(mapper::toDto);
    }

    public CategoryResponse create(CreateCategoryRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        Category category = categoryService.create(profile, request.name(), request.color(), request.type());
        return mapper.toDto(category);
    }

    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Category category = categoryService.findById(id);
        categoryService.assertOwnership(category, profileService.getByAccountId(SecurityUtils.currentAccountId()).getId());
        String newName  = request.name()  != null ? request.name()  : category.getName();
        String newColor = request.color() != null ? request.color() : category.getColor();
        return mapper.toDto(categoryService.update(category, newName, newColor));
    }

    public void delete(Long id) {
        Category category = categoryService.findById(id);
        categoryService.assertOwnership(category, profileService.getByAccountId(SecurityUtils.currentAccountId()).getId());
        categoryService.delete(category);
    }
}
