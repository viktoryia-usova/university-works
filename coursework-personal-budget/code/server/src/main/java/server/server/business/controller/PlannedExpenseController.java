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
import server.server.business.facade.finance.PlannedExpenseFacade;

@Tag(name = "Planned Expenses", description = "Плановые расходы и бюджетные лимиты")
@RestController
@RequestMapping("/api/v1/planned-expenses")
@PreAuthorize("isAuthenticated()")
public class PlannedExpenseController {

    private final PlannedExpenseFacade facade;

    public PlannedExpenseController(PlannedExpenseFacade facade) {
        this.facade = facade;
    }

    @Operation(summary = "Список плановых расходов")
    @GetMapping
    public ResponseEntity<Page<PlannedExpenseResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(facade.getMyExpenses(pageable));
    }

    @Operation(summary = "Получить плановый расход по ID")
    @GetMapping("/{id}")
    public ResponseEntity<PlannedExpenseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facade.getById(id));
    }

    @Operation(summary = "Создать плановый расход")
    @PostMapping
    public ResponseEntity<PlannedExpenseResponse> create(@Valid @RequestBody CreatePlannedExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.create(request));
    }

    @Operation(summary = "Обновить плановый расход")
    @PatchMapping("/{id}")
    public ResponseEntity<PlannedExpenseResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody UpdatePlannedExpenseRequest request) {
        return ResponseEntity.ok(facade.update(id, request));
    }

    @Operation(summary = "Удалить плановый расход")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
