package server.server.business.event;

import org.springframework.context.ApplicationEvent;
import server.server.business.entity.finance.PlannedExpense;

import java.math.BigDecimal;

public class BudgetExceededEvent extends ApplicationEvent {

    private final PlannedExpense plannedExpense;
    private final BigDecimal     actualSpent;

    public BudgetExceededEvent(Object source, PlannedExpense plannedExpense, BigDecimal actualSpent) {
        super(source);
        this.plannedExpense = plannedExpense;
        this.actualSpent    = actualSpent;
    }

    public PlannedExpense getPlannedExpense() { return plannedExpense; }
    public BigDecimal     getActualSpent()    { return actualSpent; }
}
