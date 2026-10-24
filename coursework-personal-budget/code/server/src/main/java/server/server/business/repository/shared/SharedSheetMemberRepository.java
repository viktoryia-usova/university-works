package server.server.business.repository.shared;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.business.entity.shared.SharedSheetMember;

import java.util.List;
import java.util.Optional;

public interface SharedSheetMemberRepository extends JpaRepository<SharedSheetMember, Long> {
    Optional<SharedSheetMember> findBySheet_IdAndProfile_Id(Long sheetId, Long profileId);
    List<SharedSheetMember> findBySheet_Id(Long sheetId);
    boolean existsBySheet_IdAndProfile_Id(Long sheetId, Long profileId);
    long countBySheet_Id(Long sheetId);
}
