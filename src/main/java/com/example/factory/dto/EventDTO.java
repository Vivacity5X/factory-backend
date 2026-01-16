package com.example.factory.dto;

import java.time.Instant;

public record EventDTO(
        String eventId,
        Instant eventTime,
        String machineId,
        long durationMs,
        int defectCount
) {
}
