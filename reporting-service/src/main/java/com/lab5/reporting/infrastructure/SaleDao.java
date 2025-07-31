package com.lab5.reporting.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.reporting.domain.Produit;
import com.lab5.reporting.domain.Sale;
import com.lab5.reporting.domain.SaleReport;
import com.lab5.reporting.domain.Store;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SaleDao {
    private final Dao<Sale, Integer> dao;
    public SaleDao(DataSource dataSource) throws SQLException {
        String databaseUrl = "jdbc:postgresql://reporting-db:5432/reporting_db";
        ConnectionSource connectionSource = new DataSourceConnectionSource(dataSource, databaseUrl);
        this.dao = DaoManager.createDao(connectionSource, Sale.class);
    }
    public Dao<Sale, Integer> getDao() { return dao; }
    public void recordSale(Store store, Produit p, int q) throws SQLException {
        dao.create(new Sale(store, p, q));
    }
    public List<SaleReport> consolidatedReport(StoreDao storeDao, ProduitDao prodDao) throws SQLException {
        QueryBuilder<Sale, Integer> qb = dao.queryBuilder();
        qb.selectRaw("store_id", "product_id", "SUM(quantity) as total");
        qb.groupBy("store_id");
        qb.groupBy("product_id");
        GenericRawResults<String[]> raw = dao.queryRaw(qb.prepareStatementString());
        List<SaleReport> list = new ArrayList<>();
        for (String[] row : raw.getResults()) {
            Store s = storeDao.findById(Integer.parseInt(row[0]));
            Produit p = prodDao.rechercherParId(Integer.parseInt(row[1]));
            long total = Long.parseLong(row[2]);
            list.add(new SaleReport(s.getName(), p.getNom(), total));
        }
        return list;
    }
} 