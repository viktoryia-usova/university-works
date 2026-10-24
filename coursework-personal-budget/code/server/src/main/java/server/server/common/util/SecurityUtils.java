package server.server.common.util;

import org.springframework.security.core.context.SecurityContextHolder;
import server.server.common.entity.auth.Account;

public final class SecurityUtils {

    private SecurityUtils() {}

    /** Returns the currently authenticated {@link Account} from Spring Security context. */
    public static Account currentAccount() {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long currentAccountId() {
        return currentAccount().getId();
    }
}
