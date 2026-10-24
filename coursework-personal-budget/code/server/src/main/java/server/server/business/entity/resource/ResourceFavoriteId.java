package server.server.business.entity.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ResourceFavoriteId implements Serializable {

    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "resource_id")
    private Long resourceId;
}
