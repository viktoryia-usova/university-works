package server.server.business.repository.social;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.server.business.entity.enums.ContactStatus;
import server.server.business.entity.profile.Profile;
import server.server.business.entity.social.Contact;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByRequester_IdAndAddressee_Id(Long requesterId, Long addresseeId);

    @Query("""
        SELECT c FROM Contact c
        WHERE (c.requester.id = :profileId OR c.addressee.id = :profileId)
          AND c.status = :status
        """)
    Page<Contact> findAllByProfileAndStatus(Long profileId, ContactStatus status, Pageable pageable);

    /** Returns both incoming (as addressee) and outgoing (as requester) PENDING requests. */
    @Query("""
        SELECT c FROM Contact c
        WHERE (c.addressee.id = :profileId OR c.requester.id = :profileId)
          AND c.status = 'PENDING'
        """)
    Page<Contact> findPendingRequestsForProfile(Long profileId, Pageable pageable);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Contact c
        WHERE ((c.requester.id = :profileId1 AND c.addressee.id = :profileId2)
            OR (c.requester.id = :profileId2 AND c.addressee.id = :profileId1))
          AND c.status = 'ACCEPTED'
        """)
    boolean areContacts(Long profileId1, Long profileId2);

    /** True if an ACTIVE (PENDING or ACCEPTED) contact record exists between the two profiles. */
    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Contact c
        WHERE ((c.requester.id = :requesterId AND c.addressee.id = :addresseeId)
            OR (c.requester.id = :addresseeId AND c.addressee.id = :requesterId))
          AND c.status IN ('PENDING', 'ACCEPTED')
        """)
    boolean existsRequest(Long requesterId, Long addresseeId);

    /** Find any contact record between two profiles regardless of direction or status. */
    @Query("""
        SELECT c FROM Contact c
        WHERE (c.requester.id = :p1 AND c.addressee.id = :p2)
           OR (c.requester.id = :p2 AND c.addressee.id = :p1)
        """)
    Optional<Contact> findByParticipants(Long p1, Long p2);

    @Query("""
        SELECT c FROM Contact c
        WHERE (c.requester.id = :profileId OR c.addressee.id = :profileId)
          AND c.status = 'ACCEPTED'
          AND (
              LOWER(CASE WHEN c.requester.id = :profileId THEN c.addressee.account.username ELSE c.requester.account.username END)
              LIKE LOWER(CONCAT('%', :search, '%'))
          )
        """)
    Page<Contact> searchAcceptedContacts(Long profileId, String search, Pageable pageable);
}
