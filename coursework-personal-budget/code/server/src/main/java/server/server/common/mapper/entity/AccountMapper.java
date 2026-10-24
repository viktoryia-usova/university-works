package server.server.common.mapper.entity;

import org.mapstruct.Mapper;
import server.server.common.dto.entity.account.AccountResponse;
import server.server.common.entity.auth.Account;
import server.server.common.mapper.BaseReadOnlyMapper;

@Mapper(componentModel = "spring")
public abstract class AccountMapper implements BaseReadOnlyMapper<Account, AccountResponse> {

    @Override
    public abstract AccountResponse toDto(Account entity);

    @Override
    public AccountResponse toDtoWithRelations(Account entity) {
        return toDto(entity);
    }
}
