package com.lab5.dao;


import java.sql.SQLException;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.Model.Produit;
import com.lab5.Model.Stock;
import com.lab5.Model.Store;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class StockDao {
      private final Dao<Stock,Void> dao;

    @Autowired
    public StockDao(ConnectionSource cs) throws SQLException {
        this.dao = DaoManager.createDao(cs, Stock.class);
    }

      public Dao<Stock,Void> getDao() {
        return dao;
    }

    public Stock getStock(Store store, Produit p) throws SQLException {
        QueryBuilder<Stock,Void> qb = dao.queryBuilder();
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