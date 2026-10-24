package server.server.common.dto.entity.key;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import server.server.common.entity.enums.Role;

@Data
public class CreateSignUpKeyRequest {

    @NotBlank
    private String username;

    @NotNull
    private Role role;
}
