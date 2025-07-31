package com.lab5.Controller;


import java.sql.SQLException;
import java.util.List;

import com.lab5.Model.SaleReport;
import com.lab5.Model.Stock;
import com.lab5.Model.Store;
import com.lab5.dao.*;


public class ReportController {
    private final SaleDao    saleDao;
    private final StoreDao   storeDao;
    private final ProduitDao produitDao;
    private final StockDao   stockDao;

    public ReportController(SaleDao saleDao,
                            StoreDao storeDao,
                            ProduitDao produitDao,
                            StockDao stockDao) {
        this.saleDao    = saleDao;
        this.storeDao   = storeDao;
        this.produitDao = produitDao;
        this.stockDao   = stockDao;
    }

    public void printConsolidatedReport() {
        try {

            // 1) Détail des ventes par magasin et produit
            List<SaleReport> sales = saleDao.consolidatedReport(storeDao, produitDao);
            System.out.println("\n=== Ventes détaillées par magasin ===");
            for (SaleReport r : sales) {
                System.out.printf("%s | %s | Qté vendue : %d%n",
                                  r.getStoreName(),
                                  r.getProductName(),
                                  r.getTotalQty());
            }

            // 2) Stocks restants par magasin
            System.out.println("\n=== Stocks restants par magasin ===");
            for (Store s : storeDao.listAll()) {
                System.out.printf("Magasin %s :%n", s.getName());
                List<Stock> stocks = stockDao.listByStore(s);
                for (Stock st : stocks) {
                    System.out.printf("  %s : %d en stock%n",
                                      st.getProduit().getNom(),
                                      st.getQuantity());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur UC1: " + e.getMessage());
        }
    }

}
