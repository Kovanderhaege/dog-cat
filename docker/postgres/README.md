# DogCat — PostgreSQL local

Le service `db` de Docker Compose fournit la base PostgreSQL de DogCat. L’image est construite depuis `postgres:17-alpine`. Elle expose le port 5432 sur l’interface locale et conserve les données dans le volume `postgres_data`.

## Configuration

| Paramètre | Valeur ou origine |
| --- | --- |
| Base | `dogcat` |
| Adresse | `localhost:5432` |
| Utilisateur | Variable `DB_USER` |
| Mot de passe | Variable `DB_PASSWORD` |
| Persistance | Volume `postgres_data` |

Les fichiers `compose.yaml` et `application.yaml` référencent les variables. Le fichier local `.env`, placé à côté de `compose.yaml`, contient leurs valeurs et est ignoré par Git. Sa structure est :

```dotenv
DB_USER=dogcat
DB_PASSWORD=mot_de_passe_local
```

Le mot de passe de cet exemple est une valeur illustrative. Les valeurs locales doivent correspondre aux identifiants de la base. Une modification de `.env` ne modifie pas les identifiants d’un volume PostgreSQL déjà initialisé.

## Exécution

Docker Compose et un moteur Docker en mode conteneurs Linux sont nécessaires. Les commandes suivantes s’exécutent depuis la racine du dépôt :

```powershell
docker compose up -d --build --wait
```

Compose charge automatiquement `.env`. Le contrôle de santé utilise `pg_isready` pour vérifier la disponibilité de PostgreSQL. Les données du volume restent présentes après `docker compose down`.

## Connexion du backend depuis un terminal

Spring Boot lit les variables d’environnement du processus Java. Le fichier `.env` n’est pas chargé implicitement par l’API.

Exemple de lancement PowerShell depuis la racine du dépôt :

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^(DB_USER|DB_PASSWORD)=(.*)$') {
        [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
    }
}
cd dog-cat-backend
./mvnw.cmd spring-boot:run
```

Cet exemple suppose des valeurs sans guillemets ni commentaires en fin de ligne. Les variables sont limitées au terminal courant et à ses processus enfants.

## Connexion du backend depuis VS Code

Une configuration Java peut transmettre le fichier d’environnement avec `envFile`. Exemple lorsque le dossier ouvert est la racine du dépôt :

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "DogCat API (.env)",
      "request": "launch",
      "mainClass": "com.kova.dogcat.DogcatApplication",
      "projectName": "dogcat",
      "cwd": "${workspaceFolder}/dog-cat-backend",
      "envFile": "${workspaceFolder}/.env"
    }
  ]
}
```

Lorsque le dossier ouvert est le parent du dépôt, les chemins deviennent `${workspaceFolder}/dog-cat/dog-cat-backend` et `${workspaceFolder}/dog-cat/.env`.

La configuration sélectionnée dans « Exécuter et déboguer » est lancée par F5 ou Ctrl+F5. Le lancement Java automatique ne charge pas `.env` sans configuration. Le dossier `.vscode/` du dépôt est ignoré par Git.

## Exploitation locale

```powershell
docker compose config --quiet
docker compose ps
docker compose logs db
docker compose down
```

`docker compose config --quiet` valide la configuration sans afficher les valeurs résolues. La commande sans `--quiet` peut afficher les identifiants. La configuration du schéma applicatif relève du [backend](../../dog-cat-backend/README.md).
