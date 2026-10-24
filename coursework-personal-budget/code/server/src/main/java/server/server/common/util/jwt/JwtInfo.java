package server.server.common.util.jwt;

import lombok.Data;
import server.server.common.entity.auth.Account;

@Data
public class JwtInfo {
    private final String type = "Bearer";
    private Long id;
    private String username;
    private String role;
    private Boolean active;
    private String accessToken;
    private String refreshToken;

    public JwtInfo(Account account, String accessToken, String refreshToken) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.role = account.getRole().toString();
        this.active = account.getActive();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
