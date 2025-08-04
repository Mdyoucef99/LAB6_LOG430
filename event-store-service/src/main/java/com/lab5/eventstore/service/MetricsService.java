package com.lab5.eventstore.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    
    private final Counter eventsStoredCounter;
    private final Counter eventsRetrievedCounter;
    private final Counter replayExecutedCounter;
    private final Timer eventStorageTimer;
    private final Timer eventRetrievalTimer;
    private final Timer replayTimer;
    private final Timer eventLatencyTimer;
    private final MeterRegistry meterRegistry;
    
    @Autowired
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize Prometheus metrics
        this.eventsStoredCounter = Counter.builder("eventstore_events_stored_total")
            .description("Total number of events stored")
            .register(meterRegistry);
            
        this.eventsRetrievedCounter = Counter.builder("eventstore_events_retrieved_total")
            .description("Total number of events retrieved")
            .register(meterRegistry);
            
        this.replayExecutedCounter = Counter.builder("eventstore_replay_executed_total")
            .description("Total number of event replays executed")
            .register(meterRegistry);
            
        this.eventStorageTimer = Timer.builder("eventstore_storage_duration_seconds")
            .description("Duration of event storage operations")
            .register(meterRegistry);
            
        this.eventRetrievalTimer = Timer.builder("eventstore_retrieval_duration_seconds")
            .description("Duration of event retrieval operations")
            .register(meterRegistry);
            
        this.replayTimer = Timer.builder("eventstore_replay_duration_seconds")
            .description("Duration of event replay operations")
            .register(meterRegistry);
            
        this.eventLatencyTimer = Timer.builder("eventstore_event_latency_seconds")
            .description("Latency from event emission to consumption")
            .register(meterRegistry);
    }
    
    public void incrementEventsStored() {
        eventsStoredCounter.increment();
    }
    
    public void incrementEventsRetrieved() {
        eventsRetrievedCounter.increment();
    }
    
    public void incrementReplayExecuted() {
        replayExecutedCounter.increment();
    }
    
    public Timer.Sample startStorageTimer() {
        return Timer.start(meterRegistry);
    }
    
    public Timer.Sample startRetrievalTimer() {
        return Timer.start(meterRegistry);
    }
    
    public Timer.Sample startReplayTimer() {
        return Timer.start(meterRegistry);
    }
    
    public Timer.Sample startLatencyTimer() {
        return Timer.start(meterRegistry);
    }
    
    // Getter methods for timers
    public Timer getEventStorageTimer() {
        return eventStorageTimer;
    }
    
    public Timer getEventRetrievalTimer() {
        return eventRetrievalTimer;
    }
    
    public Timer getReplayTimer() {
        return replayTimer;
    }
    
    public Timer getEventLatencyTimer() {
        return eventLatencyTimer;
    }
} 