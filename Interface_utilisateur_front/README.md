# Interface Client (Frontend) - Escape Game 3D

Ce dossier héberge l'interface utilisateur (SPA - Single Page Application) développée sur **Vue.js 3** avec l'outil de build **Vite**, permettant l'expérience immersive, fluide et la navigation des étudiants.

## 🧠 Logique et Architecture

L'application frontend est conçue pour être modulaire et rapide :
- **Framework** : Utilise l'écosystème Vue.js.
- **Routage global** : Géré par **Vue Router**. Les routes naviguent entre le système de connexion, les tableaux de bord et le jeu. 
- **Sécurisation par Route** : Le fichier `router/index.js` implémente des "Navigation Guards" (`router.beforeEach`). Si une vue requiert d'être connecté (`requiresAuth` dans ses propriétés "meta") ou d'être professeur/administrateur (`requiresAdmin`), le routeur inspecte le cache local pour valider la présence du token JWT avant d'accepter la navigation ou de rediriger l'utilisateur vers la page de login.

## 🎮 Les Salles 3D (Optimisation des ressources)

Les environnements 3D de l'Escape Game consomment potentiellement beaucoup de ressources (calcul processeur et carte graphique via le Canvas avec probable emploi de Three.js / Babylon).  

**Comment a-t-on fait en sorte qu'elles ne chargent que quand on les utilise ?**
- L'approche retenue est celle des **Composants Dynamiques (et Lazy Loading par le Routeur)** avec Vue. 
- L'exécution du code tridimensionnel est purement conditionnée à la présence et la construction du composant d'Énigme (`EnigmeBridge.vue` ou équivalent).
- Le navigateur ne télécharge pas les scripts lourds et ne lance la boucle de rendu 3D (`requestAnimationFrame`) que lorsque l'étudiant navigue physiquement sur cette URL de jeu.
- Inversement, et c'est le plus important : dès qu'il quitte la salle pour revenir à son Tableau de bord Étudiant, le cycle de vie natif de Vue ("onUnmounted" / "beforeUnmount") est exploité. La scène 3D se dissipe, le Canvas est détruit, stoppant immédiatement le calcul GPU et assurant des performances stables et fluides sur les autres pages informatives.

## ✨ Fonctionnalités et Vues Clés

- **Vues d'Authentification** : `Login.vue` et `Register.vue` traitent l'inscription avec profil et sécurisent l'accès.
- **Dashboard Étudiant** : L'écran principal après connexion. Il fait appel à l'API pour verrouiller (avec un cadenas visuel) les énigmes futures et proposer l'entrée dans le jeu en 3D.
- **Dashboard Admin** : Panneau de bord réservé aux professeurs offrant une macro-vision des avancements de la classe et des détails de chaque étudiant (profil de réussite).
- **Les Énigmes** : Sont accompagnées de comportements visuels améliorés telles que des modales d'introduction immersives, des boîtes de dialogue et un système d'inventaire dynamique. Un "Timer global" isolé aide le joueur à rester engagé.

## 🔄 Remplacer l'API Backend

Les appels de données s'adressent à une URL de domaine constante.
Si vous comptez changer de serveur, ou en cas de dysfonctionnement, l'intégralité du remplacement s'effectue dans **un seul fichier commun** centralisé :
📍 `src/services/api.js`

Dans ce fichier, dont tous les appels ont été récemment commentés en français (y compris le rôle de chaque méthode), repérez cette ligne :  
`const API_BASE_URL = 'https://apiescapegame.duckdns.org/api';`  

Vous pourrez y substituer cette adresse par la vôtre, ou bien par `http://localhost:8080/api` si vous démarrez le backend sur le même ordinateur à des fins de développement.

## 🏗️ Installation & Déploiement

1. Naviguez avec le terminal : `cd Interface_utilisateur_front`
2. Installez les paquets (bibliothèques) requis : `npm install`
3. Démarrez un serveur local instantané pour développement : `npm run dev`
4. **Déploiement production** : Lancez `npm run build`. Le système créera un petit paquet ultra compressé de fichiers figés dans le dossier `dist`, qui pourra être hébergé sur n'importe quel portail web (Vercel, GitHub Pages, ou Nginx).
