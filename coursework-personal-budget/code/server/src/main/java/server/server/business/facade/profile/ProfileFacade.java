package server.server.business.facade.profile;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.dto.profile.*;
import server.server.business.entity.profile.NotificationSettings;
import server.server.business.entity.profile.PrivacySettings;
import server.server.business.entity.profile.Profile;
import server.server.business.mapper.profile.NotificationSettingsMapper;
import server.server.business.mapper.profile.PrivacySettingsMapper;
import server.server.business.mapper.profile.ProfileMapper;
import server.server.business.service.profile.ProfileService;
import server.server.common.util.SecurityUtils;

@Component
@Transactional
public class ProfileFacade {

    private final ProfileService             profileService;
    private final ProfileMapper              profileMapper;
    private final PrivacySettingsMapper      privacyMapper;
    private final NotificationSettingsMapper notifMapper;

    public ProfileFacade(ProfileService profileService,
                         ProfileMapper profileMapper,
                         PrivacySettingsMapper privacyMapper,
                         NotificationSettingsMapper notifMapper) {
        this.profileService = profileService;
        this.profileMapper  = profileMapper;
        this.privacyMapper  = privacyMapper;
        this.notifMapper    = notifMapper;
    }

    public ProfileResponse getMyProfile() {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return profileMapper.toDto(profile);
    }

    public ProfileResponse updateMyProfile(UpdateProfileRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        if (request.firstName()       != null) profile.setFirstName(request.firstName());
        if (request.lastName()        != null) profile.setLastName(request.lastName());
        if (request.phone()           != null) profile.setPhone(request.phone());
        if (request.birthDate()       != null) profile.setBirthDate(request.birthDate());
        if (request.defaultCurrency() != null) profile.setDefaultCurrency(request.defaultCurrency());
        return profileMapper.toDto(profileService.save(profile));
    }

    public PrivacySettingsResponse getPrivacySettings() {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return privacyMapper.toDto(profileService.getPrivacySettings(profile.getId()));
    }

    public PrivacySettingsResponse updatePrivacySettings(UpdatePrivacySettingsRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        PrivacySettings settings = profileService.getPrivacySettings(profile.getId());
        if (request.searchableByEmail()      != null) settings.setSearchableByEmail(request.searchableByEmail());
        if (request.searchableByPhone()      != null) settings.setSearchableByPhone(request.searchableByPhone());
        if (request.searchableByFriendCode() != null) settings.setSearchableByFriendCode(request.searchableByFriendCode());
        if (request.showFullName()           != null) settings.setShowFullName(request.showFullName());
        return privacyMapper.toDto(profileService.savePrivacySettings(settings));
    }

    public NotificationSettingsResponse getNotificationSettings() {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        return notifMapper.toDto(profileService.getNotificationSettings(profile.getId()));
    }

    public NotificationSettingsResponse updateNotificationSettings(UpdateNotificationSettingsRequest request) {
        Profile profile = profileService.getByAccountId(SecurityUtils.currentAccountId());
        NotificationSettings settings = profileService.getNotificationSettings(profile.getId());
        if (request.onGoalReached()       != null) settings.setOnGoalReached(request.onGoalReached());
        if (request.onBudgetExceeded()    != null) settings.setOnBudgetExceeded(request.onBudgetExceeded());
        if (request.onSharedTransaction() != null) settings.setOnSharedTransaction(request.onSharedTransaction());
        if (request.onContactRequest()    != null) settings.setOnContactRequest(request.onContactRequest());
        if (request.onDepositMatured()    != null) settings.setOnDepositMatured(request.onDepositMatured());
        return notifMapper.toDto(profileService.saveNotificationSettings(settings));
    }
}
