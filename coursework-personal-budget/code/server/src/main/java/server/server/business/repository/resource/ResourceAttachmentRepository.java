package server.server.business.repository.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.resource.ResourceAttachment;

import java.util.List;

public interface ResourceAttachmentRepository extends JpaRepository<ResourceAttachment, Long> {
    List<ResourceAttachment> findByResource_Id(Long resourceId);
}
