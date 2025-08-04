package com.lab5.eventstore.infrastructure;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.lab5.eventstore.domain.EventStore;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public class EventStoreDao extends BaseDaoImpl<EventStore, Long> {
    
    public EventStoreDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, EventStore.class);
    }
    
    // Trouver les événements par aggregate ID
    public List<EventStore> findByAggregateId(String aggregateId) throws SQLException {
        QueryBuilder<EventStore, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq("aggregate_id", aggregateId);
        queryBuilder.orderBy("version", true);
        return query(queryBuilder.prepare());
    }
    
    // Trouver les événements par type
    public List<EventStore> findByEventType(String eventType) throws SQLException {
        QueryBuilder<EventStore, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq("event_type", eventType);
        queryBuilder.orderBy("timestamp", true);
        return query(queryBuilder.prepare());
    }
    
    // Replay depuis une version
    public List<EventStore> findByAggregateIdFromVersion(String aggregateId, Integer fromVersion) throws SQLException {
        QueryBuilder<EventStore, Long> queryBuilder = queryBuilder();
        queryBuilder.where()
            .eq("aggregate_id", aggregateId)
            .and()
            .ge("version", fromVersion);
        queryBuilder.orderBy("version", true);
        return query(queryBuilder.prepare());
    }
    
    // Trouver tous les événements
    public List<EventStore> findAll() throws SQLException {
        QueryBuilder<EventStore, Long> queryBuilder = queryBuilder();
        queryBuilder.orderBy("timestamp", true);
        return query(queryBuilder.prepare());
    }
}