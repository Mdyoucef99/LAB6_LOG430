package com.lab5.View;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;  
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.Controller.DashboardController;
import com.lab5.Controller.ReportController;
import com.lab5.Controller.StockController;
import com.lab5.Model.Produit;
import com.lab5.Model.Store;
import com.lab5.Service.CaisseService;
import com.lab5.Service.StockService;
import com.lab5.dao.*;

import java.sql.Statement;



public class App {
 
     private static void filterDebug() {
        PrintStream origErr = System.err;
        System.setErr(new PrintStream(origErr) {
            @Override
            public void println(String s) {
                if (s != null && s.contains("[DEBUG]")) return;
                super.println(s);
            }
        });

        PrintStream origOut = System.out;
        System.setOut(new PrintStream(origOut) {
            @Override
            public void println(String s) {
                if (s != null && s.contains("[DEBUG]")) return;
                super.println(s);
            }
        });
    }

    public static void main(String[] args) {

        filterDebug();
        try {
           
            //get the environement we are using whether be it local or on docker 
            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "5432");
            String databaseUrl = "jdbc:postgresql://" + host + ":" + port + "/magasin";

            String user = "magasin_user";
            String password = "magasinpswd";

             // 2) Raw JDBC DDL: sequences + tables, IF NOT EXISTS
            try (Connection conn = DriverManager.getConnection(databaseUrl, user, password);
             Statement  st   = conn.createStatement()) {

            // sequences
            st.execute("CREATE SEQUENCE IF NOT EXISTS stores_id_seq;");
            st.execute("CREATE SEQUENCE IF NOT EXISTS produits_id_seq;");
            st.execute("CREATE SEQUENCE IF NOT EXISTS stocks_id_seq;");
            st.execute("CREATE SEQUENCE IF NOT EXISTS sales_id_seq;");

            // tables
            st.execute("""
                CREATE TABLE IF NOT EXISTS stores (
                  id integer PRIMARY KEY DEFAULT nextval('stores_id_seq'),
                  name varchar(255) NOT NULL
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS produits (
                  id integer PRIMARY KEY DEFAULT nextval('produits_id_seq'),
                  nom varchar(255),
                  categorie varchar(255),
                  prix double precision,
                  quantite integer
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS stocks (
                  id integer PRIMARY KEY DEFAULT nextval('stocks_id_seq'),
                  store_id integer NOT NULL REFERENCES stores(id),
                  product_id integer NOT NULL REFERENCES produits(id),
                  quantity integer NOT NULL
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS sales (
                  id integer PRIMARY KEY DEFAULT nextval('sales_id_seq'),
                  store_id integer NOT NULL REFERENCES stores(id),
                  product_id integer NOT NULL REFERENCES produits(id),
                  quantity integer NOT NULL,
                  saleDate timestamp NOT NULL
                )
            """);
        }
      
            // 3) ORMLite setup
            ConnectionSource cs = new JdbcConnectionSource(databaseUrl, user, password);
            StoreDao storeDao = new StoreDao(cs);
            ProduitDao produitDao = new ProduitDao(cs);
            StockDao stockDao = new StockDao(cs);
            SaleDao saleDao = new SaleDao(cs);

            if (storeDao.listAll().isEmpty()) {
                for (int i = 1; i <= 5; i++) {
                    storeDao.create(new Store("Magasin " + i));
                }
                storeDao.create(new Store("Logistique"));
                storeDao.create(new Store("Maison-Mère"));
            }

            if (produitDao.getInventaire().isEmpty()) {
                produitDao.ajouterProduit(new Produit(1, "Pain", "Nourriture", 2.5, 100));
                produitDao.ajouterProduit(new Produit(2, "Lait", "Nourriture", 1.8, 50));
                produitDao.ajouterProduit(new Produit(3, "Savon", "Hygiène", 3.2, 30));
            }

           for (Store s : storeDao.listAll()) {
             if (stockDao.listByStore(s).isEmpty()) {
                for (Produit p : produitDao.getInventaire()) {
                 stockDao.updateQuantity(s, p, p.getQuantite());
                }
            }}
             
            java.util.Scanner sc = new java.util.Scanner(System.in);

            while (true) {
                System.out.println("=== MENU PRINCIPAL ===");
                System.out.println("1. Caisse (magasin)");
                System.out.println("2. Centre logistique (UC2)");
                System.out.println("3. Maison-Mère (UC1 & UC3)");
                System.out.println("0. Quitter");
                System.out.print("Choix: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1: {
                        System.out.print("ID magasin (1-5):");
                        int mid = sc.nextInt();
                        sc.nextLine();
                        Store magasin = storeDao.findById(mid);
                        Thread caisseThread = new Thread(
                            new CaisseService(magasin, produitDao, stockDao, saleDao)
                        );
                        caisseThread.start();
                        caisseThread.join();
                        break;
                    }
                    case 2: {

                        System.out.print("Produit ID: ");
                        int pid = sc.nextInt();
                        System.out.print("Quantité: ");
                        int qty = sc.nextInt();
                        System.out.print("Magasin cible (1-5): ");
                        int tid = sc.nextInt();

                        new StockController(new StockService(stockDao), storeDao, produitDao).reorder(pid, qty, tid);
                        break;
                    }
                    case 3: {

                        new ReportController(saleDao, storeDao, produitDao, stockDao).printConsolidatedReport();
                        
                        new DashboardController(storeDao, produitDao, stockDao,saleDao).showDashboard();

                        break;
                    }
                    case 0:
                        System.out.println("Au revoir !");
                        System.exit(0);
                    default:
                        System.out.println("Choix invalide.");
                }
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}