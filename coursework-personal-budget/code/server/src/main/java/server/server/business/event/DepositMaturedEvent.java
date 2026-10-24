package server.server.business.event;

import org.springframework.context.ApplicationEvent;
import server.server.business.entity.finance.Deposit;

public class DepositMaturedEvent extends ApplicationEvent {

    private final Deposit deposit;

    public DepositMaturedEvent(Object source, Deposit deposit) {
        super(source);
        this.deposit = deposit;
    }

    public Deposit getDeposit() {
        return deposit;
    }
}
