package server.server.business.entity.resource;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.common.entity.classes.BaseSoftDeletingEntity;

@Entity
@Table(name = "resource_categories")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCategory extends BaseSoftDeletingEntity {

    @Column(nullable = false, length = 100)
    private String name;
}
