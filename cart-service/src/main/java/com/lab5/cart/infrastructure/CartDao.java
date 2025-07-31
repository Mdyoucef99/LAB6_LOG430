package com.lab5.cart.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.cart.domain.Cart;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CartDao {
    private final Dao<Cart, Integer> cartDao;

    public CartDao(DataSource dataSource) throws SQLException {
        String databaseUrl = "jdbc:postgresql://cart-db:5432/cart_db";
        ConnectionSource connectionSource = new DataSourceConnectionSource(dataSource, databaseUrl);
        cartDao = DaoManager.createDao(connectionSource, Cart.class);
    }

    public Dao<Cart, Integer> getDao() { return cartDao; }

    public void create(Cart cart) throws SQLException { cartDao.create(cart); }
    public Cart findById(int id) throws SQLException { return cartDao.queryForId(id); }
    public List<Cart> findAll() throws SQLException { return cartDao.queryForAll(); }
    public void update(Cart cart) throws SQLException { cartDao.update(cart); }
    public void delete(Cart cart) throws SQLException { cartDao.delete(cart); }
} 