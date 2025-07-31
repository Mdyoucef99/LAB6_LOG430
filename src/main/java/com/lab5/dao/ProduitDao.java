package com.lab5.dao;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.Model.Produit;

import java.sql.SQLException;
import java.util.List;

public class ProduitDao {

    private final Dao<Produit, Integer> produitDao;

    public ProduitDao(ConnectionSource connectionSource) throws SQLException {
        produitDao = DaoManager.createDao(connectionSource, Produit.class);
    }

      public Dao<Produit, Integer> getDao() {
        return produitDao;
    }

    public void ajouterProduit(Produit p) throws SQLException {
        produitDao.createIfNotExists(p);
    }

    public List<Produit> rechercherParNom(String nom) throws SQLException {
        return produitDao.queryBuilder().where().like("nom", "%" + nom + "%").query();
    }


    public List<Produit> rechercherParCategorie(String cat) throws SQLException {
        return produitDao.queryBuilder().where().like("categorie", "%" + cat + "%").query();
    }

    public Produit rechercherParId(int id) throws SQLException {
        return produitDao.queryForId(id);
    }

    public void enregistrerVente(int id, int quantiteVendue) throws SQLException {
        Produit p = produitDao.queryForId(id);
        if (p != null && p.getQuantite() >= quantiteVendue) {
            p.setQuantite(p.getQuantite() - quantiteVendue);
            produitDao.update(p);
        } else {
            System.out.println("Produit non disponible ou stock insuffisant.");
        }
    }

    public void annulerVente(int id, int quantite) throws SQLException {
        Produit p = produitDao.queryForId(id);
        if (p != null) {
            p.setQuantite(p.getQuantite() + quantite);
            produitDao.update(p);
        }
    }

    public List<Produit> getInventaire() throws SQLException {
        return produitDao.queryForAll();
    }

}