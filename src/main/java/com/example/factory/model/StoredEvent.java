package com.example.factory.model;

import com.example.factory.dto.EventDTO;

import java.time.Instant;
import java.util.Objects;

public class StoredEvent {

    private final String eventId;
    private final Instant eventTime;
    private final String machineId;
    private final long durationMs;
    private final int defectCount;
    private final Instant receivedTime;

    public StoredEvent(EventDTO dto, Instant receivedTime) {
        this.eventId = dto.eventId();
        this.eventTime = dto.eventTime();
        this.machineId = dto.machineId();
        this.durationMs = dto.durationMs();
        this.defectCount = dto.defectCount();
        this.receivedTime = receivedTime;
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public String getMachineId() {
        return machineId;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public int getDefectCount() {
        return defectCount;
    }

    public Instant getReceivedTime() {
        return receivedTime;
    }

    public boolean equalsPayload(EventDTO dto) {
        return Objects.equals(this.eventTime, dto.eventTime()) &&
                Objects.equals(this.machineId, dto.machineId()) &&
                this.durationMs == dto.durationMs() &&
                this.defectCount == dto.defectCount();
    }
}
