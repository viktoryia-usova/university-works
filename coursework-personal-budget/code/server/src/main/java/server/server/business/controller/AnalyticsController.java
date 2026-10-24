package server.server.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.server.business.dto.analytics.AnalyticsSummaryResponse;
import server.server.business.dto.analytics.MonthlyEntryResponse;
import server.server.business.facade.analytics.AnalyticsFacade;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Analytics", description = "Аналитика доходов, расходов и баланса")
@RestController
@RequestMapping("/api/v1/analytics")
@PreAuthorize("isAuthenticated()")
public class AnalyticsController {

    private final AnalyticsFacade analyticsFacade;

    public AnalyticsController(AnalyticsFacade analyticsFacade) {
        this.analyticsFacade = analyticsFacade;
    }

    @Operation(summary = "Общая сводка (доходы + расходы + баланс) по периоду")
    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryResponse> getSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(analyticsFacade.getSummary(from, to));
    }

    @Operation(summary = "Доходы по категориям за период")
    @GetMapping("/income")
    public ResponseEntity<AnalyticsSummaryResponse> getIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(analyticsFacade.getIncome(from, to));
    }

    @Operation(summary = "Расходы по категориям за период")
    @GetMapping("/expenses")
    public ResponseEntity<AnalyticsSummaryResponse> getExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(analyticsFacade.getExpenses(from, to));
    }

    @Operation(summary = "Динамика доходов / расходов / баланса по месяцам")
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyEntryResponse>> getMonthly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(analyticsFacade.getMonthly(from, to));
    }
}
