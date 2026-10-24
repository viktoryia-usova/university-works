package server.server.common.entity.auth;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import server.server.common.entity.classes.BaseEntity;
import server.server.common.entity.enums.Role;

@Entity
@Table(name = "sign_up_keys")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SignUpKey extends BaseEntity {

    @Column(nullable = false)
    private String username;

    @Column(name = "reg_key", nullable = false, length = 8)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
