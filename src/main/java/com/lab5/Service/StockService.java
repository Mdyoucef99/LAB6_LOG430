package com.lab5.Service;

import java.sql.SQLException;

import com.lab5.Model.Produit;
import com.lab5.Model.Store;
import com.lab5.dao.*;

public class StockService {
    private final StockDao stockDao;
    public StockService(StockDao stockDao) {
        this.stockDao = stockDao;
    }
    public void reorder(Store central, Store store, Produit p, int quantity) throws SQLException {
        stockDao.updateQuantity(central, p, -quantity);
        stockDao.updateQuantity(store, p, quantity);
    }
    
}