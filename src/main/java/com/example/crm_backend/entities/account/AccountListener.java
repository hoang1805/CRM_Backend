package com.example.crm_backend.entities.account;

import com.example.crm_backend.entities.HasListener;
import com.example.crm_backend.events.AccountEvent;
import com.example.crm_backend.events.AppEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AccountListener implements HasListener<AccountEvent> {
    @EventListener
    public void handleEvent(AccountEvent event) {
        switch (event.getEvent()) {
            case CREATED:
                onCreate(event);
                break;
            case EDITED:
                onEdit(event);
                break;
            case DELETED:
                onDelete(event);
                break;
            case IMPORTED:
                onImport(event);
                break;
            default:
                throw new IllegalStateException("Unknown event type: " + event.getEvent());
        }
    }

    private void onCreate(AccountEvent event) {
        event.getService().getSearchEngine().remove("accounts");
    }

    private void onEdit(AccountEvent event) {
        event.getService().getSearchEngine().remove("accounts");
    }

    private void onDelete(AccountEvent event) {
        event.getService().getSearchEngine().remove("accounts");
    }

    private void onImport(AccountEvent event) {
        event.getService().getSearchEngine().remove("accounts");
    }
}
