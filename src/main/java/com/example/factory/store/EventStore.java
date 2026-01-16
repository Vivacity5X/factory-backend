package com.example.factory.store;

import com.example.factory.dto.EventDTO;
import com.example.factory.exception.DedupException;
import com.example.factory.exception.IgnoreUpdateException;
import com.example.factory.model.StoredEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventStore {

    private final ConcurrentHashMap<String, StoredEvent> store =
            new ConcurrentHashMap<>();


    public StoreResult upsert(EventDTO dto) {

        Instant now = Instant.now();

        try {
            store.compute(dto.eventId(), (eventId, existing) -> {

                if (existing == null) {
                    return new StoredEvent(dto, now);
                }

                if (existing.equalsPayload(dto)) {
                    throw new DedupException();
                }

                if (now.isAfter(existing.getReceivedTime())) {
                    return new StoredEvent(dto, now);
                }

                throw new IgnoreUpdateException();
            });

            return StoreResult.ACCEPTED;

        } catch (DedupException e) {
            return StoreResult.DEDUPED;

        } catch (IgnoreUpdateException e) {
            return StoreResult.IGNORED;
        }
    }

    public Collection<StoredEvent> all() {
        return store.values();
    }
}
