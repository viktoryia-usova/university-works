package server.server.common.dto.auth.sign_in;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import server.server.common.dto.auth.sign_in.ISignInRequest;

@Data
public class SignInRequest implements ISignInRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
