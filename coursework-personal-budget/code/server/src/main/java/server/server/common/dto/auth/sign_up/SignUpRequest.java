package server.server.common.dto.auth.sign_up;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import server.server.common.dto.auth.sign_up.ISignUpRequest;

@Data
public class SignUpRequest implements ISignUpRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
