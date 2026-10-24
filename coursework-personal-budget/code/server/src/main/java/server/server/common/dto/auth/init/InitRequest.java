package server.server.common.dto.auth.init;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InitRequest implements IInitRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
