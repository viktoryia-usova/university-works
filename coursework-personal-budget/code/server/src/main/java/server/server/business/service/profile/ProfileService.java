package server.server.business.service.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.business.entity.profile.NotificationSettings;
import server.server.business.entity.profile.PrivacySettings;
import server.server.business.entity.profile.Profile;
import server.server.business.repository.profile.NotificationSettingsRepository;
import server.server.business.repository.profile.PrivacySettingsRepository;
import server.server.business.repository.profile.ProfileRepository;
import server.server.common.exception.common.EntityNotFoundException;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final PrivacySettingsRepository privacySettingsRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final Random random = new Random();

    public ProfileService(ProfileRepository profileRepository,
                          PrivacySettingsRepository privacySettingsRepository,
                          NotificationSettingsRepository notificationSettingsRepository) {
        this.profileRepository = profileRepository;
        this.privacySettingsRepository = privacySettingsRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
    }

    public Profile getByAccountId(Long accountId) {
        return profileRepository.findByAccount_Id(accountId)
                .orElseThrow(() -> new EntityNotFoundException("accountId=" + accountId, "PROFILE_NOT_FOUND", Profile.class));
    }

    public Profile getByUsername(String username) {
        return profileRepository.findByAccount_Username(username)
                .orElseThrow(() -> new EntityNotFoundException("username=" + username, "PROFILE_NOT_FOUND", Profile.class));
    }

    public Profile getById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, "PROFILE_NOT_FOUND", Profile.class));
    }

    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    /**
     * Creates Profile with default PrivacySettings and NotificationSettings.
     * Called from AuthorizationService after account registration.
     */
    public Profile createWithDefaults(server.server.common.entity.auth.Account account,
                                      String firstName, String lastName) {
        Profile profile = Profile.builder()
                .account(account)
                .firstName(firstName)
                .lastName(lastName)
                .defaultCurrency("BYN")
                .friendCode(generateUniqueFriendCode())
                .build();
        profile = profileRepository.save(profile);

        PrivacySettings privacy = PrivacySettings.builder()
                .profile(profile)
                .build();
        privacySettingsRepository.save(privacy);

        NotificationSettings notifications = NotificationSettings.builder()
                .profile(profile)
                .build();
        notificationSettingsRepository.save(notifications);

        return profile;
    }

    public Page<Profile> searchPublic(String query, Long excludeProfileId, Pageable pageable) {
        return profileRepository.searchPublicProfiles(query, excludeProfileId, pageable);
    }

    // ---- Privacy & Notification settings ----

    public PrivacySettings getPrivacySettings(Long profileId) {
        return privacySettingsRepository.findByProfile_Id(profileId)
                .orElseThrow(() -> new EntityNotFoundException("profileId=" + profileId, "PRIVACY_NOT_FOUND", PrivacySettings.class));
    }

    public PrivacySettings savePrivacySettings(PrivacySettings settings) {
        return privacySettingsRepository.save(settings);
    }

    public NotificationSettings getNotificationSettings(Long profileId) {
        return notificationSettingsRepository.findByProfile_Id(profileId)
                .orElseThrow(() -> new EntityNotFoundException("profileId=" + profileId, "NOTIFICATION_NOT_FOUND", NotificationSettings.class));
    }

    public NotificationSettings saveNotificationSettings(NotificationSettings settings) {
        return notificationSettingsRepository.save(settings);
    }

    // ---- Helpers ----

    private String generateUniqueFriendCode() {
        String code;
        int attempts = 0;
        do {
            code = String.format("%08d", random.nextInt(100_000_000));
            attempts++;
            if (attempts > 100) throw new RuntimeException("Failed to generate unique friend code");
        } while (profileRepository.existsByFriendCode(code));
        return code;
    }

    public Optional<Profile> findByFriendCode(String code) {
        return profileRepository.findByFriendCode(code);
    }

    /** Resolves a profile by email (username) or friend code. */
    public Profile findByQuery(String query) {
        return profileRepository.findByAccount_Username(query)
                .or(() -> profileRepository.findByFriendCode(query))
                .orElseThrow(() -> new EntityNotFoundException("query=" + query, "PROFILE_NOT_FOUND", Profile.class));
    }
}
