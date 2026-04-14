// =========================================================================
// 🚀 SERVICE D'API - CONNECTÉ AU BACKEND SPRING BOOT
// Ce fichier est l'artère principale de communication entre le frontend Vue.js et votre backend (serveur Java Spring Boot).
// =========================================================================

// URL de base de votre serveur Backend.
// ❗️ IMPORTANT - POUR UTILISATION FUTURE ET REMPLACEMENT DE VARIABLE :
// Si vous testez l'application sur votre propre PC en local, changez cette constante par : 'http://localhost:8080/api'
// En phase de production (déploiement), assurez-vous qu'elle pointe vers l'adresse exacte du serveur distant, sans "slash" à la fin par convention.
const API_BASE_URL = 'https://apiescapegame.duckdns.org/api';

// Fonction unitaire (helper) destinée à préparer automatiquement les en-têtes (Headers) de vos requêtes.
// Elle injecte le token JWT si l'utilisateur est bien connecté, permettant l'accès aux "routes protégées" par le back-end.
function authHeaders() {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
  };
}

// ----------------------------------------------------
// 1. SERVICES D'AUTHENTIFICATION 
// Fonctions d'enregistrement et de sessions
// ----------------------------------------------------
export const authService = {
  // Inscription d'un nouvel étudiant / utilisateur. Moditez userData si d'autres champs s'ajoutent en base.
  async register(userData) {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }, // Format JSON obligatoire pour l'API Spring Boot
      body: JSON.stringify({
        email: userData.email,
        password: userData.password,
        prenom: userData.prenom,
        nom: userData.nom,
        pseudo: userData.pseudo || userData.prenom,
        // Envoi au Backend du 'profil' (filière, niveau) de l'étudiant
        profil: userData.profil, 
        dateNaissance: userData.dateNaissance,
      }),
    });
    
    // Contrôle d'erreur (ex: email déjà utilisé, mdp trop court, etc.)
    if (!response.ok) {
      const err = await response.json();
      throw new Error(err.message || "Erreur lors de l'inscription");
    }
    const data = await response.json();
    
    // Sauvegarde en cache du nom pour un affichage en interface d'accueil après insciption
    localStorage.setItem('registeredUserName', `${userData.prenom} ${userData.nom}`);
    return { success: true, ...data };
  },

  // Connexion pour le joueur avec vérification
  async login(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!response.ok) {
      throw new Error('Email ou mot de passe incorrect');
    }
    const data = await response.json();
    
    // Le stockage local (localStorage) fait office de "session". Le JWT est critique pour les appels suivants.
    localStorage.setItem('token', data.token);
    localStorage.setItem('userId', data.id);
    localStorage.setItem('userEmail', data.email);
    localStorage.setItem('userRole', data.role);
    return { success: true, ...data };
  },

  // Connexion exclusive pour les profils Administrateurs / Enseignants
  async loginAdmin(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!response.ok) {
      throw new Error('Email ou mot de passe incorrect');
    }
    const data = await response.json();
    
    // Filtrage drastique côté client : un ROLE_USER ne passera pas d'ici (le backend le bloque de toute façon post-connexion).
    if (data.role !== 'ROLE_ADMIN') {
      throw new Error('Accès réservé exclusivement aux administrateurs.');
    }
    localStorage.setItem('token', data.token);
    localStorage.setItem('userId', data.id);
    localStorage.setItem('userEmail', data.email);
    localStorage.setItem('userRole', data.role);
    return { success: true, ...data };
  },

  // Fermeture de session et neutralisation du cache de données en local
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userRole');
    localStorage.removeItem('registeredUserName');
    localStorage.removeItem('sessionId');
    localStorage.removeItem('escapeGlobalTimer'); // Évite que le chronomètre reparte s'il se reconnecte avec le même navigateur.
  },

  // Renvoie un booléen simple pour contrôler visuellement ou via le routeur.js la présence d'une connexion.
  isAuthenticated() {
    return !!localStorage.getItem('token');
  },

  // Obtenir le rôle (ROLE_USER, ROLE_ADMIN) pour adapter l'IHM
  getRole() {
    return localStorage.getItem('userRole');
  },
};

// ----------------------------------------------------
// 2. SERVICES LIÉS AU JEU (Sessions et Énigmes)
// Communique les réussites, le temps passé et assure la persistance de l'Escape Game.
// ----------------------------------------------------
export const gameService = {
  // Ouvre administrativement sur le serveur une session de type GameSession pour le traçage.
  async startGame() {
    const response = await fetch(`${API_BASE_URL}/game/start`, {
      method: 'POST',
      headers: authHeaders(),
    });
    if (!response.ok) throw new Error('Impossible de notifier le démarrage de la partie au serveur.');
    const data = await response.json();
    
    // Le Session ID est vital pour la fin de la partie et lier l'historique au backend.
    localStorage.setItem('sessionId', data.sessionId);
    return data;
  },

  // Centralisation de la validation. Envoie la réponse de l'utilisateur à l'API pour chiffrer / corroborer son code.
  async validatePuzzle(enigmaId, answer, timeSpentSeconds = 0, hintsUsed = 0) {
    const response = await fetch(`${API_BASE_URL}/game/validate-puzzle`, {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify({
        puzzleId: String(enigmaId),
        answer: answer,
        // Tranfert des variables analytiques de la tentative (le backend archive le temps et l'usage des indices).
        timeSpentSeconds: timeSpentSeconds,
        hintsUsed: hintsUsed
      }),
    });
    if (!response.ok) throw new Error("Erreur serveur lors de la validation de la réponse, l'API ne répond pas.");
    return await response.json();
  },

  // Signalement au backend que le parcours est entièrement terminé, arrêtant chronos serveurs et soldant le statut.
  async endGame() {
    const response = await fetch(`${API_BASE_URL}/game/end`, {
      method: 'POST',
      headers: authHeaders(),
    });
    if (!response.ok) throw new Error('Erreur de requête lors de la signalisation de la fin du parcours.');
    
    // On libère la variable sessionId puisque l'escape est clos
    localStorage.removeItem('sessionId');
    return await response.json();
  },
};

// ----------------------------------------------------
// 3. DONNÉES ÉTUDIANT (Vues pour le Dashboard)
// Extraction de la progression d'un seul étudiant ciblé
// ----------------------------------------------------
export const studentService = {
  // Liste exhaustive des différents "modules/énigmes" avec leur état courant (ex: "VERROUILLÉ", "ACHEVÉ").
  async getDashboardData() {
    const response = await fetch(`${API_BASE_URL}/player/dashboard`, {
      headers: authHeaders(), // Accès autorisé uniquement si l'on transite token pour soi-même
    });
    if (!response.ok) throw new Error('Erreur lors du téléchargement des informations du tableau de bord étudiant');
    return await response.json(); // Type Liste de "EnigmaStatusDTO" venant du serveur
  },

  // Regroupement statistique destiné aux graphiques (le Radar "Statistiques" sur le front)
  async getMyStats() {
    const response = await fetch(`${API_BASE_URL}/player/stats`, {
      headers: authHeaders(),
    });
    if (!response.ok) throw new Error('Erreur de récupération des données graphes');
    return await response.json(); // Type "PlayerDetailDTO"
  },
};

// ----------------------------------------------------
// 4. STATISTIQUES ADMINISTRATEUR (Réservé interface Admin)
// Extractions globales concernant tous les inscrits (professeurs & encadrants)
// ----------------------------------------------------
export const adminService = {
  // Synthèses de tous les étudiants (temps de réalisation moyen, effectif total)
  async getGlobalStats() {
    const response = await fetch(`${API_BASE_URL}/admin/dashboard-stats`, {
      headers: authHeaders(), // Sera refusé par le serveur côté "Security filter" si pas ADMIN
    });
    if (!response.ok) throw new Error("Non Autorisé : Vous n'avez pas accès à ce flux de données administrateur.");
    return await response.json();
  },

  // Tableau complet de chaque inscrit sans filtrage pour listing général de classe/promo.
  async getAllPlayers() {
    const response = await fetch(`${API_BASE_URL}/admin/players`, {
      headers: authHeaders(),
    });
    if (!response.ok) throw new Error('Accès au panel des utilisateurs rejeté.');
    return await response.json();
  },

  // Fouille approfondie ou "drill-down" en utilisant l'ID d'un joueur en particulier.
  async getPlayerDetails(playerId) {
    const response = await fetch(`${API_BASE_URL}/admin/player/${playerId}/stats`, {
      headers: authHeaders(),
    });
    if (!response.ok) throw new Error("Ce profil de joueur n'a pas pu être localisé ou vous n'avez pas les droits.");
    return await response.json();
  },
};