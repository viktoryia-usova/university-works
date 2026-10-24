package server.server.common.dto.auth.key;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckSignUpKeyRequest implements ICheckSignUpKeyRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 8, max = 8, message = "Registration key must be exactly 8 digits")
    private String key;
}
