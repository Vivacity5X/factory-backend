package com.example.factory.controller;

import com.example.factory.dto.StatsResponse;
import com.example.factory.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class StatsController {

    private final EventService service;

    public StatsController(EventService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public StatsResponse getStats(
            @RequestParam String machineId,
            @RequestParam Instant start,
            @RequestParam Instant end
    ) {
        return service.getStats(machineId, start, end);
    }
}
