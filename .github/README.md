
#LAB5 – Architecture Microservices : Système de gestion de magasin scalable et flexible

## Description

Ce projet met en œuvre une architecture microservices pour un système de gestion de magasin, utilisant Spring Boot, PostgreSQL, Docker Compose et Spring Cloud Gateway comme API Gateway.

---

IP machine virtuelle : log430@log430-etudiante-59

**1. Lancer l’architecture microservices avec Docker Compose**
```sh
docker-compose -f docker-compose.microservices.yml build
docker-compose -f docker-compose.microservices.yml up -d
```
Cela démarre tous les services :
- PostgreSQL (db)
- product-service (port 8081)
- inventory-service (port 8082)
- reporting-service (port 8083)
- customer-service (port 8084)
- cart-service (port 8085)
- order-service (port 8086)
- api-gateway (port 8080)

---

## Tester les endpoints des microservices (sans API Gateway)

Vous pouvez accéder à chaque service directement via son port mappé :

- **product-service**  
  - Liste des produits :  
    `curl http://localhost:8081/api/v1/products`
  - Détail d’un produit :  
    `curl http://localhost:8081/api/v1/products/1`

- **inventory-service**  
  - Stock d’un magasin :  
    `curl http://localhost:8082/api/v1/stores/1/stock`

- **reporting-service**  
  - Rapport de ventes :  
    `curl http://localhost:8083/api/v1/reports/sales`

- **customer-service**  
  - Liste des clients :  
    `curl http://localhost:8084/api/v1/customers`

- **cart-service**  
  - Liste des paniers :  
    `curl http://localhost:8085/api/v1/carts`

- **order-service**  
  - Liste des commandes :  
    `curl http://localhost:8086/api/v1/orders`

---

## Tester les endpoints via l’API Gateway

Tous les services sont exposés via le gateway sur le port 8080. Les routes sont automatiquement redirigées :

- **product-service**  
  - `curl http://localhost:8080/api/v1/products`
  - `curl http://localhost:8080/api/v1/products/1`

- **inventory-service**  
  - `curl http://localhost:8080/api/v1/stores/1/stock`

- **reporting-service**  
  - `curl http://localhost:8080/api/v1/reports/sales`

- **customer-service**  
  - `curl http://localhost:8080/api/v1/customers`

- **cart-service**  
  - `curl http://localhost:8080/api/v1/carts`

- **order-service**  
  - `curl http://localhost:8080/api/v1/orders`

---

## Arrêter l’architecture

```sh
docker-compose -f docker-compose.microservices.yml down -v
```
L’option `-v` supprime aussi les volumes (base de données).

---

**Remarques :**
- Les endpoints `/api/v1/...` sont accessibles directement sur chaque service ou via le gateway.
- Pour ajouter des données, utilisez les méthodes POST appropriées (voir Swagger ou la documentation de chaque service).



