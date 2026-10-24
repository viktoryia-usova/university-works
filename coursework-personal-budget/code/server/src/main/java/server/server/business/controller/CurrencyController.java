package server.server.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.server.business.facade.currency.CurrencyFacade;
import server.server.common.client.dto.NbrbRateDto;

import java.util.List;

@Tag(name = "Currencies", description = "Курсы валют (НБРБ)")
@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final CurrencyFacade currencyFacade;

    public CurrencyController(CurrencyFacade currencyFacade) {
        this.currencyFacade = currencyFacade;
    }

    @Operation(summary = "Все актуальные курсы валют НБРБ")
    @GetMapping("/rates")
    public ResponseEntity<List<NbrbRateDto>> getRates() {
        return ResponseEntity.ok(currencyFacade.getAllRates());
    }

    @Operation(summary = "Список поддерживаемых валют")
    @GetMapping("/supported")
    public ResponseEntity<List<String>> getSupportedCurrencies() {
        return ResponseEntity.ok(currencyFacade.getSupportedCurrencies());
    }
}
