package io.pillopl.library.commons.events.publisher;

import io.pillopl.library.commons.events.DomainEvent;
import io.pillopl.library.commons.events.DomainEvents;
import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation of the Store-and-Forward pattern for reliable event publishing.
 * Events are first saved to storage and then published periodically in batches.
 * This ensures that events are not lost if the publishing mechanism fails momentarily.
 */
@AllArgsConstructor
public class StoreAndForwardDomainEventPublisher implements DomainEvents {

    private final DomainEvents eventsPublisher;
    private final EventsStorage eventsStorage;

    /**
     * Saves the event to storage instead of publishing it immediately.
     *
     * @param event the domain event to store
     */
    @Override
    public void publish(DomainEvent event) {
        eventsStorage.save(event);
    }

    /**
     * Periodically retrieves unpublished events from storage and publishes them.
     * Marked as successful only after publishing.
     * Runs every 3000ms.
     */
    @Scheduled(fixedRate = 3000L)
    @Transactional
    public void publishAllPeriodically() {
        List<DomainEvent> domainEvents = eventsStorage.toPublish();
        domainEvents.forEach(eventsPublisher::publish);
        eventsStorage.published(domainEvents);
    }
}
