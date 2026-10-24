package server.server.common.dto.entity.account;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeActiveRequest {

    @NotNull(message = "Active status must not be null")
    private Boolean active;
}
