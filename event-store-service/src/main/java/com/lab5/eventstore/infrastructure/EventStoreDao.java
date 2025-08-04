package com.lab5.eventstore.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;

import com.lab5.eventstore.domain.EventStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public class EventStoreDao {
    
    private final Dao<EventStore, Long> eventStoreDao;
    private final ConnectionSource connectionSource;

    public EventStoreDao(@Value("${spring.datasource.url}") String dbUrl,
                        @Value("${spring.datasource.username}") String dbUsername,
                        @Value("${spring.datasource.password}") String dbPassword) throws SQLException {
        this.connectionSource = new JdbcConnectionSource(dbUrl, dbUsername, dbPassword);
        this.eventStoreDao = DaoManager.createDao(connectionSource, EventStore.class);
    }
    
    // Trouver les événements par aggregate ID
    public List<EventStore> findByAggregateId(String aggregateId) throws SQLException {
        QueryBuilder<EventStore, Long> queryBuilder = eventStoreDao.queryBuilder();
        queryBuilder.where().eq("aggregate_id", aggregateId);
        queryBuilder.orderBy("version", true);
        return eventStoreDao.query(queryBuilder.prepare());
    }
    
    // Trouver les événements par type
    public List<EventStore> findByEventType(String eventType) throws SQLException {
        QueryBuilder<EventStore, Long> queryBuilder = eventStoreDao.queryBuilder();
        queryBuilder.where().eq("event_type", eventType);
        queryBuilder.orderBy("timestamp", true);
        return eventStoreDao.query(queryBuilder.prepare());
    }
    
    // Replay depuis une version
    public List<EventStore> findByAggregateIdFromVersion(String aggregateId, Integer fromVersion) throws SQLException {
        QueryBuilder<EventStore, Long> queryBuilder = eventStoreDao.queryBuilder();
        queryBuilder.where()
            .eq("aggregate_id", aggregateId)
            .and()
            .ge("version", fromVersion);
        queryBuilder.orderBy("version", true);
        return eventStoreDao.query(queryBuilder.prepare());
    }
    
    // Trouver tous les événements
    public List<EventStore> findAll() throws SQLException {
        QueryBuilder<EventStore, Long> queryBuilder = eventStoreDao.queryBuilder();
        queryBuilder.orderBy("timestamp", true);
        return eventStoreDao.query(queryBuilder.prepare());
    }
    
    // Créer un événement
    public void create(EventStore event) throws SQLException {
        eventStoreDao.create(event);
    }
}