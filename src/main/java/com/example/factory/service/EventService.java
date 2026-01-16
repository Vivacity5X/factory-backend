package com.example.factory.service;

import com.example.factory.dto.BatchResponse;
import com.example.factory.dto.EventDTO;
import com.example.factory.dto.Rejection;
import com.example.factory.exception.ValidationException;
import com.example.factory.store.EventStore;
import com.example.factory.store.StoreResult;
import org.springframework.stereotype.Service;

import com.example.factory.dto.StatsResponse;
import com.example.factory.model.StoredEvent;

import java.time.Duration;
import java.util.List;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private static final long MAX_DURATION_MS = 6L * 60 * 60 * 1000; // 6 hours

    private final EventStore store;

    public EventService(EventStore store) {
        this.store = store;
    }

    public BatchResponse ingest(List<EventDTO> events) {

        int accepted = 0;
        int deduped = 0;
        int rejected = 0;

        List<Rejection> rejections = new ArrayList<>();

        for (EventDTO event : events) {
            try {
                validate(event);

                StoreResult result = store.upsert(event);

                if (result == StoreResult.ACCEPTED) {
                    accepted++;
                } else if (result == StoreResult.DEDUPED) {
                    deduped++;
                }


            } catch (ValidationException ex) {
                rejected++;
                rejections.add(new Rejection(event.eventId(), ex.getMessage()));
            }
        }

        return new BatchResponse(
                accepted,
                deduped,
                rejected,
                rejections
        );
    }


    private void validate(EventDTO event) {

        if (event.durationMs() < 0 || event.durationMs() > MAX_DURATION_MS) {
            throw new ValidationException("INVALID_DURATION");
        }

        Instant maxAllowedTime = Instant.now().plusSeconds(15 * 60);
        if (event.eventTime().isAfter(maxAllowedTime)) {
            throw new ValidationException("FUTURE_EVENT");
        }
    }
    public StatsResponse getStats(String machineId, Instant start, Instant end) {

        List<StoredEvent> filteredEvents = store.all().stream()
                .filter(e -> e.getMachineId().equals(machineId))
                .filter(e -> !e.getEventTime().isBefore(start))
                .filter(e -> e.getEventTime().isBefore(end))
                .toList();

        long eventsCount = filteredEvents.size();

        long defectsCount = filteredEvents.stream()
                .filter(e -> e.getDefectCount() >= 0)
                .mapToLong(StoredEvent::getDefectCount)
                .sum();

        double windowHours =
                Duration.between(start, end).toSeconds() / 3600.0;

        double avgDefectRate =
                windowHours > 0 ? defectsCount / windowHours : 0.0;

        String status =
                avgDefectRate < 2.0 ? "Healthy" : "Warning";

        return new StatsResponse(
                machineId,
                start,
                end,
                eventsCount,
                defectsCount,
                avgDefectRate,
                status
        );
    }

}
