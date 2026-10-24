package server.server.business.facade.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.admin.UserResponse;
import server.server.business.entity.profile.Profile;
import server.server.business.repository.profile.ProfileRepository;
import server.server.common.entity.auth.Account;
import server.server.common.entity.enums.Role;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.service.entity.AccountService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class AdminFacade {

    private final AccountService      accountService;
    private final AccountRepository   accountRepository;
    private final ProfileRepository   profileRepository;

    public AdminFacade(AccountService accountService,
                       AccountRepository accountRepository,
                       ProfileRepository profileRepository) {
        this.accountService    = accountService;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
    }

    public Page<UserResponse> getUsers(Pageable pageable) {
        Page<Account> page = accountRepository.findAll(pageable);
        List<UserResponse> responses = page.stream()
                .filter(a -> a.getRole() == Role.USER)
                .map(this::toUserResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    public UserResponse blockUser(Long accountId) {
        Account account = accountService.changeActive(accountId, false);
        return toUserResponse(account);
    }

    public UserResponse unblockUser(Long accountId) {
        Account account = accountService.changeActive(accountId, true);
        return toUserResponse(account);
    }

    private UserResponse toUserResponse(Account account) {
        Optional<Profile> profileOpt = profileRepository.findByAccount_Id(account.getId());
        return new UserResponse(
                account.getId(),
                account.getUsername(),
                account.getRole(),
                account.getActive(),
                account.getCreatedAt(),
                profileOpt.map(Profile::getId).orElse(null),
                profileOpt.map(Profile::getFirstName).orElse(null),
                profileOpt.map(Profile::getLastName).orElse(null),
                profileOpt.map(Profile::getPhone).orElse(null),
                profileOpt.map(Profile::getFriendCode).orElse(null));
    }
}
