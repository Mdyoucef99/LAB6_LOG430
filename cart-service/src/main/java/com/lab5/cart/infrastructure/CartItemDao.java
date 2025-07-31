package com.lab5.cart.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.cart.domain.CartItem;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CartItemDao {
    private final Dao<CartItem, Integer> cartItemDao;

    public CartItemDao(DataSource dataSource) throws SQLException {
        String databaseUrl = "jdbc:postgresql://cart-db:5432/cart_db";
        ConnectionSource connectionSource = new DataSourceConnectionSource(dataSource, databaseUrl);
        cartItemDao = DaoManager.createDao(connectionSource, CartItem.class);
    }

    public Dao<CartItem, Integer> getDao() { return cartItemDao; }

    public void create(CartItem item) throws SQLException { cartItemDao.create(item); }
    public CartItem findById(int id) throws SQLException { return cartItemDao.queryForId(id); }
    public List<CartItem> findAll() throws SQLException { return cartItemDao.queryForAll(); }
    public void update(CartItem item) throws SQLException { cartItemDao.update(item); }
    public void delete(CartItem item) throws SQLException { cartItemDao.delete(item); }
    public List<CartItem> findByCartId(int cartId) throws SQLException {
        QueryBuilder<CartItem, Integer> qb = cartItemDao.queryBuilder();
        qb.where().eq("cartId", cartId);
        return cartItemDao.query(qb.prepare());
    }
} 