# LAB6 – Architecture Microservices avec Saga Orchestration : Système de gestion de magasin avec transactions distribuées

## Description

Ce projet étend l'architecture microservices du LAB5 en implémentant le pattern **Saga Orchestration** pour gérer les transactions distribuées. Il utilise Spring Boot, PostgreSQL (Database-per-Service), Docker Compose, Spring Cloud Gateway et un service orchestrateur dédié pour garantir la cohérence des données à travers les microservices.

---

IP machine virtuelle : log430@log430-etudiante-59

**1. Lancer l'architecture microservices avec Saga Orchestration**
```sh
docker-compose -f docker-compose.microservices.yml build
docker-compose -f docker-compose.microservices.yml up -d
```
Cela démarre tous les services :
- **Bases de données PostgreSQL** (7 instances distinctes)
  - product-db (port 5431)
  - inventory-db (port 5432)
  - reporting-db (port 5433)
  - customer-db (port 5434)
  - cart-db (port 5435)
  - order-db (port 5437)
- **Microservices**
  - product-service (port 8081)
  - inventory-service (port 8082)
  - reporting-service (port 8083)
  - customer-service (port 8084)
  - cart-service (port 8085)
  - order-service (port 8086)
  - **saga-orchestrator** (port 8087) - **NOUVEAU**
- **Infrastructure**
  - api-gateway (port 8080)
  - prometheus (port 9090)
  - grafana (port 3000)

---

## Tester les endpoints des microservices (sans API Gateway)

Vous pouvez accéder à chaque service directement via son port mappé :

- **product-service**  
  - Liste des produits :  
    `curl http://localhost:8081/api/v1/products`
  - Détail d'un produit :  
    `curl http://localhost:8081/api/v1/products/1`

- **inventory-service**  
  - Stock d'un magasin :  
    `curl http://localhost:8082/api/v1/stores/1/stock`
  - **Réservation de stock** (pour saga) :  
    `curl -X POST http://localhost:8082/api/v1/stores/1/stock/reserve -H "Content-Type: application/json" -d '[{"productId": 1, "quantity": 2}]'`
  - **Libération de stock** (compensation) :  
    `curl -X POST http://localhost:8082/api/v1/stores/1/stock/release -H "Content-Type: application/json" -d '[{"productId": 1, "quantity": 2}]'`

- **reporting-service**  
  - Rapport de ventes :  
    `curl http://localhost:8083/api/v1/reports/sales`

- **customer-service**  
  - Liste des clients :  
    `curl http://localhost:8084/api/v1/customers`

- **cart-service**  
  - Liste des paniers :  
    `curl http://localhost:8085/api/v1/carts`
  - **Validation de panier** (pour saga) :  
    `curl -X POST http://localhost:8085/api/v1/carts/1/validate`
  - **Nettoyage de panier** (pour saga) :  
    `curl -X DELETE http://localhost:8085/api/v1/carts/1/clear`
  - Ajouter un article au panier :  
    `curl -X POST http://localhost:8085/api/v1/carts/1/items -H "Content-Type: application/json" -d '{"productId": 1, "quantity": 2}'`

- **order-service**  
  - Liste des commandes :  
    `curl http://localhost:8086/api/v1/orders`
  - **Création de commande** (pour saga) :  
    `curl -X POST http://localhost:8086/api/v1/orders/checkout -H "Content-Type: application/json" -d '{"customerId": 1, "totalAmount": 5.0}'`

- **saga-orchestrator** (NOUVEAU)  
  - Test de la saga :  
    `curl http://localhost:8087/api/v1/sagas/test`
  - Statut du service :  
    `curl http://localhost:8087/api/v1/sagas/status`
  - Santé du service :  
    `curl http://localhost:8087/api/v1/sagas/health`

---

## Tester la Saga Orchestration

### **1. Test de Saga Réussie**
```sh
# Préparer un panier avec des articles
curl -X POST http://localhost:8085/api/v1/carts/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'

# Exécuter la saga de commande
curl -X POST http://localhost:8087/api/v1/sagas/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "cartId": 1,
    "items": [{"productId": 1, "quantity": 2}],
    "totalAmount": 5.0
  }'
```

### **2. Test de Saga avec Échec (Compensation)**
```sh
# Test avec quantité > 10 (déclenche l'échec)
curl -X POST http://localhost:8087/api/v1/sagas/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "cartId": 1,
    "items": [{"productId": 1, "quantity": 15}],
    "totalAmount": 25.0
  }'
```

### **3. Test de Saga avec Panier Vide**
```sh
# Test avec panier vide (échec précoce)
curl -X POST http://localhost:8087/api/v1/sagas/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "cartId": 1,
    "items": [{"productId": 1, "quantity": 2}],
    "totalAmount": 5.0
  }'
```

---

## Tester les endpoints via l'API Gateway

Tous les services sont exposés via le gateway sur le port 8080 :

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

- **saga-orchestrator** (via gateway)  
  - `curl http://localhost:8080/api/v1/sagas/test`
  - `curl http://localhost:8080/api/v1/sagas/status`

---

## Monitoring et Observabilité

### **Prometheus**
- Interface web : `http://localhost:9090`
- Métriques saga : 
  - `curl "http://localhost:9090/api/v1/query?query=saga_started_total"`
  - `curl "http://localhost:9090/api/v1/query?query=saga_completed_total"`
  - `curl "http://localhost:9090/api/v1/query?query=saga_failed_total"`

### **Grafana**
- Interface web : `http://localhost:3000`
- Identifiants : `admin/admin`
- Dashboard : "Saga Metrics Dashboard"
---

## Arrêter l'architecture

```sh
docker-compose -f docker-compose.microservices.yml down -v
```
L'option `-v` supprime aussi les volumes (bases de données).

---

**Remarques :**
- Les endpoints `/api/v1/...` sont accessibles directement sur chaque service ou via le gateway.
- Le saga orchestrator est accessible sur le port 8087 ou via le gateway.
- Chaque service a sa propre base de données PostgreSQL (Database-per-Service pattern).
- Les métriques saga sont automatiquement collectées par Prometheus.
- La compensation est automatique pour les échecs après réservation de stock. 