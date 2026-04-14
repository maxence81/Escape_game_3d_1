<template>
  <div class="dashboard-layout">
    <header class="dashboard-header">
      <button @click="logout" class="btn-logout">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="19" y1="12" x2="5" y2="12"></line>
          <polyline points="12 19 5 12 12 5"></polyline>
        </svg>
        Déconnexion
      </button>
      <button @click="$router.push('/statistiques')" class="btn-menu">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="3" y1="12" x2="21" y2="12"></line>
          <line x1="3" y1="6" x2="21" y2="6"></line>
          <line x1="3" y1="18" x2="21" y2="18"></line>
        </svg>
      </button>
    </header>

    <div class="dashboard-banner">
      <h2>Accueil Post-Inscription</h2>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Chargement de votre progression...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <button @click="loadDashboard" class="btn-retry">Réessayer</button>
    </div>

    <div v-else class="dashboard-content">
      <!-- Left Profile Panel -->
      <aside class="profile-panel">
        <div class="profile-avatar">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#22d3ee" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
          </svg>
        </div>
        <h3 class="greeting">Bonjour,<br/><span class="username">[{{ userName }}]</span> !</h3>
        <p class="ready-text">Prêt(e) pour relever le défi ?</p>
        <button class="btn-replay-global-intro" @click="$router.push('/introduction')">
          Revoir l'introduction
        </button>
        <div class="progress-summary">
          <div class="progress-bar-bg">
            <div class="progress-bar-fill" :style="{ width: progressPercent + '%' }"></div>
          </div>
          <p class="progress-text">{{ completedCount }} / {{ levels.length }} enigmes résolus</p>
        </div>
        <div class="profile-footer">
          <div class="divider">Projet FIE-3 • CHL</div>
        </div>
      </aside>

      <!-- Right Levels Panel -->
      <main class="levels-panel">
        <div v-for="(level, index) in levels" :key="index" class="level-card" :class="{ 'card-solved': level.status === 'RÉUSSI', 'card-locked': level.status === 'VERROUILLÉ' }">
          <div class="level-icon-wrapper" :style="{ backgroundColor: getLevelIconBg(level.status) }">
            <svg v-html="getLevelIcon(index)" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></svg>
          </div>
          <div class="level-info">
            <div class="level-header">
              <h4>{{ level.nom }}</h4>
              <div class="level-badges">
                <span class="difficulty-badge" :class="getDifficultyClass(level.difficulte)">{{ level.difficulte }}</span>
                <span class="status-badge" :class="getStatusClass(level.status)">{{ level.status }}</span>
              </div>
            </div>
            <p class="level-subtitle">{{ getLevelSubtitle(index) }}</p>
            <p class="level-desc">{{ level.description || getLevelDesc(index) }}</p>
            <div class="level-actions">
              <button
                v-if="level.status === 'COMMENCER'"
                @click="startEnigma(level)"
                class="btn-start"
              >
                Commencer &rsaquo;
              </button>
              <button
                v-else-if="level.status === 'RÉUSSI'"
                class="btn-solved"
                disabled
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg>
                Complété
              </button>
              <button v-else class="btn-locked" disabled>
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                Verrouillé
              </button>

              <button
                v-if="level.status !== 'VERROUILLÉ'"
                @click="replayIntro(level.id)"
                class="btn-replay-level-intro"
                title="Revoir l'introduction de l'énigme"
              >
                Revoir l'introduction
              </button>
              
              <button
                v-if="level.status === 'RÉUSSI'"
                @click="openFiche(level.id)"
                class="btn-fiche-pedagogique"
                title="Revoir la fiche pédagogique"
              >
                Revoir la fiche
              </button>
            </div>
          </div>
        </div>

        <!-- Si aucun niveau chargé -->
        <div v-if="levels.length === 0" class="empty-state">
          <p>Aucun enigme disponible pour le moment.</p>
        </div>

        <!-- Bouton Conclusion de fin de jeu -->
        <div v-if="levels.length > 0" class="conclusion-section">
          <button 
            class="btn-conclusion" 
            :class="{ 'btn-conclusion-locked': completedCount < levels.length, 'btn-conclusion-unlocked': completedCount === levels.length }"
            :disabled="completedCount < levels.length"
            @click="$router.push('/conclusion')"
          >
            <span v-if="completedCount < levels.length" class="conclusion-content">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
              Conclure l'enquête ({{ completedCount }} / {{ levels.length }} énigmes résolues)
            </span>
            <span v-else class="conclusion-content">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
              Conclure l'enquête
            </span>
          </button>
        </div>

        <!-- Bouton Télécharger le certificat -->
        <div v-if="completedCount === levels.length && levels.length > 0" class="certificate-section">
          <div class="certificate-card">
            <div class="certificate-icon-glow">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
            </div>
            <div class="certificate-text">
              <h4>Certificat de Complétion</h4>
              <p>Vous avez terminé toutes les énigmes. Téléchargez votre certificat officiel.</p>
            </div>
            <button @click="downloadCertificate" class="btn-certificate" :disabled="generatingPdf">
              <svg v-if="!generatingPdf" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="7 10 12 15 17 10"></polyline>
                <line x1="12" y1="15" x2="12" y2="3"></line>
              </svg>
              <div v-else class="spinner-small"></div>
              {{ generatingPdf ? 'Génération...' : 'Télécharger PDF' }}
            </button>
          </div>
        </div>
      </main>
    </div>

    <!-- Modal pour l'introduction de l'énigme -->
    <div v-if="currentIntroId" class="modal-overlay" style="z-index: 5000;">
      <ComicEnigmePanel
        :title="currentEpisodeData.title"
        :bgImage="currentEpisodeData.bgImage"
        buttonText="Fermer l'introduction"
        @start="stopIntro"
      >
        <p v-for="(p, index) in currentEpisodeData.paragraphs" :key="index">{{ p }}</p>
      </ComicEnigmePanel>
    </div>
    <!-- Modal Fiche Pédagogique -->
    <div v-if="currentFicheId" class="modal-overlay" style="z-index: 3000;">
      <div class="modal-box fiche-modal">
        <button @click="closeFiche" class="btn-close-modal">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"></polyline></svg>
          Fermer la fiche
        </button>
        <div class="fiche-content-wrapper">
          <Suspense>
            <template #default>
              <component :is="activeFicheComponent" @continue="closeFiche" />
            </template>
            <template #fallback>
              <div class="frame-loading"><div class="spinner"></div><p>Chargement de la fiche...</p></div>
            </template>
          </Suspense>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, defineAsyncComponent } from 'vue'
import { useRouter } from 'vue-router'
import { authService, studentService, gameService } from '../services/api'
import ComicEnigmePanel from '../components/ComicEnigmePanel.vue'

import stripReseau from '../assets/comics/strip_reseau.png'
import stripBureau from '../assets/comics/strip_bureau.png'
import stripChambre from '../assets/comics/strip_chambre.png'
import stripPharmacie from '../assets/comics/strip_pharmacie.png'
import stripReunion from '../assets/comics/strip_reunion.png'

import { jsPDF } from 'jspdf'

const router = useRouter()
const userName = ref('utilisateur')
const levels = ref([])
const loading = ref(true)
const error = ref(null)
const generatingPdf = ref(false)
const logoBase64 = ref(null)

// Mapping enigme ID -> route de l'enigme (ordre défini par les sous-dossiers)
// IDs dans la BD correspondent à l'ordre d'insertion des enigmes
const ENIGME_ROUTES = {
  1: '/enigme/salle-reseau',
  2: '/enigme/bureau',
  3: '/enigme/chambre-patient',
  4: '/enigme/pharmacie',
  5: '/enigme/salle-reunion',
}

const INTRO_TEXTS = {
  1: {
    id: 1, title: 'L\'ENQUÊTE', bgImage: stripReseau,
    paragraphs: [
      "Une mort suspecte a eu lieu.",
      "Vous êtes dans la salle réseau du laboratoire, un lieu clé pour comprendre ce qui s'est réellement passé.",
      "Votre mission : rétablir la connexion Internet, accéder au système informatique et trouver le code du coffre-fort.",
      "En analysant les fichiers secrets et le rapport d'autopsie, vous devrez découvrir la véritable cause du décès.",
      "Le temps presse... La vérité ne tient qu'à un fil."
    ]
  },
  2: {
    id: 2, title: 'LE BUREAU DU DOCTEUR DECKARD', bgImage: stripBureau,
    paragraphs: [
      "Vous venez d'entrer dans le bureau du Dr Deckard.",
      "Ce bureau renferme de nombreux secrets liés au mystère de la mort de Mme Calvin.",
      "Inspectez chaque recoin, observez l'environnement et fouillez ses dossiers personnels.",
      "Méfiez-vous des apparences, les réponses sont souvent dissimulées sous vos yeux.",
      "Le temps est compté... Accedez au serveur et découvrez la vérité."
    ]
  },
  3: {
    id: 3, title: 'LA CHAMBRE PATIENT', bgImage: stripChambre,
    paragraphs: [
      "La chambre d'hôpital de Mme Calvin est la dernière zone qu'elle a occupée avant son décès.",
      "Il est crucial d'examiner attentivement ce lieu pour comprendre ce qui lui est arrivé.",
      "Cherchez des indices dissimulés, décryptez le dossier médical et recomposez les pièces du puzzle.",
      "Soyez perspicaces, la moindre erreur de jugement pourrait tout fausser..."
    ]
  },
  4: {
    id: 4, title: 'LA PHARMACIE DE L\'HÔPITAL', bgImage: stripPharmacie,
    paragraphs: [
      "Vos investigations vous mènent finalement au dispensaire médical de l'hôpital.",
      "C'est dans ce lieu hautement sécurisé que les traitements de Mme Calvin ont été préparés et délivrés.",
      "Trouvez l'accès au système de l'ordinateur principal pour accéder au dossier de Mme Calvin.",
      "Fouillez les registres, analysez le dossier de Mme Calvin et completer le mini jeu.",
      "Les ours en peluche et le pingouin seront vos pires ennemis..."
    ]
  },
  5: {
    id: 5, title: 'LA RÉVÉLATION', bgImage: stripReunion,
    paragraphs: [
      "Vous avez rassemblé toutes les pièces du puzzle au cours de vos précédentes investigations.",
      "Vous voici dans la salle de réunion, le lieu de toutes les décisions.",
      "Tout indique qu'une erreur a été comise par un de vos collègues. Qui est le véritable responsable ?",
      "L'heure est venue de confronter les suspects et de faire éclater la vérité.",
      "La vérité sur la mort de Mme Calvin est entre vos mains."
    ]
  }
}

const currentIntroId = ref(null)
const currentEpisodeData = computed(() => currentIntroId.value ? INTRO_TEXTS[currentIntroId.value] : null)

function replayIntro(id) {
  currentIntroId.value = id
}

function stopIntro() {
  currentIntroId.value = null
}

const fichesMap = {
  1: defineAsyncComponent(() => import('../components/fiches_pedagogiques/FicheSalleReseau.vue')),
  2: defineAsyncComponent(() => import('../components/fiches_pedagogiques/FicheIngenierieSociale.vue')),
  3: defineAsyncComponent(() => import('../components/fiches_pedagogiques/FicheIntelligenceArtificielle.vue')),
  4: defineAsyncComponent(() => import('../components/fiches_pedagogiques/FicheRetroconception.vue')),
  5: defineAsyncComponent(() => import('../components/fiches_pedagogiques/FicheResponsabiliteEthique.vue')),
}

const currentFicheId = ref(null)
const activeFicheComponent = computed(() => currentFicheId.value ? fichesMap[currentFicheId.value] : null)

function openFiche(id) {
  currentFicheId.value = id
}

function closeFiche() {
  currentFicheId.value = null
}

// Données statiques de présentation par index (0-based)
const LEVEL_META = [
  {
    subtitle: 'Cabinet du Médecin',
    icon: '<rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>',
    iconBgUnlocked: 'rgba(168, 85, 247, 0.4)',
  },
  {
    subtitle: 'Urgence Vitale',
    icon: '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>',
    iconBgUnlocked: 'rgba(236, 72, 153, 0.4)',
  },
  {
    subtitle: 'Code Médicament',
    icon: '<rect x="2" y="10" width="20" height="4" rx="2"></rect><path d="M6 10V6a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v4"></path>',
    iconBgUnlocked: 'rgba(168, 85, 247, 0.4)',
  },
  {
    subtitle: 'Enquête Numérique',
    icon: '<rect x="2" y="2" width="20" height="20" rx="2"></rect><line x1="8" y1="2" x2="8" y2="22"></line>',
    iconBgUnlocked: 'rgba(6, 182, 212, 0.4)',
  },
  {
    subtitle: 'Conseil Médical',
    icon: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path>',
    iconBgUnlocked: 'rgba(168, 85, 247, 0.4)',
  },
]

const DEFAULT_DESCS = [
  'Déchiffrez le dossier médical pour trouver le diagnostic du patient.',
  'Stabilisez le patient en analysant les constantes vitales et les dossiers.',
  'Déverrouillez le coffre en identifiant les molécules et prescriptions.',
  'Résolvez l\'énigme réseau pour accéder aux fichiers secrets du serveur.',
  'Présentez votre cas devant le comité d\'experts et rendez votre verdict.',
]

const completedCount = computed(() => levels.value.filter(l => l.status === 'RÉUSSI').length)
const progressPercent = computed(() => levels.value.length ? (completedCount.value / levels.value.length) * 100 : 0)

function getLevelIconBg(status) {
  if (status === 'RÉUSSI') return 'rgba(16, 185, 129, 0.4)'
  if (status === 'VERROUILLÉ') return 'rgba(100, 100, 100, 0.3)'
  return 'rgba(168, 85, 247, 0.4)'
}

function getLevelIcon(index) {
  return LEVEL_META[index]?.icon || '<circle cx="12" cy="12" r="10"></circle>'
}

function getLevelSubtitle(index) {
  return LEVEL_META[index]?.subtitle || ''
}

function getLevelDesc(index) {
  return DEFAULT_DESCS[index] || ''
}

function getDifficultyClass(diff) {
  if (!diff) return 'badge-medium'
  const d = diff.toLowerCase()
  if (d === 'facile') return 'badge-easy'
  if (d === 'difficile' || d === 'dur') return 'badge-hard'
  return 'badge-medium'
}

function getStatusClass(status) {
  if (status === 'RÉUSSI') return 'status-done'
  if (status === 'COMMENCER') return 'status-start'
  return 'status-locked'
}

async function loadDashboard() {
  loading.value = true
  error.value = null
  try {
    const data = await studentService.getDashboardData()
    levels.value = data

    // Récupérer le vrai nom depuis les statistiques du joueur
    try {
      const stats = await studentService.getMyStats()
      if (stats && stats.playerName) {
        userName.value = stats.playerName
        localStorage.setItem('registeredUserName', stats.playerName)
      }
    } catch (statsErr) {
      console.warn('Impossible de récupérer le nom du joueur', statsErr)
    }

  } catch (e) {
    error.value = 'Impossible de charger votre progression. Vérifiez votre connexion.'
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function startEnigma(level) {
  try {
    // Démarrer une session de jeu côté backend
    await gameService.startGame()
    // Stocker l'ID de l'enigme en cours
    localStorage.setItem('currentEnigmaId', level.id)
    localStorage.setItem('enigmaStartTime', Date.now().toString())
    // Naviguer vers l'enigme correspondant
    const route = ENIGME_ROUTES[level.id]
    if (route) {
      router.push(route)
    } else {
      alert(`Enigme "${level.nom}" — route non configurée (ID: ${level.id})`)
    }
  } catch (e) {
    console.error('Erreur démarrage enigme:', e)
    alert('Impossible de démarrer l\'enigme. Réessayez.')
  }
}

function logout() {
  authService.logout()
  router.push('/')
}

// Charger le logo ISIS en base64 pour le PDF
async function loadLogoBase64() {
  try {
    const response = await fetch('/logo-ISIS-horizontal-RVB-HD.png')
    const blob = await response.blob()
    return new Promise((resolve) => {
      const reader = new FileReader()
      reader.onloadend = () => resolve(reader.result)
      reader.readAsDataURL(blob)
    })
  } catch (e) {
    console.error('Erreur chargement logo:', e)
    return null
  }
}

// Calcul du score global pour le certificat
function computeGlobalScore() {
  const solved = levels.value.filter(l => l.status === 'RÉUSSI')
  if (solved.length === 0) return 0
  // On réutilise la même formule que StatistiquesEtudiant : 100 - (erreurs * 10)
  // Les niveaux du dashboard n'ont pas forcément les erreurs, on utilise un score estimé
  return 100
}

async function downloadCertificate() {
  if (generatingPdf.value) return
  generatingPdf.value = true

  try {
    // Charger le logo si pas encore fait
    if (!logoBase64.value) {
      logoBase64.value = await loadLogoBase64()
    }

    // Charger les stats pour le score réel
    let globalScore = 0
    try {
      const statsData = await studentService.getMyStats()
      if (statsData && statsData.enigmaTimes) {
        const standardEnigmas = ['Salle Réseau', 'Bureau Médecin', 'Chambre du Patient', 'Pharmacie', 'Salle de Réunion']
        const enigmasJoues = standardEnigmas.map((name) => {
          const found = statsData.enigmaTimes.find(e => e.nom === name)
          if (found) return Math.max(0, 100 - (found.erreurs * 10))
          return null
        }).filter(s => s !== null)
        if (enigmasJoues.length > 0) {
          globalScore = Math.round(enigmasJoues.reduce((a, b) => a + b, 0) / enigmasJoues.length)
        }
      }
    } catch (e) {
      console.warn('Stats non disponibles, score par défaut', e)
      globalScore = 100
    }

    // Générer le PDF
    const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' })
    const W = 297, H = 210

    // --- Fond dégradé ---
    const steps = 200
    for (let i = 0; i < steps; i++) {
      const t = i / steps
      const r = Math.round(15 + t * 10)
      const g = Math.round(10 + t * 5)
      const b = Math.round(40 + t * 30)
      doc.setFillColor(r, g, b)
      doc.rect(0, (H / steps) * i, W, (H / steps) + 1, 'F')
    }

    // --- Bordure décorative ---
    doc.setDrawColor(168, 85, 247)
    doc.setLineWidth(1.5)
    doc.roundedRect(12, 10, W - 24, H - 20, 4, 4, 'S')
    doc.setDrawColor(34, 211, 238)
    doc.setLineWidth(0.5)
    doc.roundedRect(15, 13, W - 30, H - 26, 3, 3, 'S')

    // --- Étoiles décoratives (petits cercles) ---
    const starPositions = [
      [30, 25], [50, 18], [80, 22], [120, 16], [200, 20], [250, 25], [270, 18],
      [35, 185], [70, 190], [140, 188], [220, 185], [260, 192]
    ]
    starPositions.forEach(([sx, sy]) => {
      doc.setFillColor(168, 85, 247)
      doc.circle(sx, sy, 0.6, 'F')
    })

    // --- Logo ISIS ---
    if (logoBase64.value) {
      doc.addImage(logoBase64.value, 'PNG', W / 2 - 30, 22, 60, 24)
    }

    // --- Ligne séparatrice sous le logo ---
    const lineY = 52
    doc.setDrawColor(168, 85, 247)
    doc.setLineWidth(0.4)
    const lineLen = 80
    doc.line(W / 2 - lineLen / 2, lineY, W / 2 + lineLen / 2, lineY)
    // Points décoratifs aux extrémités
    doc.setFillColor(34, 211, 238)
    doc.circle(W / 2 - lineLen / 2, lineY, 1, 'F')
    doc.circle(W / 2 + lineLen / 2, lineY, 1, 'F')

    // --- Titre ---
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(28)
    doc.setTextColor(255, 255, 255)
    doc.text('CERTIFICAT DE COMPLÉTION', W / 2, 66, { align: 'center' })

    // --- Sous-titre ---
    doc.setFont('helvetica', 'normal')
    doc.setFontSize(12)
    doc.setTextColor(180, 180, 200)
    doc.text('Escape Game ', W / 2, 76, { align: 'center' })

    // --- "Décerné à" ---
    doc.setFontSize(11)
    doc.setTextColor(168, 85, 247)
    doc.text('Ce certificat est décerné à', W / 2, 92, { align: 'center' })

    // --- Nom du joueur ---
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(26)
    doc.setTextColor(34, 211, 238)
    doc.text(userName.value.toUpperCase(), W / 2, 106, { align: 'center' })

    // --- Ligne sous le nom ---
    doc.setDrawColor(34, 211, 238)
    doc.setLineWidth(0.3)
    const nameLineLen = 100
    doc.line(W / 2 - nameLineLen / 2, 110, W / 2 + nameLineLen / 2, 110)

    // --- Texte de complétion ---
    doc.setFont('helvetica', 'normal')
    doc.setFontSize(11)
    doc.setTextColor(200, 200, 220)
    doc.text('pour avoir complété avec succès les 5 énigmes', W / 2, 120, { align: 'center' })
    doc.text("de l'Escape Game de l'Ecole d'Ingénieur ISIS.", W / 2, 127, { align: 'center' })

    // --- Bloc Score ---
    const scoreBoxW = 70, scoreBoxH = 28
    const scoreBoxX = W / 2 - scoreBoxW / 2, scoreBoxY = 136
    doc.setFillColor(30, 20, 60)
    doc.setDrawColor(168, 85, 247)
    doc.setLineWidth(0.6)
    doc.roundedRect(scoreBoxX, scoreBoxY, scoreBoxW, scoreBoxH, 3, 3, 'FD')

    doc.setFont('helvetica', 'normal')
    doc.setFontSize(9)
    doc.setTextColor(168, 85, 247)
    doc.text('SCORE GLOBAL', W / 2, scoreBoxY + 9, { align: 'center' })

    doc.setFont('helvetica', 'bold')
    doc.setFontSize(20)
    doc.setTextColor(34, 211, 238)
    doc.text(`${globalScore} / 100`, W / 2, scoreBoxY + 22, { align: 'center' })

    // --- Date ---
    const now = new Date()
    const dateStr = now.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
    doc.setFont('helvetica', 'italic')
    doc.setFontSize(9)
    doc.setTextColor(150, 150, 170)
    doc.text(`Délivré le ${dateStr}`, W / 2, 178, { align: 'center' })

    // --- Footer ---
    doc.setFont('helvetica', 'normal')
    doc.setFontSize(7)
    doc.setTextColor(100, 100, 130)
    doc.text('Projet FIE-3 • ISIS Ingénieur Santé Numérique • Centre Hospitalier de Lourdes', W / 2, 195, { align: 'center' })

    // Télécharger
    doc.save(`Certificat_EscapeGame_${userName.value.replace(/\s+/g, '_')}.pdf`)
  } catch (e) {
    console.error('Erreur génération certificat:', e)
    alert('Erreur lors de la génération du certificat. Réessayez.')
  } finally {
    generatingPdf.value = false
  }
}

onMounted(() => {
  const storedName = localStorage.getItem('registeredUserName')
  if (storedName) userName.value = storedName

  if (!authService.isAuthenticated()) {
    router.push('/connexion')
    return
  }
  loadDashboard()
})
</script>

<style scoped>
.dashboard-layout {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
}

.btn-logout, .btn-menu {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-family: inherit;
  font-size: 0.875rem;
  transition: background 0.2s;
}
.btn-menu { padding: 0.5rem; }
.btn-logout:hover, .btn-menu:hover { background: rgba(255, 255, 255, 0.1); }

.dashboard-banner {
  background: rgba(45, 27, 84, 0.4);
  border-radius: 8px;
  padding: 1rem 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.05);
}
.dashboard-banner h2 {
  font-size: 1.15rem;
  font-weight: 600;
  margin: 0;
  color: white;
}

/* Loading / Error */
.loading-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  gap: 1rem;
  color: rgba(255,255,255,0.7);
}
.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255,255,255,0.2);
  border-top-color: #22d3ee;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.btn-retry {
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.2);
  color: white;
  padding: 0.5rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-family: inherit;
}

.dashboard-content {
  display: flex;
  gap: 2rem;
  flex: 1;
  align-items: flex-start;
  padding-bottom: 2rem;
}

/* Profile Panel */
.profile-panel {
  width: 280px;
  background: rgba(45, 27, 84, 0.6);
  border-radius: 12px;
  padding: 3rem 2rem 2rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  position: sticky;
  top: 2rem;
  min-height: 400px;
}
.profile-avatar {
  width: 80px;
  height: 80px;
  background: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 1.5rem;
  border: 4px solid #22d3ee;
  box-shadow: 0 0 20px rgba(34, 211, 238, 0.4);
}
.greeting {
  font-size: 1.4rem;
  font-weight: 500;
  line-height: 1.3;
  margin-bottom: 0.75rem;
  color: white;
}
.username { color: #22d3ee; font-weight: 700; }
.ready-text {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 1rem;
}

.btn-replay-global-intro {
  background: rgba(34, 211, 238, 0.1);
  border: 1px solid rgba(34, 211, 238, 0.3);
  color: #22d3ee;
  border-radius: 20px;
  padding: 0.4rem 1rem;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 2rem;
}

.btn-replay-global-intro:hover {
  background: rgba(34, 211, 238, 0.2);
}

.progress-summary {
  width: 100%;
  margin-bottom: 2rem;
}
.progress-bar-bg {
  width: 100%;
  height: 6px;
  background: rgba(255,255,255,0.15);
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}
.progress-bar-fill {
  height: 100%;
  background: linear-gradient(to right, #22d3ee, #a855f7);
  border-radius: 3px;
  transition: width 0.5s ease;
}
.progress-text {
  font-size: 0.75rem;
  color: rgba(255,255,255,0.6);
}

.profile-footer .divider {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.4);
}

/* Levels Panel */
.levels-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.level-card {
  background: rgba(67, 39, 114, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: transform 0.2s, background 0.2s;
}
.level-card:hover:not(.card-locked) {
  background: rgba(78, 46, 133, 0.8);
  transform: translateX(4px);
}
.card-solved {
  border-color: rgba(16, 185, 129, 0.3);
  background: rgba(16, 185, 129, 0.08) !important;
}
.card-locked {
  opacity: 0.6;
}

.level-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.level-icon-wrapper svg { stroke: white; }

.level-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.level-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.25rem;
  gap: 0.5rem;
}
.level-header h4 {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0;
  color: white;
}
.level-badges {
  display: flex;
  gap: 0.4rem;
  flex-shrink: 0;
}

.difficulty-badge, .status-badge {
  font-size: 0.65rem;
  font-weight: 700;
  padding: 0.2rem 0.5rem;
  border-radius: 10px;
  color: white;
}
.badge-easy { background: #10b981; }
.badge-medium { background: #eab308; }
.badge-hard { background: #ef4444; }

.status-done { background: rgba(16, 185, 129, 0.3); color: #4ade80; border: 1px solid rgba(16,185,129,0.4); }
.status-start { background: rgba(168, 85, 247, 0.3); color: #d8b4fe; border: 1px solid rgba(168,85,247,0.4); }
.status-locked { background: rgba(100, 100, 100, 0.3); color: rgba(255,255,255,0.4); border: 1px solid rgba(255,255,255,0.1); }

.level-subtitle {
  font-size: 0.75rem;
  color: #22d3ee;
  font-weight: 500;
  margin-bottom: 0.4rem;
}
.level-desc {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 1rem;
}

.level-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.btn-start {
  background: #06b6d4;
  color: white;
  border: none;
  padding: 0.4rem 1.25rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s, transform 0.2s;
}
.btn-start:hover { background: #0891b2; transform: translateY(-1px); }

.btn-solved {
  background: rgba(16, 185, 129, 0.2);
  color: #4ade80;
  border: 1px solid rgba(16, 185, 129, 0.3);
  padding: 0.4rem 1.25rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.4rem;
  cursor: default;
}

.btn-locked {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0.4rem 1.25rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.4rem;
  cursor: not-allowed;
}

.btn-replay-level-intro {
  background: transparent;
  color: #a855f7;
  border: 1px solid #a855f7;
  padding: 0.4rem 1rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-replay-level-intro:hover {
  background: rgba(168, 85, 247, 0.1);
}

.btn-fiche-pedagogique {
  background: transparent;
  color: #3b82f6;
  border: 1px solid #3b82f6;
  padding: 0.4rem 1rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-fiche-pedagogique:hover {
  background: rgba(59, 130, 246, 0.1);
}

/* Modal CSS */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

.modal-box {
  background: #1e293b;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 16px;
  padding: 2rem;
  max-width: 420px;
  width: 90%;
  color: white;
  text-align: center;
  box-shadow: 0 25px 50px rgba(0,0,0,0.5);
}

.fiche-modal {
  width: 90vw;
  max-width: 1200px;
  height: 90vh;
  display: flex !important;
  flex-direction: column;
  padding: 0 !important;
  overflow: hidden;
  background: #0f172a;
}

.fiche-content-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
  position: relative;
  text-align: left;
}

.btn-close-modal {
  background: rgba(15, 23, 42, 0.95);
  border: none;
  color: white;
  padding: 1rem 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: background 0.2s;
  flex-shrink: 0;
}

.btn-close-modal:hover {
  background: rgba(255, 255, 255, 0.05);
}

.frame-loading {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: rgba(255,255,255,0.5);
}

.conclusion-section {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
  margin-bottom: 2rem;
}

.btn-conclusion {
  width: 100%;
  max-width: 600px;
  padding: 1rem 2rem;
  border-radius: 12px;
  font-family: inherit;
  font-size: 1.1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  letter-spacing: 0.05em;
}

.conclusion-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.btn-conclusion-locked {
  background: rgba(15, 23, 42, 0.6);
  color: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.1);
  cursor: not-allowed;
}

.btn-conclusion-unlocked {
  background: linear-gradient(135deg, #a855f7, #ec4899);
  color: white;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(168, 85, 247, 0.4);
  animation: pulse-glow 2s infinite;
}

.btn-conclusion-unlocked:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(236, 72, 153, 0.6);
}

@keyframes pulse-glow {
  0% { box-shadow: 0 0 0 0 rgba(236, 72, 153, 0.4); }
  70% { box-shadow: 0 0 0 15px rgba(236, 72, 153, 0); }
  100% { box-shadow: 0 0 0 0 rgba(236, 72, 153, 0); }
}

/* Certificate Section */
.certificate-section {
  margin-top: 1.5rem;
  margin-bottom: 2rem;
}

.certificate-card {
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.12), rgba(34, 211, 238, 0.08));
  border: 1px solid rgba(168, 85, 247, 0.25);
  border-radius: 14px;
  padding: 1.5rem 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  position: relative;
  overflow: hidden;
  animation: certificate-glow 3s ease-in-out infinite;
}

.certificate-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(168, 85, 247, 0.1) 0%, transparent 70%);
  pointer-events: none;
}

@keyframes certificate-glow {
  0%, 100% { box-shadow: 0 0 15px rgba(168, 85, 247, 0.15); }
  50% { box-shadow: 0 0 30px rgba(168, 85, 247, 0.25), 0 0 60px rgba(34, 211, 238, 0.1); }
}

.certificate-icon-glow {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.3), rgba(34, 211, 238, 0.2));
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #d8b4fe;
  border: 1px solid rgba(168, 85, 247, 0.3);
}

.certificate-text {
  flex: 1;
}

.certificate-text h4 {
  margin: 0 0 0.3rem 0;
  font-size: 1rem;
  font-weight: 700;
  color: #e2e8f0;
  letter-spacing: 0.01em;
}

.certificate-text p {
  margin: 0;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.55);
  line-height: 1.4;
}

.btn-certificate {
  background: linear-gradient(135deg, #a855f7, #22d3ee);
  color: white;
  border: none;
  padding: 0.65rem 1.5rem;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  white-space: nowrap;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(168, 85, 247, 0.3);
  flex-shrink: 0;
}

.btn-certificate:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(168, 85, 247, 0.5);
}

.btn-certificate:disabled {
  opacity: 0.7;
  cursor: wait;
}

.spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@media (max-width: 900px) {
  .dashboard-content { flex-direction: column; }
  .profile-panel { width: 100%; position: relative; top: 0; min-height: auto; }
  .certificate-card { flex-direction: column; text-align: center; }
}
</style>
