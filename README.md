# DogCat — Classification d’images

DogCat est une application de classification d’images associant une interface Angular, une API Java et un modèle de vision exécuté localement. Les images importées sont classées comme `cat`, `dog` ou `other`, avec un score de confiance.

Ce projet de démonstration couvre la préparation des données, l’entraînement du modèle et son intégration dans une application web. L’inférence repose sur ONNX Runtime dans le backend, sans appel à une API d’intelligence artificielle externe.

## Fonctionnalités

- Import de photos avec aperçu avant envoi.
- Galerie avec statut, résultat et score de confiance.
- Évaluation à la demande, réévaluation et nouvelle tentative après erreur.
- Stockage des fichiers sur disque et des métadonnées dans PostgreSQL.

## Architecture

Le frontend transmet les images à l’API Spring Boot. Le backend conserve les fichiers dans `uploads/` et enregistre leurs métadonnées en base. Une demande d’évaluation déclenche l’inférence ONNX ; le résultat est persisté, puis affiché dans la galerie.

| Composant | Technologies | Documentation |
| --- | --- | --- |
| Interface | Angular 21, TypeScript, signals, RxJS | [Frontend](dog-cat-frontend/README.md) |
| API | Java 21, Spring Boot 4, JPA, ONNX Runtime | [Backend](dog-cat-backend/README.md) |
| Base de données | PostgreSQL 17, Docker Compose | [PostgreSQL](docker/postgres/README.md) |
| Machine learning | PyTorch, torchvision, ONNX | [Modèle et données](ml/README.md) |

## Exécution locale

L’application utilise PostgreSQL sur `localhost:5432`, l’API sur `http://localhost:8080` et le frontend sur `http://localhost:4200`. La racine du dépôt désigne le dossier contenant `compose.yaml`.

Les prérequis sont Docker Compose avec des conteneurs Linux, Java 21 et une version de Node.js compatible avec Angular 21. Maven est fourni par le wrapper du backend.

Le démarrage suit cet ordre : configuration des variables `DB_USER` et `DB_PASSWORD`, lancement de PostgreSQL, lancement de l’API, puis du frontend. Les commandes sont détaillées dans les README de chaque composant.

Les identifiants locaux sont conservés dans un fichier `.env` ignoré par Git. Docker Compose le charge automatiquement ; le lancement Java nécessite leur transmission par le terminal ou par la configuration de l’IDE.

## Modèle de classification

Le modèle utilise ResNet18 préentraîné sur ImageNet, dont la couche finale est adaptée aux classes `cat`, `dog` et `other`. Le backend applique un softmax aux sorties et renvoie `other` lorsque la confiance maximale est inférieure au seuil configuré.

Les ressources d’inférence sont regroupées dans `dog-cat-backend/src/main/resources/ml/` : `model_v0.onnx`, `labels.json`, `preprocess.json` et `threshold.json`. Les données d’entraînement et les checkpoints ne font pas partie du dépôt.
