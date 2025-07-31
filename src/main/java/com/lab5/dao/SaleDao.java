package com.lab5.dao;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.Model.Produit;
import com.lab5.Model.Sale;
import com.lab5.Model.SaleReport;
import com.lab5.Model.Store;

public class SaleDao {
       private final Dao<Sale,Integer> dao;

    public SaleDao(ConnectionSource cs) throws SQLException {
        this.dao = DaoManager.createDao(cs, Sale.class);
    }

      public Dao<Sale,Integer> getDao() {
        return dao;
    }

    public void recordSale(Store store, Produit p, int q) throws SQLException {
        dao.create(new Sale(store, p, q));
    }

    public List<SaleReport> consolidatedReport(StoreDao storeDao, ProduitDao prodDao) throws SQLException {
        QueryBuilder<Sale,Integer> qb = dao.queryBuilder();
        qb.selectRaw("store_id","product_id","SUM(quantity) as total");
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
