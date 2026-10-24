package server.server.common.service.common;

import java.util.List;

public interface AccountSingleQueryable<Entity> {
    Entity findByAccountId(Long accountId);
}
