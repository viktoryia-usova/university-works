package server.server.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.server.business.dto.finance.*;
import server.server.business.entity.enums.DepositStatus;
import server.server.business.entity.enums.DepositType;
import server.server.business.facade.finance.DepositFacade;

@Tag(name = "Deposits", description = "Депозиты и копилки")
@RestController
@RequestMapping("/api/v1/deposits")
@PreAuthorize("isAuthenticated()")
public class DepositController {

    private final DepositFacade depositFacade;

    public DepositController(DepositFacade depositFacade) {
        this.depositFacade = depositFacade;
    }

    @Operation(summary = "Список депозитов (с фильтром по статусу/типу)")
    @GetMapping
    public ResponseEntity<Page<DepositResponse>> getAll(
            @RequestParam(required = false) DepositStatus status,
            @RequestParam(required = false) DepositType type,
            Pageable pageable) {
        return ResponseEntity.ok(depositFacade.getMyDeposits(status, type, pageable));
    }

    @Operation(summary = "Получить депозит по ID")
    @GetMapping("/{id}")
    public ResponseEntity<DepositResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(depositFacade.getById(id));
    }

    @Operation(summary = "Создать депозит/копилку")
    @PostMapping
    public ResponseEntity<DepositResponse> create(@Valid @RequestBody CreateDepositRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(depositFacade.create(request));
    }

    @Operation(summary = "Обновить депозит")
    @PatchMapping("/{id}")
    public ResponseEntity<DepositResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateDepositRequest request) {
        return ResponseEntity.ok(depositFacade.update(id, request));
    }

    @Operation(summary = "Пополнить копилку (только PIGGY_BANK)")
    @PostMapping("/{id}/add-funds")
    public ResponseEntity<DepositResponse> addFunds(@PathVariable Long id,
                                                    @Valid @RequestBody AddFundsRequest request) {
        return ResponseEntity.ok(depositFacade.addFunds(id, request));
    }

    @Operation(summary = "Удалить депозит")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        depositFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
