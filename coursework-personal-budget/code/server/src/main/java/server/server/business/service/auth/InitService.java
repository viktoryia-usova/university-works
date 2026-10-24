package server.server.business.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import server.server.business.service.profile.ProfileService;
import server.server.common.entity.auth.Account;
import server.server.common.repository.auth.AccountRepository;
import server.server.common.service.auth.BaseInitService;

@Service
public class InitService extends BaseInitService {

    private final ProfileService profileService;

    public InitService(AccountRepository repository,
                       PasswordEncoder passwordEncoder,
                       ProfileService profileService) {
        super(repository, passwordEncoder);
        this.profileService = profileService;
    }

    @Override
    protected void afterAdminCreated(Account admin) {
        profileService.createWithDefaults(admin, "Admin", "");
    }
}
