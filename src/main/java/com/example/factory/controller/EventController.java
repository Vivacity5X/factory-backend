package com.example.factory.controller;

import com.example.factory.dto.BatchResponse;
import com.example.factory.dto.EventDTO;
import com.example.factory.service.EventService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping("/events/batch")
    public BatchResponse ingest(@RequestBody List<EventDTO> events) {
        return service.ingest(events);
    }
}
