package server.server.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.server.business.dto.finance.CreateTransactionRequest;
import server.server.business.dto.finance.TransactionResponse;
import server.server.business.dto.finance.UpdateTransactionRequest;
import server.server.business.entity.enums.TransactionType;
import server.server.business.facade.finance.TransactionFacade;

import java.time.LocalDate;

@Tag(name = "Transactions", description = "Транзакции (доходы и расходы)")
@RestController
@RequestMapping("/api/v1/transactions")
@PreAuthorize("isAuthenticated()")
public class TransactionController {

    private final TransactionFacade transactionFacade;

    public TransactionController(TransactionFacade transactionFacade) {
        this.transactionFacade = transactionFacade;
    }

    @Operation(summary = "Список транзакций с фильтрацией и пагинацией")
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAll(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String currency,
            Pageable pageable) {
        return ResponseEntity.ok(transactionFacade.getMyTransactions(type, categoryId, dateFrom, dateTo, currency, pageable));
    }

    @Operation(summary = "Получить транзакцию по ID")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionFacade.getById(id));
    }

    @Operation(summary = "Создать транзакцию")
    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionFacade.create(request));
    }

    @Operation(summary = "Обновить транзакцию")
    @PatchMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateTransactionRequest request) {
        return ResponseEntity.ok(transactionFacade.update(id, request));
    }

    @Operation(summary = "Удалить транзакцию")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
