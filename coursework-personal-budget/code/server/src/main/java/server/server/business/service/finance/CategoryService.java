package server.server.business.service.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.enums.CategoryType;
import server.server.business.entity.finance.Category;
import server.server.business.entity.profile.Profile;
import server.server.business.repository.finance.CategoryRepository;
import server.server.common.exception.BaseKeyException;
import server.server.common.exception.common.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Page<Category> findByProfile(Long profileId, CategoryType type, Pageable pageable) {
        return type != null
                ? categoryRepository.findByProfile_IdAndTypeAndDeletedFalse(profileId, type, pageable)
                : categoryRepository.findByProfile_IdAndDeletedFalse(profileId, pageable);
    }

    public List<Category> findAllByProfile(Long profileId) {
        return categoryRepository.findByProfile_IdAndDeletedFalse(profileId);
    }

    public Category findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "CATEGORY_NOT_FOUND", Category.class));
        if (Boolean.TRUE.equals(category.getDeleted())) {
            throw new EntityNotFoundException("id=" + id, "CATEGORY_NOT_FOUND", Category.class);
        }
        return category;
    }

    public Category create(Profile profile, String name, String color, CategoryType type) {
        if (categoryRepository.existsByProfile_IdAndNameIgnoreCaseAndTypeAndDeletedFalse(profile.getId(), name, type)) {
            throw new DuplicateCategoryException(name, type);
        }
        return categoryRepository.save(Category.builder()
                .profile(profile)
                .name(name)
                .color(color)
                .type(type)
                .build());
    }

    public Category update(Category category, String name, String color) {
        category.setName(name);
        category.setColor(color);
        return categoryRepository.save(category);
    }

    public void delete(Category category) {
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    public void assertOwnership(Category category, Long profileId) {
        if (!category.getProfile().getId().equals(profileId)) {
            throw new CategoryAccessDeniedException();
        }
    }

    // Inner exception classes
    public static class DuplicateCategoryException extends BaseKeyException {
        public DuplicateCategoryException(String name, CategoryType type) {
            super("Category '" + name + "' of type " + type + " already exists", "DUPLICATE_CATEGORY");
        }
    }

    public static class CategoryAccessDeniedException extends BaseKeyException {
        public CategoryAccessDeniedException() {
            super("You do not own this category", "CATEGORY_ACCESS_DENIED");
        }
    }
}
