package com.example.factory.model;

import com.example.factory.dto.EventDTO;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
@Entity
public class StoredEvent {
    @Id
    private  String eventId;
    private  Instant eventTime;
    private  String machineId;
    private  long durationMs;
    private  int defectCount;
    private  Instant receivedTime;

    public StoredEvent() {
    }

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
