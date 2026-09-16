# DogCat — Modèle et données

Le répertoire `ml/` rassemble les scripts de préparation des données, d’entraînement, d’évaluation et d’export du classificateur. Le modèle distingue les classes `cat`, `dog` et `other`.

## Organisation

| Répertoire | Rôle |
| --- | --- |
| `datasets/` | Contrôle et répartition des données |
| `train/` | Chargement, transformations, entraînement, métriques et calibration |
| `export/` | Export ONNX et validation de la correspondance avec PyTorch |
| `artifacts/` | Labels, prétraitement et seuil de confiance |

## Approche

Le réseau repose sur ResNet18 préentraîné sur ImageNet. Les paramètres du réseau initial sont gelés et sa couche finale est remplacée par une couche de classification à trois sorties. L’entraînement utilise PyTorch et le suivi de validation repose sur le score F1 macro.

L’export ONNX permet l’exécution dans le [backend Java](../dog-cat-backend/README.md). L’inférence applique un softmax aux sorties ; une confiance inférieure au seuil configuré conduit à la classe `other`, qui est également une classe d’entraînement.

Les paramètres de prétraitement décrivent des images RGB de 224 × 224 pixels et une normalisation ImageNet. Les ressources utilisées par l’application sont placées dans `dog-cat-backend/src/main/resources/ml/`.

## Sources de données

Les images d’entraînement ne sont pas versionnées. Les volumes ci-dessous correspondent aux ordres de grandeur documentés pour la constitution du jeu de données du projet.

### Chiens et chats

| Source | Volume approximatif |
| --- | --- |
| [Dogs vs Cats — Kaggle](https://www.kaggle.com/c/dogs-vs-cats) | 4 000 images |

### Classe `other`

Cette classe regroupe des images de personnes, d’animaux, de scènes, de véhicules et d’objets afin de diversifier les exemples.

| Source | Volume approximatif |
| --- | --- |
| [Human Images Dataset — Men and Women](https://www.kaggle.com/datasets/snmahsa/human-images-dataset-men-and-women) | 800 images |
| [Animals Detection Images Dataset](https://www.kaggle.com/datasets/antoreepjana/animals-detection-images-dataset) | 600 images |
| [Scene Classification Dataset](https://www.kaggle.com/datasets/nitishabharathi/scene-classification) | 800 images |
| [Vehicle Images Dataset](https://www.kaggle.com/datasets/lyensoetanto/vehicle-images-dataset) | 400 images |
| [Daily Objects Around the World — Dollar Street](https://www.kaggle.com/datasets/humansintheloop/dollar-street-dataset) | 800 images |
