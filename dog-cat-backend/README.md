# DogCat — API Spring Boot

Le backend fournit l’API REST de DogCat et exécute le modèle de classification. Il utilise Java 21, Spring Boot 4, Spring Data JPA, PostgreSQL et ONNX Runtime.

## Responsabilités

- Réception des images par requête multipart.
- Stockage des fichiers dans `uploads/`, relatif au répertoire de lancement.
- Persistance des métadonnées et des résultats dans PostgreSQL.
- Inférence locale à partir du modèle ONNX.
- Exposition des images et de leur état au frontend.

## API REST

L’API est exposée sur `http://localhost:8080`.

| Méthode | Route | Description |
| --- | --- | --- |
| POST | `/images` | Import multipart, champ `file` ; retourne l’UUID |
| GET | `/images` | Liste des images et de leurs métadonnées |
| GET | `/images/{id}` | Métadonnées d’une image |
| GET | `/images/{id}/file` | Contenu du fichier image |
| POST | `/images/{id}/predict` | Évaluation synchrone et persistance du résultat |

Les requêtes du frontend sont autorisées depuis `http://localhost:4200`. Après une évaluation, le frontend recharge les métadonnées pour afficher le résultat.

## Données et cycle de vie

Une image est représentée par son identifiant UUID, son chemin de fichier, son statut, son label prédit, sa confiance et sa date de création.

| Statut | Signification |
| --- | --- |
| `PENDING` | Image importée, en attente de résultat |
| `DONE` | Évaluation terminée |
| `ERROR` | Échec de l’évaluation |

Le service autorise une nouvelle évaluation d’une image existante. Un échec du modèle est enregistré avec le statut `ERROR` ; le statut retourné par la liste des images détermine donc l’affichage du résultat.

## Inférence

Les ressources chargées au démarrage se trouvent dans `src/main/resources/ml/` :

- `model_v0.onnx` : réseau de classification.
- `labels.json` : ordre des classes.
- `preprocess.json` : paramètres de prétraitement.
- `threshold.json` : seuil de confiance.

Le service prépare une image RGB de 224 × 224 pixels, normalise ses canaux, exécute le modèle et applique un softmax. Une confiance inférieure au seuil conduit au label `other`.

## Configuration et lancement

La connexion cible `jdbc:postgresql://localhost:5432/dogcat` et utilise les variables `DB_USER` et `DB_PASSWORD`. Le fichier `.env` utilisé par Compose n’est pas chargé automatiquement par Java.

Les [instructions PostgreSQL](../docker/postgres/README.md) décrivent le démarrage de la base et le chargement des variables dans le terminal ou dans VS Code. Une fois les variables disponibles, le lancement depuis `dog-cat-backend/` est :

```powershell
./mvnw.cmd spring-boot:run
```

L’import facultatif d’`application-local.yaml` permet des surcharges locales. Ce fichier est ignoré par Git. Hibernate met à jour le schéma au démarrage via `ddl-auto: update`.
