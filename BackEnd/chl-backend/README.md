# Serveur Backend - Escape Game 3D

Ce répertoire contient le code source du backend (API REST) pour l'Escape Game 3D interactif. Il a été développé avec **Java** et le framework **Spring Boot**.

## 🧠 Logique et Fonctionnement

Le backend agit comme le cerveau du système :
- **API REST** : Expose des endpoints pour permettre au Frontend (Vue.js) d'effectuer des opérations sans accès direct à la base de données.
- **Sécurité et Authentification** : Utilise **Spring Security** avec des **JSON Web Tokens (JWT)**. Lorsqu'un étudiant se connecte, il reçoit un token qui certifie son identité lors de ses futures requêtes, sans stockage de session en mémoire côté serveur. Les routes "Admin" exigent un rôle spécifique `ROLE_ADMIN`.
- **Persistance des Données (ORM)** : Repose sur **Hibernate / JPA**. L'application mappe les objets Java (Entités) directement dans la base de données MySQL. `application.properties` définit la règle `ddl-auto=update` qui assure la création et mise à jour des tables automatiquement.
- **Gestion du Jeu** : Consignation des statistiques clés (démarrage, validation des réponses, temps chronométré envoyé par le client, utilisation des indices).

## 🗄️ Base de Données

Le backend est connecté à une base de données **MySQL**.
Actuellement, cette base de données est hébergée dans le Cloud via **Amazon RDS (AWS)** (*voir l'URL jdbc dans `application.properties`*). L'identifiant `admin` et le mot de passe sont spécifiés dans ce même fichier de configuration.
Si vous souhaitez travailler entièrement sans Internet (offline), vous pouvez installer MySQL localement, créer un schéma (database) vide `escape_game_db`, et ajuster le lien jdbc en pointant vers `localhost`.

## 🚀 Version et Déploiement

- **Versionnement** : Le socle actuel utilise une version mature de **Spring Boot 3.x** (compatible Java 17+).
- **Déploiement cible** : L'API est prévue pour tourner de façon permanente. L'URL actuelle de production est masquée derrière un nom de domaine dynamique (DuckDNS) pour garantir le HTTPS requis par les navigateurs modernes (`apiescapegame.duckdns.org`).  
  Pour le déploiement sur un vrai serveur (ex. VPS, EC2, Raspberry Pi), exécutez la commande `mvn clean package` pour générer un fichier `.jar` exécutable, que vous lancerez avec une commande telle que `java -jar nom_du_fichier.jar`.

## ⚙️ Remplacement de configurations futures

Toutes les variables critiques se trouvent dans le fichier suivant :
📍 `src/main/resources/application.properties`

C'est ici que des commentaires détaillés en français ont été rajoutés pour la maintenance. Vous pourrez y modifier :
- Le port du serveur (actuellement 8080).
- Les identifiants de la base de données MySQL.
- La clé secrète JWT (pour déconnecter de force tout le monde en invalidant l'ancienne).
