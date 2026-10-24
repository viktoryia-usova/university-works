package server.server.common.dto.entity.account;

import lombok.Data;

@Data
public class AccountResponse {
    private Long id;
    private String username;
    private Boolean active;
}
