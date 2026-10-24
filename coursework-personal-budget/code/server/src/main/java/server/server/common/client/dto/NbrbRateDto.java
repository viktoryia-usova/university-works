package server.server.common.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Response DTO from the National Bank of Belarus exchange rates API.
 * Example endpoint: GET https://www.nbrb.by/api/exrates/rates?periodicity=0
 */
public record NbrbRateDto(
        @JsonProperty("Cur_ID")          int curId,
        @JsonProperty("Cur_Abbreviation") String curAbbreviation,
        @JsonProperty("Cur_Scale")        int curScale,
        @JsonProperty("Cur_Name")         String curName,
        @JsonProperty("Cur_OfficialRate") BigDecimal curOfficialRate
) {}
