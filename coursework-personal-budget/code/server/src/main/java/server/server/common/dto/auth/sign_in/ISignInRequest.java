package server.server.common.dto.auth.sign_in;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public interface ISignInRequest {
    String getUsername();
    String getPassword();
}
