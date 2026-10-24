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
import server.server.business.entity.enums.GoalStatus;
import server.server.business.facade.finance.GoalFacade;

@Tag(name = "Goals", description = "Финансовые цели")
@RestController
@RequestMapping("/api/v1/goals")
@PreAuthorize("isAuthenticated()")
public class GoalController {

    private final GoalFacade goalFacade;

    public GoalController(GoalFacade goalFacade) {
        this.goalFacade = goalFacade;
    }

    @Operation(summary = "Список целей")
    @GetMapping
    public ResponseEntity<Page<GoalResponse>> getAll(
            @RequestParam(required = false) GoalStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(goalFacade.getMyGoals(status, pageable));
    }

    @Operation(summary = "Получить цель по ID")
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(goalFacade.getById(id));
    }

    @Operation(summary = "Создать цель")
    @PostMapping
    public ResponseEntity<GoalResponse> create(@Valid @RequestBody CreateGoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalFacade.create(request));
    }

    @Operation(summary = "Обновить цель")
    @PatchMapping("/{id}")
    public ResponseEntity<GoalResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateGoalRequest request) {
        return ResponseEntity.ok(goalFacade.update(id, request));
    }

    @Operation(summary = "Пополнить цель")
    @PostMapping("/{id}/contribute")
    public ResponseEntity<GoalResponse> contribute(@PathVariable Long id,
                                                   @Valid @RequestBody ContributeToGoalRequest request) {
        return ResponseEntity.ok(goalFacade.contribute(id, request));
    }

    @Operation(summary = "Удалить цель")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        goalFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
