package com.lab5.reporting.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.lab5.reporting.domain.Produit;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProduitDao {
    private final Dao<Produit, Integer> produitDao;
    public ProduitDao(DataSource dataSource) throws SQLException {
        String databaseUrl = "jdbc:postgresql://reporting-db:5432/reporting_db";
        ConnectionSource connectionSource = new DataSourceConnectionSource(dataSource, databaseUrl);
        produitDao = DaoManager.createDao(connectionSource, Produit.class);
    }
    public Dao<Produit, Integer> getDao() { return produitDao; }
    public void ajouterProduit(Produit p) throws SQLException { produitDao.createIfNotExists(p); }
    public List<Produit> rechercherParNom(String nom) throws SQLException {
        return produitDao.queryBuilder().where().like("nom", "%" + nom + "%").query();
    }
    public List<Produit> rechercherParCategorie(String cat) throws SQLException {
        return produitDao.queryBuilder().where().like("categorie", "%" + cat + "%").query();
    }
    public Produit rechercherParId(int id) throws SQLException { return produitDao.queryForId(id); }
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
    public List<Produit> getInventaire() throws SQLException { return produitDao.queryForAll(); }
} 