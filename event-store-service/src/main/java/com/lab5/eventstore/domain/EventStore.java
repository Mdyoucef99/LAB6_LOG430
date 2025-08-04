package com.lab5.eventstore.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "events")
public class EventStore {
    @DatabaseField(generatedId = true)
    private Long id;
    
    @DatabaseField(columnName = "event_id", canBeNull = false)
    private String eventId;
    
    @DatabaseField(columnName = "event_type", canBeNull = false)
    private String eventType;
    
    @DatabaseField(columnName = "aggregate_id", canBeNull = false)
    private String aggregateId;
    
    @DatabaseField(columnName = "event_data", canBeNull = false)
    private String eventData;
    
    @DatabaseField(columnName = "timestamp", canBeNull = false)
    private LocalDateTime timestamp;
    
    @DatabaseField(columnName = "version", canBeNull = false)
    private Integer version;

    // Constructeurs
    public EventStore() {}
    
    public EventStore(String eventId, String eventType, String aggregateId, 
                     String eventData, LocalDateTime timestamp, Integer version) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.eventData = eventData;
        this.timestamp = timestamp;
        this.version = version;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}