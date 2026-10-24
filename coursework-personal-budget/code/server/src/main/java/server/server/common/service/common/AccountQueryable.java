package server.server.common.service.common;

import java.util.List;

public interface AccountQueryable<Entity> {
    List<Entity> findAllByAccountId(Long accountId);
}
