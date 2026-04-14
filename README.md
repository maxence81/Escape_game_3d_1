# 🎓 Projet Tuteuré - Escape Game 3D

Bienvenue dans le dépôt principal du projet "Escape Game 3D" à destination des élèves. 
Ce logiciel global a été conçu pour offrir une expérience ludique en trois dimensions intégrée via un simple navigateur web. Il se décompose en deux grands sous-projets indépendants mais qui communiquent en direct.

## 🏗️ Architecture Globale Coordonnée

L'application repose sur un schéma technique dit "3-Tiers", séparant clairement les responsabilités, ce qui la rend robuste et facilement maintenable :
1. **La Base de données (MySQL)** : C'est le cerveau mémoriel final. Stocke (actuellement chez AWS Cloud) les comptes utilisateurs, leurs statuts cryptés, les temps effectués et la validation des énigmes.
2. **Le Backend (`BackEnd/chl-backend`)** : Développé en Java **Spring Boot**. Il s'agit du garde forestier. Il analyse ce que veut faire le Frontend, autorise l'action si le Token de sécurité est valide, dialogue avec la base de données, puis renvoie le résultat au joueur sans jamais exposer la base de données.
3. **Le Frontend (`Interface_utilisateur_front`)** : L'interface visuelle développée en **Vue.js**. Il permet d'afficher les Tableaux debords, de gérer ce que voient les profs ou les étudiants, et bien sûr de lancer la scène de rendu interactive 3D des salles qui compose l'Escape Game.

## 🗺️ Orientation et Où trouver les informations ?

Chacun de ces deux environnements est pourvu d'un dossier racine possédant un README extrêmement détaillé quant à sa logique, son déploiement et son fonctionnement intime :
- **Si votre rôle concerne la logique Java, le serveur d'API, Spring, la Sécurité ou MySQL** -> Veuillez lire le document dédié dans 📍 `BackEnd/chl-backend/README.md`.
- **Si votre rôle concerne l'interface Vue.js, le chargement intelligent de la 3D, les statistiques graphiques ou si vous voulez pointer le site sur un nouveau port serveur** -> Veuillez lire le document dédié dans 📍 `Interface_utilisateur_front/README.md`.

## ⚙️ Points de jonction à retenir

Pour quiconque reprend le flambeau sur le projet, il y a deux "noeuds" vitaux où les variables de l'un communiquent avec l'autre :
- **Sur le Frontend :** L'URL unique qui relie l'application visuelle au serveur Java se trouve dans `Interface_utilisateur_front/src/services/api.js`.
- **Sur le Backend :** Les identifiants de sécurité de base de données (pour que l'API trouve MySQL) se trouvent dans le fichier de clés `BackEnd/chl-backend/src/main/resources/application.properties`.

> **Note aux futurs développeurs** : L'intégralité du code sensible a été commentée et documentée en français pour guider la maintenance, en particulier les deux fichiers pré-cités qui gèrent intégralement l'écosystème de réseau et variable à remplacer lors d'un éventuel passage vers de nouvelles adresses IP ou noms de domaines.
