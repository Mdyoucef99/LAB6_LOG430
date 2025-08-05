 # LAB7 - Architecture Événementielle avec Saga Chorégraphiée

## Description

Ce projet étend l'architecture microservices du LAB6 en implémentant le pattern **Saga Chorégraphiée** pour gérer les transactions distribuées de manière décentralisée. Il utilise Spring Boot, PostgreSQL (Database-per-Service), Docker Compose, Spring Cloud Gateway, RabbitMQ pour la communication événementielle, et un Event Store pour l'audit trail complet.

**Nouveautés LAB7 :**
- Event Store Service : Stockage et relecture de tous les événements
- Notification Service : Gestion des notifications événementielles
- RabbitMQ : Message broker pour la communication asynchrone
- Architecture événementielle : Pattern Pub/Sub avec compensation automatique

---

## Architecture

### Services Microservices
- **product-service** (port 8081) : Gestion des produits
- **inventory-service** (port 8082) : Gestion des stocks avec saga handler
- **reporting-service** (port 8083) : Rapports et analytics
- **customer-service** (port 8084) : Gestion des clients
- **cart-service** (port 8085) : Gestion des paniers avec saga handler
- **order-service** (port 8086) : Gestion des commandes avec saga handler
- **saga-orchestrator** (port 8087) : Saga orchestrée (LAB6)
- **event-store-service** (port 8089) : NOUVEAU - Stockage d'événements
- **notification-service** (port 8088) : NOUVEAU - Notifications

### Infrastructure
- **api-gateway** (port 8080) : Point d'entrée unifié
- **rabbitmq** (port 5672) : NOUVEAU - Message broker
- **prometheus** (port 9090) : Métriques et monitoring
- **grafana** (port 3000) : Dashboards de monitoring

### Bases de Données PostgreSQL
- product-db (port 5434)
- inventory-db (port 5436)
- reporting-db (port 5435)
- customer-db (port 5438)
- cart-db (port 5439)
- order-db (port 5437)
- event-store-db (port 5440) : NOUVEAU

---

## Démarrage Rapide

### 1. Lancer l'architecture complète
```bash
docker-compose -f docker-compose.microservices.yml build
docker-compose -f docker-compose.microservices.yml up -d
```

### 2. Vérifier que tous les services sont démarrés
```bash
docker-compose -f docker-compose.microservices.yml ps
```

### 3. Accéder aux interfaces
- **API Gateway** : http://localhost:8080
- **RabbitMQ Management** : http://localhost:15672 (admin/admin)
- **Prometheus** : http://localhost:9090
- **Grafana** : http://localhost:3000 (admin/admin)

---

## Saga Chorégraphiée

### Concept
La saga chorégraphiée coordonne le processus de commande via des événements publiés par chaque service, sans orchestrateur central. Chaque service écoute les événements pertinents et publie ses propres événements.

### Flux de la Saga
1. **Order Service** → Publie `OrderStartedEvent`
2. **Cart Service** → Écoute `OrderStartedEvent` → Publie `CartValidatedEvent`
3. **Inventory Service** → Écoute `CartValidatedEvent` → Publie `StockReservedEvent`
4. **Order Service** → Écoute `StockReservedEvent` → Publie `OrderCreatedEvent`
5. **Cart Service** → Écoute `OrderCreatedEvent` → Publie `CartClearedEvent`

### Gestion des Échecs
- **Échec de validation** : `CartValidationFailedEvent` → Arrêt de la saga
- **Échec de réservation** : `StockReservationFailedEvent` → Compensation automatique
- **Échec de création** : Compensation automatique du stock réservé

---

## Tests de la Saga Chorégraphiée

### 1. Préparation des Données
```bash
# Créer un produit
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Gaming",
    "price": 1299.99,
    "category": "Electronics"
  }'

# Créer un client
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jean Dupont",
    "email": "jean.dupont@email.com"
  }'

# Créer un panier
curl -X POST http://localhost:8080/api/v1/carts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1
  }'

# Ajouter un article au panier
curl -X POST http://localhost:8080/api/v1/carts/1/items \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 1
  }'
```

### 2. Test de Succès de la Saga
```bash
# Démarrer la saga chorégraphiée
curl -X POST http://localhost:8080/api/v1/orders/start-saga \
  -H "Content-Type: application/json" \
  -d '{
    "cartId": 1
  }'

# Vérifier le résultat
curl -X GET http://localhost:8080/api/v1/orders
curl -X GET http://localhost:8080/api/v1/carts/1
```

### 3. Test d'Échec (Compensation)
```bash
# Créer un nouveau panier pour test d'échec
curl -X POST http://localhost:8080/api/v1/carts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1
  }'

# Ajouter une quantité excessive (déclenche l'échec)
curl -X POST http://localhost:8080/api/v1/carts/2/items \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 15
  }'

# Démarrer la saga (va échouer)
curl -X POST http://localhost:8080/api/v1/orders/start-saga \
  -H "Content-Type: application/json" \
  -d '{
    "cartId": 2
  }'
```

### 4. Test avec Panier Inexistant
```bash
curl -X POST http://localhost:8080/api/v1/orders/start-saga \
  -H "Content-Type: application/json" \
  -d '{
    "cartId": 999
  }'
```

---

## Event Store - Consultation des Événements

### Consulter tous les événements
```bash
curl -X GET http://localhost:8089/api/v1/eventstore/events
```

### Consulter les événements par agrégat
```bash
# Événements par agrégat
curl -X GET http://localhost:8089/api/v1/eventstore/events/aggregate/cart-1
curl -X GET http://localhost:8089/api/v1/eventstore/events/aggregate/order-1
curl -X GET http://localhost:8089/api/v1/eventstore/events/aggregate/inventory-1
```

### Relecture d'événements
```bash
# Relecture depuis une version
curl -X GET http://localhost:8089/api/v1/eventstore/events/replay/cart-1?fromVersion=1

# Consulter l'état courant d'un objet
curl -X GET http://localhost:8089/api/v1/eventstore/current-state/cart/1
```

---

## Endpoints des Services

### Via API Gateway (port 8080)
```bash
# Products
curl http://localhost:8080/api/v1/products
curl http://localhost:8080/api/v1/products/1

# Inventory
curl http://localhost:8080/api/v1/stores/1/stock

# Customers
curl http://localhost:8080/api/v1/customers

# Carts
curl http://localhost:8080/api/v1/carts
curl http://localhost:8080/api/v1/carts/1

# Orders
curl http://localhost:8080/api/v1/orders

# Saga Orchestrator (LAB6)
curl http://localhost:8080/api/v1/sagas/test
curl http://localhost:8080/api/v1/sagas/status

# Event Store (LAB7)
curl http://localhost:8080/api/v1/eventstore/events

# Notifications (LAB7)
curl http://localhost:8080/api/v1/notifications
```

### Accès Direct aux Services
```bash
# Product Service
curl http://localhost:8081/api/v1/products

# Inventory Service
curl http://localhost:8082/api/v1/stores/1/stock

# Customer Service
curl http://localhost:8084/api/v1/customers

# Cart Service
curl http://localhost:8085/api/v1/carts

# Order Service
curl http://localhost:8086/api/v1/orders

# Event Store Service
curl http://localhost:8089/api/v1/eventstore/events

# Notification Service
curl http://localhost:8088/api/v1/notifications
```

---

## Monitoring et Observabilité

### RabbitMQ Management
- **Interface web** : http://localhost:15672
- **Identifiants** : admin/admin
- **Queues** : cart.queue, inventory.queue, order.queue, event.store.queue
- **Exchange** : saga.events

### Prometheus
- **Interface web** : http://localhost:9090
- **Métriques saga** :
  ```bash
  curl "http://localhost:9090/api/v1/query?query=saga_started_total"
  curl "http://localhost:9090/api/v1/query?query=saga_completed_total"
  curl "http://localhost:9090/api/v1/query?query=saga_failed_total"
  ```

### Grafana
- **Interface web** : http://localhost:3000
- **Identifiants** : admin/admin
- **Dashboards disponibles** :
  - Saga Metrics Dashboard
  - Event Store Metrics Dashboard
  - Load Balancer Comparison

---

## Comparaison : Saga Orchestrée vs Chorégraphiée

### Saga Orchestrée (LAB6)
- **Orchestrateur central** : saga-orchestrator
- **Communication** : REST synchrone
- **Contrôle** : Centralisé
- **Complexité** : Modérée
- **Debugging** : Facile

### Saga Chorégraphiée (LAB7)
- **Orchestrateur** : Aucun (décentralisé)
- **Communication** : Événements asynchrones (RabbitMQ)
- **Contrôle** : Distribué
- **Complexité** : Élevée
- **Debugging** : Complexe (via Event Store)

---

## Arrêt de l'Architecture

```bash
# Arrêter tous les services
docker-compose -f docker-compose.microservices.yml down

# Arrêter et supprimer les volumes (bases de données)
docker-compose -f docker-compose.microservices.yml down -v
```


## Remarques Importantes

- **Ports** : Tous les services sont accessibles via l'API Gateway (port 8080) ou directement
- **Bases de données** : Chaque service a sa propre base PostgreSQL (Database-per-Service)
- **Événements** : Tous les événements sont stockés dans l'Event Store pour audit
- **Compensation** : Gestion automatique des échecs avec compensation
- **Monitoring** : Métriques automatiques collectées par Prometheus
- **Documentation** : Voir `rapport_labo7.md` pour les détails techniques

---

**Note** : Ce projet démontre l'évolution d'une architecture microservices de la saga orchestrée (LAB6) vers la saga chorégraphiée (LAB7), illustrant les différents patterns de gestion des transactions distribuées. 