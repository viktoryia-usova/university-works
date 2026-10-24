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
import org.springframework.web.multipart.MultipartFile;
import server.server.business.dto.resource.*;
import server.server.business.entity.enums.AttachmentType;
import server.server.business.facade.resource.ResourceFacade;
import server.server.common.service.util.FileService;

import java.util.List;
import java.util.Objects;

@Tag(name = "Financial Resources", description = "Обучающие ресурсы по финансовой грамотности")
@RestController
@RequestMapping("/api/v1/resources")
@PreAuthorize("isAuthenticated()")
public class ResourceController {

    private final ResourceFacade resourceFacade;
    private final FileService    fileService;

    public ResourceController(ResourceFacade resourceFacade, FileService fileService) {
        this.resourceFacade = resourceFacade;
        this.fileService    = fileService;
    }

    // ---- Categories ----

    @Operation(summary = "Список категорий ресурсов")
    @GetMapping("/categories")
    public ResponseEntity<List<ResourceCategoryResponse>> getCategories() {
        return ResponseEntity.ok(resourceFacade.getAllCategories());
    }

    @Operation(summary = "Создать категорию (ADMIN)")
    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResourceCategoryResponse> createCategory(
            @Valid @RequestBody CreateResourceCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceFacade.createCategory(request));
    }

    @Operation(summary = "Обновить категорию (ADMIN)")
    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResourceCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateResourceCategoryRequest request) {
        return ResponseEntity.ok(resourceFacade.updateCategory(id, request));
    }

    @Operation(summary = "Удалить категорию (ADMIN)")
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        resourceFacade.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Resources ----

    @Operation(summary = "Список ресурсов (фильтр по категории / заголовку)")
    @GetMapping
    public ResponseEntity<Page<FinancialResourceResponse>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String title,
            Pageable pageable) {
        return ResponseEntity.ok(resourceFacade.getResources(categoryId, title, pageable));
    }

    @Operation(summary = "Получить ресурс по ID")
    @GetMapping("/{id}")
    public ResponseEntity<FinancialResourceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceFacade.getResourceById(id));
    }

    @Operation(summary = "Создать ресурс (ADMIN)")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FinancialResourceResponse> create(
            @Valid @RequestBody CreateFinancialResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceFacade.createResource(request));
    }

    @Operation(summary = "Обновить ресурс (ADMIN)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FinancialResourceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFinancialResourceRequest request) {
        return ResponseEntity.ok(resourceFacade.updateResource(id, request));
    }

    @Operation(summary = "Удалить ресурс (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceFacade.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Загрузить вложение к ресурсу (ADMIN)")
    @PostMapping("/{id}/attachments")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AttachmentResponse> addAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam AttachmentType fileType) {
        String originalName = Objects.requireNonNull(file.getOriginalFilename());
        String savedPath    = fileService.save(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceFacade.addAttachment(id, originalName, savedPath, fileType));
    }

    @Operation(summary = "Удалить вложение (ADMIN)")
    @DeleteMapping("/attachments/{attachmentId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
        resourceFacade.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    // ---- Favorites ----

    @Operation(summary = "Избранное пользователя")
    @GetMapping("/favorites")
    public ResponseEntity<Page<FinancialResourceResponse>> getFavorites(Pageable pageable) {
        return ResponseEntity.ok(resourceFacade.getFavorites(pageable));
    }

    @Operation(summary = "Добавить в избранное")
    @PostMapping("/{id}/favorite")
    public ResponseEntity<FinancialResourceResponse> addFavorite(@PathVariable Long id) {
        return ResponseEntity.ok(resourceFacade.addFavorite(id));
    }

    @Operation(summary = "Удалить из избранного")
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<FinancialResourceResponse> removeFavorite(@PathVariable Long id) {
        return ResponseEntity.ok(resourceFacade.removeFavorite(id));
    }
}
