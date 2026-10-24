package server.server.business.service.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.profile.Profile;
import server.server.business.entity.resource.*;
import server.server.business.repository.resource.*;
import server.server.common.exception.BaseKeyException;
import server.server.common.exception.common.EntityNotFoundException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {

    private final ResourceCategoryRepository   categoryRepository;
    private final FinancialResourceRepository  resourceRepository;
    private final ResourceAttachmentRepository attachmentRepository;
    private final ResourceFavoriteRepository   favoriteRepository;

    // ---- Categories ----

    public List<ResourceCategory> findAllCategories() {
        return categoryRepository.findAllByDeletedFalse();
    }

    public ResourceCategory findCategoryById(Long id) {
        ResourceCategory cat = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "RESOURCE_CATEGORY_NOT_FOUND", ResourceCategory.class));
        if (Boolean.TRUE.equals(cat.getDeleted())) {
            throw new EntityNotFoundException("id=" + id, "RESOURCE_CATEGORY_NOT_FOUND", ResourceCategory.class);
        }
        return cat;
    }

    public ResourceCategory saveCategory(ResourceCategory cat) {
        if (categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(cat.getName())) {
            throw new DuplicateCategoryNameException(cat.getName());
        }
        return categoryRepository.save(cat);
    }

    public ResourceCategory updateCategory(ResourceCategory cat, String name) {
        cat.setName(name);
        return categoryRepository.save(cat);
    }

    public void deleteCategory(ResourceCategory cat) {
        cat.setDeleted(true);
        categoryRepository.save(cat);
    }

    // ---- Resources ----

    public Page<FinancialResource> findAll(Long categoryId, String title, Pageable pageable) {
        boolean hasCategory = categoryId != null;
        boolean hasTitle    = title != null && !title.isBlank();

        if (hasCategory && hasTitle) {
            return resourceRepository.findByResourceCategory_IdAndTitleContainingIgnoreCaseAndDeletedFalse(categoryId, title, pageable);
        } else if (hasCategory) {
            return resourceRepository.findByResourceCategory_IdAndDeletedFalse(categoryId, pageable);
        } else if (hasTitle) {
            return resourceRepository.findByTitleContainingIgnoreCaseAndDeletedFalse(title, pageable);
        }
        return resourceRepository.findAllByDeletedFalse(pageable);
    }

    public FinancialResource findById(Long id) {
        FinancialResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "RESOURCE_NOT_FOUND", FinancialResource.class));
        if (Boolean.TRUE.equals(resource.getDeleted())) {
            throw new EntityNotFoundException("id=" + id, "RESOURCE_NOT_FOUND", FinancialResource.class);
        }
        return resource;
    }

    public FinancialResource save(FinancialResource resource) {
        return resourceRepository.save(resource);
    }

    public void delete(FinancialResource resource) {
        resource.setDeleted(true);
        resourceRepository.save(resource);
    }

    // ---- Attachments ----

    public ResourceAttachment saveAttachment(ResourceAttachment attachment) {
        return attachmentRepository.save(attachment);
    }

    public ResourceAttachment findAttachmentById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "ATTACHMENT_NOT_FOUND", ResourceAttachment.class));
    }

    public void deleteAttachment(ResourceAttachment attachment) {
        attachmentRepository.delete(attachment);
    }

    // ---- Favorites ----

    public void addFavorite(Profile profile, FinancialResource resource) {
        ResourceFavoriteId id = new ResourceFavoriteId(profile.getId(), resource.getId());
        if (!favoriteRepository.existsById(id)) {
            favoriteRepository.save(ResourceFavorite.builder()
                    .id(id)
                    .profile(profile)
                    .resource(resource)
                    .build());
        }
    }

    public void removeFavorite(Long profileId, Long resourceId) {
        favoriteRepository.deleteByProfileIdAndResourceId(profileId, resourceId);
    }

    public boolean isFavorite(Long profileId, Long resourceId) {
        return favoriteRepository.existsByIdProfileIdAndIdResourceId(profileId, resourceId);
    }

    public Set<Long> getFavoriteResourceIds(Long profileId) {
        return favoriteRepository.findResourceIdsByProfileId(profileId);
    }

    public Page<FinancialResource> findFavorites(Long profileId, Pageable pageable) {
        return favoriteRepository.findFavoriteResourcesByProfileId(profileId, pageable);
    }

    // ---- Exceptions ----

    public static class DuplicateCategoryNameException extends BaseKeyException {
        public DuplicateCategoryNameException(String name) {
            super("Resource category '" + name + "' already exists", "DUPLICATE_RESOURCE_CATEGORY");
        }
    }
}
