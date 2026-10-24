package server.server.common.dto.entity.key;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class SignUpKeyResponse {

    Long id;
    String username;
    String key;
    String role;
    LocalDateTime createdAt;
}
