package com.lab5.order.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.order.domain.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderDao {
    
    private final Dao<Order, Integer> orderDao;
    private final ConnectionSource connectionSource;

    public OrderDao(@Value("${spring.datasource.url}") String dbUrl,
                   @Value("${spring.datasource.username}") String dbUsername,
                   @Value("${spring.datasource.password}") String dbPassword) throws SQLException {
        this.connectionSource = new JdbcConnectionSource(dbUrl, dbUsername, dbPassword);
        this.orderDao = DaoManager.createDao(connectionSource, Order.class);
    }

    public List<Order> findAll() throws SQLException {
        return orderDao.queryForAll();
    }

    public Order findById(int id) throws SQLException {
        return orderDao.queryForId(id);
    }

    public void create(Order order) throws SQLException {
        orderDao.create(order);
    }
} 