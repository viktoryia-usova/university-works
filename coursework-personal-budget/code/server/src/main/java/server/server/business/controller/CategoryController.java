package server.server.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.server.business.dto.finance.CategoryResponse;
import server.server.business.dto.finance.CreateCategoryRequest;
import server.server.business.dto.finance.UpdateCategoryRequest;
import server.server.business.entity.enums.CategoryType;
import server.server.business.facade.finance.CategoryFacade;

@Tag(name = "Categories", description = "Категории доходов и расходов")
@RestController
@RequestMapping("/api/v1/categories")
@PreAuthorize("isAuthenticated()")
public class CategoryController {

    private final CategoryFacade categoryFacade;

    public CategoryController(CategoryFacade categoryFacade) {
        this.categoryFacade = categoryFacade;
    }

    @Operation(summary = "Список своих категорий (с фильтром по типу)")
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAll(
            @RequestParam(required = false) CategoryType type,
            Pageable pageable) {
        return ResponseEntity.ok(categoryFacade.getMyCategories(type, pageable));
    }

    @Operation(summary = "Создать категорию")
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryFacade.create(request));
    }

    @Operation(summary = "Обновить категорию")
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryFacade.update(id, request));
    }

    @Operation(summary = "Удалить категорию")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
