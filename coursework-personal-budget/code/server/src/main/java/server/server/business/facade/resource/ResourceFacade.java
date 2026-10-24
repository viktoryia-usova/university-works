package server.server.business.facade.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.resource.*;
import server.server.business.entity.profile.Profile;
import server.server.business.entity.resource.FinancialResource;
import server.server.business.entity.resource.ResourceAttachment;
import server.server.business.entity.resource.ResourceCategory;
import server.server.business.mapper.resource.AttachmentMapper;
import server.server.business.mapper.resource.FinancialResourceMapper;
import server.server.business.mapper.resource.ResourceCategoryMapper;
import server.server.business.service.profile.ProfileService;
import server.server.business.service.resource.ResourceService;
import server.server.common.util.SecurityUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
public class ResourceFacade {

    private final ResourceService         resourceService;
    private final ProfileService          profileService;
    private final ResourceCategoryMapper  categoryMapper;
    private final FinancialResourceMapper resourceMapper;
    private final AttachmentMapper        attachmentMapper;

    public ResourceFacade(ResourceService resourceService,
                          ProfileService profileService,
                          ResourceCategoryMapper categoryMapper,
                          FinancialResourceMapper resourceMapper,
                          AttachmentMapper attachmentMapper) {
        this.resourceService  = resourceService;
        this.profileService   = profileService;
        this.categoryMapper   = categoryMapper;
        this.resourceMapper   = resourceMapper;
        this.attachmentMapper = attachmentMapper;
    }

    // ---- Categories ----

    public List<ResourceCategoryResponse> getAllCategories() {
        return resourceService.findAllCategories().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public ResourceCategoryResponse createCategory(CreateResourceCategoryRequest request) {
        ResourceCategory cat = ResourceCategory.builder().name(request.name()).build();
        return categoryMapper.toDto(resourceService.saveCategory(cat));
    }

    public ResourceCategoryResponse updateCategory(Long id, CreateResourceCategoryRequest request) {
        ResourceCategory cat = resourceService.findCategoryById(id);
        return categoryMapper.toDto(resourceService.updateCategory(cat, request.name()));
    }

    public void deleteCategory(Long id) {
        resourceService.deleteCategory(resourceService.findCategoryById(id));
    }

    // ---- Resources ----

    public Page<FinancialResourceResponse> getResources(Long categoryId, String title, Pageable pageable) {
        Long profileId = currentProfileId();
        Set<Long> favoriteIds = resourceService.getFavoriteResourceIds(profileId);
        return resourceService.findAll(categoryId, title, pageable)
                .map(r -> resourceMapper.toDto(r, favoriteIds.contains(r.getId())));
    }

    public FinancialResourceResponse getResourceById(Long id) {
        Long profileId = currentProfileId();
        FinancialResource resource = resourceService.findById(id);
        boolean fav = resourceService.isFavorite(profileId, id);
        return resourceMapper.toDto(resource, fav);
    }

    public FinancialResourceResponse createResource(CreateFinancialResourceRequest request) {
        server.server.common.entity.auth.Account author = SecurityUtils.currentAccount();
        ResourceCategory category = resourceService.findCategoryById(request.categoryId());
        FinancialResource resource = FinancialResource.builder()
                .author(author)
                .resourceCategory(category)
                .title(request.title())
                .content(request.content())
                .build();
        return resourceMapper.toDto(resourceService.save(resource));
    }

    public FinancialResourceResponse updateResource(Long id, UpdateFinancialResourceRequest request) {
        FinancialResource resource = resourceService.findById(id);
        if (request.categoryId() != null) resource.setResourceCategory(resourceService.findCategoryById(request.categoryId()));
        if (request.title()      != null) resource.setTitle(request.title());
        if (request.content()    != null) resource.setContent(request.content());
        Long profileId = currentProfileId();
        return resourceMapper.toDto(resourceService.save(resource), resourceService.isFavorite(profileId, id));
    }

    public void deleteResource(Long id) {
        resourceService.delete(resourceService.findById(id));
    }

    // ---- Attachments ----

    public AttachmentResponse addAttachment(Long resourceId, String fileName, String filePath,
                                            server.server.business.entity.enums.AttachmentType fileType) {
        FinancialResource resource = resourceService.findById(resourceId);
        ResourceAttachment attachment = ResourceAttachment.builder()
                .resource(resource)
                .fileName(fileName)
                .filePath(filePath)
                .fileType(fileType)
                .build();
        return attachmentMapper.toDto(resourceService.saveAttachment(attachment));
    }

    public void deleteAttachment(Long attachmentId) {
        resourceService.deleteAttachment(resourceService.findAttachmentById(attachmentId));
    }

    // ---- Favorites ----

    public FinancialResourceResponse addFavorite(Long resourceId) {
        Profile profile = currentProfile();
        FinancialResource resource = resourceService.findById(resourceId);
        resourceService.addFavorite(profile, resource);
        return resourceMapper.toDto(resource, true);
    }

    public FinancialResourceResponse removeFavorite(Long resourceId) {
        Long profileId = currentProfileId();
        resourceService.removeFavorite(profileId, resourceId);
        FinancialResource resource = resourceService.findById(resourceId);
        return resourceMapper.toDto(resource, false);
    }

    public Page<FinancialResourceResponse> getFavorites(Pageable pageable) {
        Long profileId = currentProfileId();
        return resourceService.findFavorites(profileId, pageable)
                .map(r -> resourceMapper.toDto(r, true));
    }

    // ---- Helpers ----

    private Profile currentProfile() {
        return profileService.getByAccountId(SecurityUtils.currentAccountId());
    }

    private Long currentProfileId() {
        return currentProfile().getId();
    }
}
