# DogCat — Interface Angular

Le frontend présente les images et les résultats du modèle de classification. Il repose sur Angular 21, des composants standalone et une détection des changements sans Zone.js. L’état des composants est géré par des signals ; les échanges HTTP passent par les Observables du service `ImageService`.

## Pages

| Route | Fonctionnalités |
| --- | --- |
| `/upload` | Sélection JPG/PNG, aperçu, import et accès à la galerie |
| `/gallery` | Collection d’images, évaluation, nouvelle tentative et résultats |

La route racine redirige vers `/upload`. La galerie conserve une action visible sur chaque carte : évaluation initiale, réévaluation ou nouvelle tentative. Les requêtes en cours et les erreurs sont présentées dans l’interface.

## Communication avec l’API

`ImageService` centralise l’import, la liste des images, le déclenchement de l’inférence et les URL des fichiers. L’API attendue est `http://localhost:8080`. Le modèle s’exécute dans le backend Java.

## Exécution locale

Commandes depuis la racine du dépôt :

```powershell
cd dog-cat-frontend
npm ci
npm start
```

Le serveur de développement expose l’interface sur `http://localhost:4200`. Le chargement de la galerie, l’import et l’évaluation nécessitent une [API opérationnelle](../dog-cat-backend/README.md).

Sous PowerShell, `npm.cmd` constitue une alternative lorsque l’exécution de `npm.ps1` est bloquée.

## Vérification

Commandes depuis `dog-cat-frontend/` :

```powershell
npm run build
npm test -- --watch=false
```

La compilation produit les fichiers distribuables dans `dist/dog-cat-frontend/`. Les tests couvrent notamment les mises à jour après réponse HTTP, l’évaluation et la gestion des erreurs d’import et d’analyse.
