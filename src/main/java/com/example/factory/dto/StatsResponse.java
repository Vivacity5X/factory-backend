package com.example.factory.dto;

import java.time.Instant;

public record StatsResponse(
        String machineId,
        Instant start,
        Instant end,
        long eventsCount,
        long defectsCount,
        double avgDefectRate,
        String status
) {
}
