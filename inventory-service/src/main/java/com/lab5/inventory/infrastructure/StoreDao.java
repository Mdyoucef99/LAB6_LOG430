package com.lab5.inventory.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.inventory.domain.Store;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StoreDao {
    private final Dao<Store, Integer> dao;

    public StoreDao(DataSource dataSource) throws SQLException {
        String databaseUrl = "jdbc:postgresql://inventory-db:5432/inventory_db";
        ConnectionSource connectionSource = new DataSourceConnectionSource(dataSource, databaseUrl);
        this.dao = DaoManager.createDao(connectionSource, Store.class);
    }

    public Dao<Store, Integer> getDao() { return dao; }

    public List<Store> listAll() throws SQLException {
        return dao.queryForAll();
    }

    public Store findById(int id) throws SQLException {
        return dao.queryForId(id);
    }

    public void create(Store s) throws SQLException {
        dao.create(s);
    }
} 