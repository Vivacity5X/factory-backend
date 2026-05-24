package com.example.factory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.factory.dto.BatchResponse;
import com.example.factory.dto.EventDTO;
import com.example.factory.service.EventService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Tag(
        name = "Event APIs",
        description = "Batch machine event ingestion APIs"
)
@RestController
public class EventController {

    private final EventService service;


    public EventController(EventService service) {
        this.service = service;
    }
    @Operation(
            summary = "Ingest machine telemetry events"
    )
    @PostMapping("/events/batch")
    public BatchResponse ingest(@RequestBody List<EventDTO> events) {

        return service.ingest(events);
    }
}
