package server.server.business.event;

import org.springframework.context.ApplicationEvent;
import server.server.business.entity.shared.SharedSheet;
import server.server.business.entity.shared.SharedTransaction;

public class SharedTransactionAddedEvent extends ApplicationEvent {

    private final SharedTransaction transaction;
    private final SharedSheet       sheet;

    public SharedTransactionAddedEvent(Object source, SharedTransaction transaction, SharedSheet sheet) {
        super(source);
        this.transaction = transaction;
        this.sheet       = sheet;
    }

    public SharedTransaction getTransaction() { return transaction; }
    public SharedSheet       getSheet()       { return sheet; }
}
