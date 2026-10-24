package server.server.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.server.business.dto.profile.*;
import server.server.business.facade.profile.ProfileFacade;

@Tag(name = "Profile", description = "Профиль, настройки приватности и уведомлений")
@RestController
@RequestMapping("/api/v1/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final ProfileFacade profileFacade;

    public ProfileController(ProfileFacade profileFacade) {
        this.profileFacade = profileFacade;
    }

    @Operation(summary = "Получить свой профиль")
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile() {
        return ResponseEntity.ok(profileFacade.getMyProfile());
    }

    @Operation(summary = "Обновить профиль")
    @PatchMapping("/me")
    public ResponseEntity<ProfileResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileFacade.updateMyProfile(request));
    }

    @Operation(summary = "Получить настройки приватности")
    @GetMapping("/me/privacy")
    public ResponseEntity<PrivacySettingsResponse> getPrivacy() {
        return ResponseEntity.ok(profileFacade.getPrivacySettings());
    }

    @Operation(summary = "Обновить настройки приватности")
    @PutMapping("/me/privacy")
    public ResponseEntity<PrivacySettingsResponse> updatePrivacy(@Valid @RequestBody UpdatePrivacySettingsRequest request) {
        return ResponseEntity.ok(profileFacade.updatePrivacySettings(request));
    }

    @Operation(summary = "Получить настройки уведомлений")
    @GetMapping("/me/notifications")
    public ResponseEntity<NotificationSettingsResponse> getNotifications() {
        return ResponseEntity.ok(profileFacade.getNotificationSettings());
    }

    @Operation(summary = "Обновить настройки уведомлений")
    @PutMapping("/me/notifications")
    public ResponseEntity<NotificationSettingsResponse> updateNotifications(@Valid @RequestBody UpdateNotificationSettingsRequest request) {
        return ResponseEntity.ok(profileFacade.updateNotificationSettings(request));
    }
}
