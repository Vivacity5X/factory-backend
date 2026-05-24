package com.example.factory.repository;
import com.example.factory.model.StoredEvent;

import java.time.Instant;
import java.util.List;
import com.example.factory.model.StoredEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredEventRepository
        extends JpaRepository<StoredEvent, String> {
    List<StoredEvent>
    findByMachineIdAndEventTimeBetween(
            String machineId,
            Instant start,
            Instant end
    );
}