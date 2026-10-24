package server.server.business.event;

import org.springframework.context.ApplicationEvent;
import server.server.business.entity.social.Contact;

public class ContactRequestEvent extends ApplicationEvent {

    private final Contact contact;

    public ContactRequestEvent(Object source, Contact contact) {
        super(source);
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}
