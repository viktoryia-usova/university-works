package server.server.business.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.profile.NotificationSettings;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    Optional<NotificationSettings> findByProfile_Id(Long profileId);
}
