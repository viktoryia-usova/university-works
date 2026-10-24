package server.server.business.facade.currency;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.server.common.client.NbrbExchangeRateClient;
import server.server.common.client.dto.NbrbRateDto;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class CurrencyFacade {

    private final NbrbExchangeRateClient nbrbClient;

    public CurrencyFacade(NbrbExchangeRateClient nbrbClient) {
        this.nbrbClient = nbrbClient;
    }

    public List<NbrbRateDto> getAllRates() {
        return nbrbClient.getAllRates();
    }

    public List<String> getSupportedCurrencies() {
        return nbrbClient.getSupportedCurrencies();
    }
}
