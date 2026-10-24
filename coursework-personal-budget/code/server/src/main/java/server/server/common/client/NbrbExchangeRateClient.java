package server.server.common.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import server.server.common.client.dto.NbrbRateDto;
import server.server.common.exception.common.ExternalApiException;
import server.server.common.util.log.AppLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * HTTP client for the National Bank of Belarus (NBRB) exchange rates API.
 * Rates are cached for 6 hours — the NBRB publishes rates once per working day.
 */
@Component
@RequiredArgsConstructor
public class NbrbExchangeRateClient {

    private static final String LOG = AppLogger.name(NbrbExchangeRateClient.class);
    /** NBRB public API v2 — moved from www.nbrb.by/api to api.nbrb.by */
    private static final String RATES_URL = "https://api.nbrb.by/exrates/rates?periodicity=0";
    private static final BigDecimal ONE = BigDecimal.ONE;

    private final RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate() {
        return restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Fetches all rates from NBRB (cached for 6 hours by Spring Cache).
     * Returns a map: currency abbreviation (e.g. "USD") → NbrbRateDto.
     */
    @Cacheable(value = "nbrbRates", unless = "#result == null || #result.isEmpty()")
    public Map<String, NbrbRateDto> getRatesMap() {
        AppLogger.info("Fetching exchange rates from NBRB API", LOG);
        try {
            List<NbrbRateDto> rates = restTemplate().exchange(
                    RATES_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<NbrbRateDto>>() {}
            ).getBody();

            if (rates == null || rates.isEmpty()) {
                throw new ExternalApiException("NBRB API returned empty rates list");
            }

            return rates.stream()
                    .collect(Collectors.toMap(
                            dto -> dto.curAbbreviation().toUpperCase(),
                            Function.identity()
                    ));
        } catch (Exception e) {
            AppLogger.error("Failed to fetch NBRB rates: " + e.getMessage(), LOG);
            throw new ExternalApiException("Cannot fetch exchange rates: " + e.getMessage());
        }
    }

    /**
     * Returns the official BYN rate for the given currency code.
     * For BYN itself, returns 1.0.
     * Rate is normalized by scale (e.g. if Cur_Scale=100, the per-unit rate = OfficialRate / 100).
     */
    public BigDecimal getRateToByn(String currencyCode) {
        if ("BYN".equalsIgnoreCase(currencyCode)) {
            return ONE;
        }

        Map<String, NbrbRateDto> ratesMap = getRatesMap();
        NbrbRateDto rate = Optional.ofNullable(ratesMap.get(currencyCode.toUpperCase()))
                .orElseThrow(() -> new ExternalApiException("Unsupported currency: " + currencyCode));

        return rate.curOfficialRate()
                .divide(BigDecimal.valueOf(rate.curScale()), 6, RoundingMode.HALF_UP);
    }

    /**
     * Returns a list of all supported currency codes (including BYN).
     */
    public List<String> getSupportedCurrencies() {
        List<String> currencies = getRatesMap().keySet().stream().sorted().collect(Collectors.toList());
        currencies.add(0, "BYN");
        return currencies;
    }

    /**
     * Returns all rate DTOs as a list.
     */
    public List<NbrbRateDto> getAllRates() {
        return List.copyOf(getRatesMap().values());
    }
}
