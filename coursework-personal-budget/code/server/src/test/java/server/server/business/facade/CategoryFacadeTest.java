package server.server.business.facade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import server.server.business.dto.finance.CategoryResponse;
import server.server.business.dto.finance.CreateCategoryRequest;
import server.server.business.dto.finance.UpdateCategoryRequest;
import server.server.business.entity.enums.CategoryType;
import server.server.business.entity.finance.Category;
import server.server.business.entity.profile.Profile;
import server.server.business.facade.finance.CategoryFacade;
import server.server.business.mapper.finance.CategoryMapper;
import server.server.business.service.finance.CategoryService;
import server.server.business.service.profile.ProfileService;
import server.server.common.util.SecurityUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryFacadeTest {

    @Mock private CategoryService  categoryService;
    @Mock private ProfileService   profileService;
    @Mock private CategoryMapper   mapper;

    @InjectMocks private CategoryFacade facade;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private static final Long ACCOUNT_ID = 10L;
    private static final Long PROFILE_ID = 20L;

    @BeforeEach
    void setUp() {
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::currentAccountId).thenReturn(ACCOUNT_ID);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    private Profile profile() {
        Profile p = Profile.builder().build();
        p.setId(PROFILE_ID);
        return p;
    }

    // ---- getMyCategories ----

    @Test
    void getMyCategories_returnsPageOfDtos() {
        Profile profile = profile();
        Category cat = new Category();
        CategoryResponse resp = new CategoryResponse(1L, "Food", "#f00", CategoryType.EXPENSE, null);
        var page = new PageImpl<>(List.of(cat));
        var pageable = PageRequest.of(0, 10);

        when(profileService.getByAccountId(ACCOUNT_ID)).thenReturn(profile);
        when(categoryService.findByProfile(PROFILE_ID, null, pageable)).thenReturn(page);
        when(mapper.toDto(cat)).thenReturn(resp);

        var result = facade.getMyCategories(null, pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(resp, result.getContent().get(0));
    }

    // ---- create ----

    @Test
    void create_delegatesToServiceAndMapsResult() {
        Profile profile = profile();
        Category cat = new Category();
        CategoryResponse resp = new CategoryResponse(2L, "Salary", null, CategoryType.INCOME, null);
        CreateCategoryRequest req = new CreateCategoryRequest("Salary", null, CategoryType.INCOME);

        when(profileService.getByAccountId(ACCOUNT_ID)).thenReturn(profile);
        when(categoryService.create(profile, "Salary", null, CategoryType.INCOME)).thenReturn(cat);
        when(mapper.toDto(cat)).thenReturn(resp);

        assertSame(resp, facade.create(req));
        verify(categoryService).create(profile, "Salary", null, CategoryType.INCOME);
    }

    // ---- update ----

    @Test
    void update_withNewName_updatesCategory() {
        Profile profile = profile();
        Category cat = Category.builder().name("Old").color("#000").profile(profile).build();
        UpdateCategoryRequest req = new UpdateCategoryRequest("New", null);
        CategoryResponse resp = new CategoryResponse(1L, "New", "#000", CategoryType.EXPENSE, null);

        when(profileService.getByAccountId(ACCOUNT_ID)).thenReturn(profile);
        when(categoryService.findById(1L)).thenReturn(cat);
        doNothing().when(categoryService).assertOwnership(cat, PROFILE_ID);
        when(categoryService.update(cat, "New", "#000")).thenReturn(cat);
        when(mapper.toDto(cat)).thenReturn(resp);

        assertSame(resp, facade.update(1L, req));
        verify(categoryService).update(cat, "New", "#000");
    }

    @Test
    void update_withNullName_keepsExistingName() {
        Profile profile = profile();
        Category cat = Category.builder().name("Keep").color("#fff").profile(profile).build();
        UpdateCategoryRequest req = new UpdateCategoryRequest(null, "#abc");

        when(profileService.getByAccountId(ACCOUNT_ID)).thenReturn(profile);
        when(categoryService.findById(1L)).thenReturn(cat);
        doNothing().when(categoryService).assertOwnership(cat, PROFILE_ID);
        when(categoryService.update(cat, "Keep", "#abc")).thenReturn(cat);
        when(mapper.toDto(cat)).thenReturn(new CategoryResponse(1L, "Keep", "#abc", CategoryType.EXPENSE, null));

        facade.update(1L, req);

        // name falls back to existing "Keep"
        verify(categoryService).update(cat, "Keep", "#abc");
    }

    // ---- delete ----

    @Test
    void delete_assertsOwnershipAndDelegates() {
        Profile profile = profile();
        Category cat = Category.builder().profile(profile).build();

        when(profileService.getByAccountId(ACCOUNT_ID)).thenReturn(profile);
        when(categoryService.findById(3L)).thenReturn(cat);
        doNothing().when(categoryService).assertOwnership(cat, PROFILE_ID);

        facade.delete(3L);

        verify(categoryService).assertOwnership(cat, PROFILE_ID);
        verify(categoryService).delete(cat);
    }
}
