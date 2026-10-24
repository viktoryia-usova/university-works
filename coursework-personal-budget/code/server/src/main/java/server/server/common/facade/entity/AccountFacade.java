package server.server.common.facade.entity;

import org.springframework.stereotype.Service;
import server.server.common.dto.entity.account.AccountResponse;
import server.server.common.dto.entity.account.ChangeActiveRequest;
import server.server.common.dto.entity.account.ChangePasswordRequest;
import server.server.common.entity.auth.Account;
import server.server.common.facade.BaseReadOnlyFacade;
import server.server.common.mapper.entity.AccountMapper;
import server.server.common.service.entity.AccountService;

@Service
public class AccountFacade extends BaseReadOnlyFacade<Account, Long, AccountMapper, AccountService, AccountResponse> {

    private final AccountService accountService;

    public AccountFacade(AccountService service, AccountMapper mapper) {
        super(service, mapper);
        this.accountService = service;
    }

    public AccountResponse changePassword(Long id, ChangePasswordRequest request) {
        Account account = accountService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return mapper.toDto(account);
    }

    public AccountResponse changeActive(Long id, ChangeActiveRequest request) {
        Account account = accountService.changeActive(id, request.getActive());
        return mapper.toDto(account);
    }
}
