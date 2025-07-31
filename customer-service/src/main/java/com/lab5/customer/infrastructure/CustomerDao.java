package com.lab5.customer.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.customer.domain.Customer;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomerDao {
    private final Dao<Customer, Integer> customerDao;
    public CustomerDao(DataSource dataSource) throws SQLException {
        String databaseUrl = "jdbc:postgresql://customer-db:5432/customer_db";
        ConnectionSource connectionSource = new DataSourceConnectionSource(dataSource, databaseUrl);
        customerDao = DaoManager.createDao(connectionSource, Customer.class);
    }
    public Dao<Customer, Integer> getDao() { return customerDao; }
    public void addCustomer(Customer c) throws SQLException { customerDao.createIfNotExists(c); }
    public List<Customer> getAll() throws SQLException { return customerDao.queryForAll(); }
} 