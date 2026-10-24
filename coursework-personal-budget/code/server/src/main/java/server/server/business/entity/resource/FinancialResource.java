package server.server.business.entity.resource;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.common.entity.auth.Account;
import server.server.common.entity.classes.BaseSoftDeletingEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "financial_resources")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialResource extends BaseSoftDeletingEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_category_id", nullable = false)
    private ResourceCategory resourceCategory;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResourceAttachment> attachments = new ArrayList<>();
}
