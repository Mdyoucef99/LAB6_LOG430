package com.lab5.inventory.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.inventory.domain.Produit;
import com.lab5.inventory.domain.Stock;
import com.lab5.inventory.domain.Store;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StockDao {
    private final Dao<Stock, Void> dao;

    public StockDao(DataSource dataSource) throws SQLException {
        String databaseUrl = "jdbc:postgresql://inventory-db:5432/inventory_db";
        ConnectionSource connectionSource = new DataSourceConnectionSource(dataSource, databaseUrl);
        this.dao = DaoManager.createDao(connectionSource, Stock.class);
    }

    public Dao<Stock, Void> getDao() { return dao; }

    public Stock getStock(Store store, Produit p) throws SQLException {
        QueryBuilder<Stock, Void> qb = dao.queryBuilder();
        qb.where()
          .eq("store_id", store.getId())
          .and()
          .eq("product_id", p.getId());
        return dao.queryForFirst(qb.prepare());
    }

    public void updateQuantity(Store store, Produit p, int delta) throws SQLException {
        Stock stock = getStock(store, p);
        if (stock == null) {
            dao.create(new Stock(store, p, delta));
        } else {
            stock.setQuantity(stock.getQuantity() + delta);
            dao.update(stock);
        }
    }

    public List<Stock> listByStore(Store store) throws SQLException {
        return dao.queryForEq("store_id", store.getId());
    }

    public List<Stock> getAll() throws SQLException {
        return dao.queryForAll();
    }
} 