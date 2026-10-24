package server.server.business.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.server.business.entity.enums.CategoryType;
import server.server.business.entity.finance.Category;
import server.server.business.entity.profile.Profile;
import server.server.business.repository.finance.CategoryRepository;
import server.server.business.service.finance.CategoryService;
import server.server.common.exception.common.EntityNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;

    @InjectMocks private CategoryService categoryService;

    // ---- findById ----

    @Test
    void findById_exists_returnsCategory() {
        Category cat = new Category();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

        assertSame(cat, categoryService.findById(1L));
    }

    @Test
    void findById_missing_throwsEntityNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findById(99L));
    }

    // ---- create ----

    @Test
    void create_noDuplicate_savesAndReturns() {
        Profile profile = Profile.builder().build();
        profile.setId(1L);

        when(categoryRepository.existsByProfile_IdAndNameIgnoreCaseAndType(1L, "Food", CategoryType.EXPENSE))
                .thenReturn(false);
        Category saved = Category.builder().name("Food").type(CategoryType.EXPENSE).build();
        when(categoryRepository.save(any())).thenReturn(saved);

        Category result = categoryService.create(profile, "Food", "#ff0000", CategoryType.EXPENSE);

        assertSame(saved, result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void create_duplicate_throwsDuplicateCategoryException() {
        Profile profile = Profile.builder().build();
        profile.setId(1L);

        when(categoryRepository.existsByProfile_IdAndNameIgnoreCaseAndType(1L, "Food", CategoryType.EXPENSE))
                .thenReturn(true);

        assertThrows(CategoryService.DuplicateCategoryException.class,
                () -> categoryService.create(profile, "Food", null, CategoryType.EXPENSE));

        verify(categoryRepository, never()).save(any());
    }

    // ---- update ----

    @Test
    void update_changesNameColorAndSaves() {
        Category cat = Category.builder().name("Old").color("#000").build();
        when(categoryRepository.save(cat)).thenReturn(cat);

        Category result = categoryService.update(cat, "New", "#fff");

        assertEquals("New", result.getName());
        assertEquals("#fff", result.getColor());
        verify(categoryRepository).save(cat);
    }

    // ---- delete (soft) ----

    @Test
    void delete_setsDeletedTrueAndSaves() {
        Category cat = Category.builder().build();
        cat.setDeleted(false);
        when(categoryRepository.save(cat)).thenReturn(cat);

        categoryService.delete(cat);

        assertTrue(cat.getDeleted());
        verify(categoryRepository).save(cat);
    }

    // ---- assertOwnership ----

    @Test
    void assertOwnership_ownProfile_doesNotThrow() {
        Profile profile = Profile.builder().build();
        profile.setId(5L);
        Category cat = Category.builder().profile(profile).build();

        assertDoesNotThrow(() -> categoryService.assertOwnership(cat, 5L));
    }

    @Test
    void assertOwnership_foreignProfile_throwsAccessDenied() {
        Profile profile = Profile.builder().build();
        profile.setId(5L);
        Category cat = Category.builder().profile(profile).build();

        assertThrows(CategoryService.CategoryAccessDeniedException.class,
                () -> categoryService.assertOwnership(cat, 99L));
    }
}
