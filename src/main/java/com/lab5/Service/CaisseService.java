package com.lab5.Service;
import java.util.Scanner;

import com.lab5.Model.Produit;
import com.lab5.Model.Stock;
import com.lab5.Model.Store;
import com.lab5.dao.*;

import java.sql.SQLException;
import java.util.List;


public class CaisseService implements Runnable {
    private final Store store;
    private final ProduitDao produitDao;
    private final StockDao stockDao;
    private final SaleDao saleDao;

    public CaisseService(Store store, ProduitDao produitDao, StockDao stockDao, SaleDao saleDao) {
        this.store = store;
        this.produitDao = produitDao;
        this.stockDao = stockDao;
        this.saleDao = saleDao;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("--- CAISSE (" + store.getName() + ") ---");
            System.out.println("1. Rechercher produit");
            System.out.println("2. Enregistrer vente");
            System.out.println("3. Annuler vente");
            System.out.println("4. Consulter inventaire");
            System.out.println("0. Quitter");
            System.out.print("Choix: ");

            String input = sc.nextLine();
    try {
             switch (input) {

             case "1":

            System.out.print("ID produit: ");
            int pidLookup = Integer.parseInt(sc.nextLine());
            Produit pLookup = produitDao.rechercherParId(pidLookup);

            if (pLookup == null) {
                System.out.println("Produit introuvable.");
            } else {

                Stock stockLookup = stockDao.getStock(store, pLookup);
                int available = (stockLookup != null) ? stockLookup.getQuantity() : 0;

                System.out.printf(
                    "Produit %d - %s : %d en stock dans %s%n",
                    pLookup.getId(),
                    pLookup.getNom(),
                    available,
                    store.getName()
                );
            }
            break;

            case "2":
            System.out.print("ID produit: ");
            int pid = Integer.parseInt(sc.nextLine());
            System.out.print("Quantité vendue: ");
            int qty = Integer.parseInt(sc.nextLine());

            Produit p = produitDao.rechercherParId(pid);
            Stock s = stockDao.getStock(store, p);

            if (s == null || s.getQuantity() < qty) {
                System.out.println("Stock insuffisant pour la vente.");
            } else {
                stockDao.updateQuantity(store, p, -qty);
                saleDao.recordSale(store, p, qty);
                System.out.println("Vente enregistrée.");
            }
            break;

            case "3":
            System.out.print("ID produit: ");
            int pidR = Integer.parseInt(sc.nextLine());
            System.out.print("Quantité à retourner: ");
            int qtyR = Integer.parseInt(sc.nextLine());

            Produit pR = produitDao.rechercherParId(pidR);
            stockDao.updateQuantity(store, pR, +qtyR);
            System.out.println("Retour enregistré.");
            break;


            case "4":
            List<Stock> inv = stockDao.listByStore(store);
            inv.forEach(stock -> System.out.printf(
                "%d - %s : %d en stock%n",
                stock.getProduit().getId(),
                stock.getProduit().getNom(),
                stock.getQuantity()
            ));
            break;

        case "0":
            return;

        default:
            System.out.println("Choix invalide.");
            break;
    }
} catch (SQLException e) {
                System.out.println("Erreur base de données: " + e.getMessage());
            }
        }
    }
}