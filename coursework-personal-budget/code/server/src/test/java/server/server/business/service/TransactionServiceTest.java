package server.server.business.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import server.server.business.entity.enums.TransactionType;
import server.server.business.entity.finance.Transaction;
import server.server.business.entity.profile.Profile;
import server.server.business.repository.finance.TransactionRepository;
import server.server.business.service.finance.TransactionService;
import server.server.common.exception.common.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;

    @InjectMocks private TransactionService transactionService;

    // ---- findById ----

    @Test
    void findById_exists_returnsTransaction() {
        Transaction tx = new Transaction();
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));

        assertSame(tx, transactionService.findById(1L));
    }

    @Test
    void findById_missing_throwsEntityNotFoundException() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.findById(99L));
    }

    // ---- save ----

    @Test
    void save_delegatesToRepository() {
        Transaction tx = new Transaction();
        when(transactionRepository.save(tx)).thenReturn(tx);

        assertSame(tx, transactionService.save(tx));
    }

    // ---- delete (soft) ----

    @Test
    void delete_setsDeletedFlagAndSaves() {
        Transaction tx = new Transaction();
        tx.setDeleted(false);
        when(transactionRepository.save(tx)).thenReturn(tx);

        transactionService.delete(tx);

        assertTrue(tx.getDeleted());
        verify(transactionRepository).save(tx);
    }

    // ---- findByProfile (Specification-based) ----

    @Test
    void findByProfile_returnsPageFromRepository() {
        var pageable = PageRequest.of(0, 10);
        Page<Transaction> expected = new PageImpl<>(List.of(new Transaction()));
        when(transactionRepository.findAll(any(), eq(pageable))).thenReturn(expected);

        Page<Transaction> result = transactionService.findByProfile(
                1L, TransactionType.EXPENSE, null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    // ---- assertOwnership ----

    @Test
    void assertOwnership_correctOwner_doesNotThrow() {
        Profile profile = Profile.builder().build();
        profile.setId(7L);
        Transaction tx = Transaction.builder().profile(profile).build();

        assertDoesNotThrow(() -> transactionService.assertOwnership(tx, 7L));
    }

    @Test
    void assertOwnership_wrongOwner_throwsAccessDenied() {
        Profile profile = Profile.builder().build();
        profile.setId(7L);
        Transaction tx = Transaction.builder().profile(profile).build();

        assertThrows(TransactionService.TransactionAccessDeniedException.class,
                () -> transactionService.assertOwnership(tx, 99L));
    }
}
