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
import server.server.business.dto.finance.ContributeToGoalRequest;
import server.server.business.dto.shared.*;
import server.server.business.facade.shared.SharedSheetFacade;

@Tag(name = "Shared Sheets", description = "Совместные бюджетные листы")
@RestController
@RequestMapping("/api/v1/shared-sheets")
@PreAuthorize("isAuthenticated()")
public class SharedSheetController {

    private final SharedSheetFacade facade;

    public SharedSheetController(SharedSheetFacade facade) {
        this.facade = facade;
    }

    // ---- Sheets ----

    @Operation(summary = "Список совместных листов (где я участник)")
    @GetMapping
    public ResponseEntity<Page<SharedSheetResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(facade.getMySheets(pageable));
    }

    @Operation(summary = "Получить лист по ID")
    @GetMapping("/{id}")
    public ResponseEntity<SharedSheetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facade.getById(id));
    }

    @Operation(summary = "Создать совместный лист")
    @PostMapping
    public ResponseEntity<SharedSheetResponse> create(@Valid @RequestBody CreateSharedSheetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.create(request));
    }

    @Operation(summary = "Обновить совместный лист")
    @PatchMapping("/{id}")
    public ResponseEntity<SharedSheetResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateSharedSheetRequest request) {
        return ResponseEntity.ok(facade.update(id, request));
    }

    @Operation(summary = "Завершить совместный лист (только создатель)")
    @PostMapping("/{id}/complete")
    public ResponseEntity<SharedSheetResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(facade.complete(id));
    }

    @Operation(summary = "Удалить совместный лист (только создатель)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facade.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Members ----

    @Operation(summary = "Пригласить участника (из контактов)")
    @PostMapping("/{id}/members")
    public ResponseEntity<Void> invite(@PathVariable Long id,
                                       @Valid @RequestBody InviteMemberRequest request) {
        facade.inviteMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Покинуть лист (до первой транзакции)")
    @DeleteMapping("/{id}/members/me")
    public ResponseEntity<Void> leave(@PathVariable Long id) {
        facade.leaveSheet(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Transactions ----

    @Operation(summary = "Список транзакций в листе")
    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<SharedTransactionResponse>> getTransactions(
            @PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(facade.getTransactions(id, pageable));
    }

    @Operation(summary = "Добавить транзакцию в лист")
    @PostMapping("/{id}/transactions")
    public ResponseEntity<SharedTransactionResponse> addTransaction(
            @PathVariable Long id,
            @Valid @RequestBody CreateSharedTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.addTransaction(id, request));
    }

    @Operation(summary = "Удалить транзакцию из листа")
    @DeleteMapping("/{id}/transactions/{txId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, @PathVariable Long txId) {
        facade.deleteTransaction(id, txId);
        return ResponseEntity.noContent().build();
    }

    // ---- Goals ----

    @Operation(summary = "Список целей листа")
    @GetMapping("/{id}/goals")
    public ResponseEntity<Page<SharedGoalResponse>> getGoals(
            @PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(facade.getGoals(id, pageable));
    }

    @Operation(summary = "Создать цель в листе")
    @PostMapping("/{id}/goals")
    public ResponseEntity<SharedGoalResponse> createGoal(
            @PathVariable Long id,
            @Valid @RequestBody CreateSharedGoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.createGoal(id, request));
    }

    @Operation(summary = "Пополнить цель листа")
    @PostMapping("/{id}/goals/{goalId}/contribute")
    public ResponseEntity<SharedGoalResponse> contributeToGoal(
            @PathVariable Long id,
            @PathVariable Long goalId,
            @Valid @RequestBody ContributeToGoalRequest request) {
        return ResponseEntity.ok(facade.contributeToGoal(id, goalId, request));
    }

    @Operation(summary = "Удалить цель из листа")
    @DeleteMapping("/{id}/goals/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id, @PathVariable Long goalId) {
        facade.deleteGoal(id, goalId);
        return ResponseEntity.noContent().build();
    }

    // ---- Analytics ----

    @Operation(summary = "Аналитика совместного листа")
    @GetMapping("/{id}/analytics")
    public ResponseEntity<SharedSheetAnalyticsResponse> getAnalytics(@PathVariable Long id) {
        return ResponseEntity.ok(facade.getAnalytics(id));
    }
}
