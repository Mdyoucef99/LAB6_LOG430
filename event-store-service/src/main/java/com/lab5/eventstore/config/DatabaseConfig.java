package com.lab5.eventstore.config;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lab5.eventstore.domain.EventStore;
import com.lab5.eventstore.infrastructure.EventStoreDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    private ConnectionSource connectionSource;
    
    @Bean
    public ConnectionSource connectionSource() throws SQLException {
        connectionSource = new JdbcConnectionSource(databaseUrl, username, password);
        
        // Drop and recreate the table to avoid sequence conflicts
        try {
            TableUtils.dropTable(connectionSource, EventStore.class, true);
        } catch (SQLException e) {
            // Ignore if table doesn't exist
        }
        TableUtils.createTable(connectionSource, EventStore.class);
        
        return connectionSource;
    }
    
    @Bean
    public EventStoreDao eventStoreDao(ConnectionSource connectionSource) throws SQLException {
        return new EventStoreDao(connectionSource);
    }
    
    @PreDestroy
    public void closeConnection() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}