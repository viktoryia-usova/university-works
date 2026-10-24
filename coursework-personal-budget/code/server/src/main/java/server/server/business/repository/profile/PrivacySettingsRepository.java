package server.server.business.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.profile.PrivacySettings;

import java.util.Optional;

public interface PrivacySettingsRepository extends JpaRepository<PrivacySettings, Long> {
    Optional<PrivacySettings> findByProfile_Id(Long profileId);
}
