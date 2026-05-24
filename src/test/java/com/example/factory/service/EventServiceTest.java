package com.example.factory.service;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.example.factory.dto.EventDTO;
import com.example.factory.model.StoredEvent;
import com.example.factory.store.EventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import java.time.Instant;
import java.util.List;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.factory.repository.StoredEventRepository;
class EventServiceTest {

    private EventService service;
    private StoredEventRepository repository;

    @BeforeEach
    void setUp() {

        repository = mock(StoredEventRepository.class);

        service = new EventService(
                new EventStore(),
                repository
        );
    }

    @Test
    void validEvent_isAccepted() {
        EventDTO event = new EventDTO(
                "E-1",
                Instant.now().minusSeconds(60),
                "M-001",
                1000,
                0
        );

        var response = service.ingest(List.of(event));

        assertEquals(1, response.accepted());
        assertEquals(0, response.rejected());
    }

    @Test
    void duplicateEvent_isDeduped() {

        Instant eventTime = Instant.now().minusSeconds(60);

        EventDTO event1 = new EventDTO(
                "E-1",
                eventTime,
                "M-001",
                1000,
                0
        );

        EventDTO event2 = new EventDTO(
                "E-1",
                eventTime,
                "M-001",
                1000,
                0
        );

        var response = service.ingest(List.of(event1, event2));

        assertEquals(1, response.accepted());
        assertEquals(1, response.deduped());
        assertEquals(0, response.rejected());
    }
    @Test
    void invalidDuration_isRejected() {

        EventDTO invalidEvent = new EventDTO(
                "E-2",
                Instant.now().minusSeconds(60),
                "M-002",
                -10,
                0
        );

        var response = service.ingest(List.of(invalidEvent));

        assertEquals(0, response.accepted());
        assertEquals(1, response.rejected());
        assertEquals(1, response.rejections().size());
        assertEquals("INVALID_DURATION", response.rejections().get(0).reason());
    }
    @Test
    void concurrentDuplicateEvents_areDeduped() throws InterruptedException {

        EventDTO event = new EventDTO(
                "E-CONCURRENT",
                Instant.now().minusSeconds(60),
                "M-100",
                1000,
                0
        );

        int threads = 10;
        int requests = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(requests);

        for (int i = 0; i < requests; i++) {
            executor.submit(() -> {
                try {
                    service.ingest(List.of(event));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        when(repository.findByMachineIdAndEventTimeBetween(
                any(),
                any(),
                any()
        )).thenReturn(
                List.of(
                        new StoredEvent(
                                event,
                                Instant.now()
                        )
                )
        );
        var stats = service.getStats(
                "M-100",
                Instant.now().minusSeconds(3600),
                Instant.now()
        );

        assertEquals(1, stats.eventsCount());
    }
    @Test
    void stats_respectEventTimeWindow_andIgnoreUnknownDefects() {

        Instant baseTime = Instant.now().minusSeconds(3600);

        EventDTO e1 = new EventDTO(
                "E-STAT-1",
                baseTime.plusSeconds(10),
                "M-STAT",
                1000,
                1
        );

        EventDTO e2 = new EventDTO(
                "E-STAT-2",
                baseTime.plusSeconds(20),
                "M-STAT",
                1000,
                -1   // unknown defect → must be ignored
        );

        EventDTO e3 = new EventDTO(
                "E-STAT-3",
                baseTime.plusSeconds(30),
                "M-STAT",
                1000,
                2
        );

        service.ingest(List.of(e1, e2, e3));
        when(repository.findByMachineIdAndEventTimeBetween(
                any(),
                any(),
                any()
        )).thenReturn(
                List.of(
                        new StoredEvent(e1, Instant.now()),
                        new StoredEvent(e2, Instant.now()),
                        new StoredEvent(e3, Instant.now())
                )
        );
        var stats = service.getStats(
                "M-STAT",
                baseTime,
                baseTime.plusSeconds(3600)
        );

        assertEquals(3, stats.eventsCount());
        assertEquals(3, stats.defectsCount()); // 1 + 2 (ignore -1)
        assertEquals("Warning", stats.status());

    }


}
