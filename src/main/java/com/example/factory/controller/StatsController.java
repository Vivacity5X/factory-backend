package com.example.factory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.factory.dto.StatsResponse;
import com.example.factory.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
@Tag(
        name = "Analytics APIs",
        description = "Machine analytics and monitoring APIs"
)
@RestController
public class StatsController {

    private final EventService service;

    public StatsController(EventService service) {
        this.service = service;
    }
    @Operation(
            summary = "Get machine analytics by time window"
    )
    @GetMapping("/stats")
    public StatsResponse getStats(
            @RequestParam String machineId,
            @RequestParam Instant start,
            @RequestParam Instant end
    ) {
        return service.getStats(machineId, start, end);
    }
}
