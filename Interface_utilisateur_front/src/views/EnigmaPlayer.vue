<template>
  <div class="enigma-wrapper">
    <!-- Header con timer -->
    <header class="enigma-header">
      <button @click="abandonner" class="btn-back">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
          fill="none" stroke="currentColor" stroke-width="2">
          <line x1="19" y1="12" x2="5" y2="12"></line>
          <polyline points="12 19 5 12 12 5"></polyline>
        </svg>
        Abandonner
      </button>

      <div class="timer-display">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
          fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"></circle>
          <polyline points="12 6 12 12 16 14"></polyline>
        </svg>
        <span>{{ timerDisplay }}</span>
      </div>

      <div class="enigma-title">{{ enigmaName }}</div>
    </header>

    <!-- iframe del juego -->
    <div class="iframe-container" v-if="!solved && !loading">
      <iframe
        ref="gameFrame"
        :src="enigmaUrl"
        class="game-iframe"
        sandbox="allow-scripts allow-same-origin allow-forms allow-popups"
      ></iframe>
    </div>

    <!-- Pantalla de carga -->
    <div class="loading-screen" v-if="loading">
      <div class="spinner"></div>
      <p>Chargement de l'énigme...</p>
    </div>

    <!-- Pantalla de éxito -->
    <div class="success-screen" v-if="solved">
      <div class="success-content">
        <div class="success-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24"
            fill="none" stroke="#22d3ee" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
        </div>
        <h2>Énigme résolue !</h2>
        <p>Temps : <strong>{{ timerDisplay }}</strong></p>
        <p>Score : <strong>{{ finalScore }} / 100</strong></p>
        <button @click="retourDashboard" class="btn-retour">
          Retour au tableau de bord
        </button>
      </div>
    </div>

    <!-- Error -->
    <div class="error-msg" v-if="errorMsg">{{ errorMsg }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { gameService } from '../services/api'

const router = useRouter()
const route = useRoute()

const enigmaId = route.params.id
const enigmaName = ref('Énigme en cours...')
const enigmaUrl = ref('')
const loading = ref(true)
const solved = ref(false)
const errorMsg = ref('')
const finalScore = ref(0)
const sessionId = ref(null)

// ---- Timer ----
const secondes = ref(0)
let timerInterval = null

const timerDisplay = computed(() => {
  const m = Math.floor(secondes.value / 60).toString().padStart(2, '0')
  const s = (secondes.value % 60).toString().padStart(2, '0')
  return `${m}:${s}`
})

function startTimer() {
  timerInterval = setInterval(() => { secondes.value++ }, 1000)
}

function stopTimer() {
  clearInterval(timerInterval)
}

// ---- Map enigmaId → URL del juego HTML ----
// Ajusta estos paths según tus archivos reales
const ENIGMA_URLS = {
  '1': '/Cabinet Deckard/Enigme Cabinet Deckard.html',
  '2': '/combo-lock/index.html',
  '3': '/ftp-interface/index.html',
  // Añade aquí los demás enigmas
}

const ENIGMA_NAMES = {
  '1': 'Cabinet du Dr. Deckard',
  '2': 'Coffre à combinaison',
  '3': 'Interface FTP',
}

// ---- Inicialización ----
onMounted(async () => {
  enigmaUrl.value = ENIGMA_URLS[enigmaId] || '/'
  enigmaName.value = ENIGMA_NAMES[enigmaId] || `Énigme ${enigmaId}`

  // 1. Iniciar la sesión en el backend
  try {
    const session = await gameService.startGame()
    sessionId.value = session.sessionId
  } catch (err) {
    // Si ya hay una sesión activa, continuar igual
    console.warn('Session déjà active ou erreur:', err.message)
  }

  loading.value = false
  startTimer()

  // 2. Escuchar el evento ENIGMA_SOLVED que el juego HTML envía via postMessage
  window.addEventListener('message', onGameMessage)
})

onUnmounted(() => {
  stopTimer()
  window.removeEventListener('message', onGameMessage)
})

// ---- Receptor del mensaje del juego ----
async function onGameMessage(event) {
  // Aceptar mensajes de cualquier origen (nuestro iframe local)
  const data = event.data

  if (!data || data.type !== 'ENIGMA_SOLVED') return

  stopTimer()

  const answer = data.answer || 'SOLVED'
  const tempsEnSecondes = secondes.value

  try {
    // 3. Validar la respuesta en el backend
    const result = await gameService.validatePuzzle(enigmaId, answer)

    if (result.success) {
      finalScore.value = calcularScore(tempsEnSecondes)
      solved.value = true

      // 4. Cerrar la sesión y grabar el tiempo
      await gameService.endGame()
    } else {
      // Respuesta incorrecta — el juego no debería enviar ENIGMA_SOLVED
      // si la respuesta es mala, pero por seguridad:
      errorMsg.value = 'Réponse incorrecte transmise.'
      setTimeout(() => { errorMsg.value = '' }, 3000)
      startTimer() // reanudar timer
    }
  } catch (err) {
    console.error('Erreur validation:', err)
    errorMsg.value = 'Erreur de connexion au serveur.'
    startTimer()
  }
}

// Score basado en tiempo (más rápido = más puntos)
function calcularScore(segs) {
  if (segs < 300) return 100
  if (segs < 600) return 80
  if (segs < 900) return 60
  return 40
}

async function abandonner() {
  stopTimer()
  try { await gameService.endGame() } catch (_) {}
  router.push('/dashboard')
}

function retourDashboard() {
  router.push('/dashboard')
}
</script>

<style scoped>
.enigma-wrapper {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #0f0f1a;
  color: white;
}

.enigma-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1.5rem;
  background: rgba(0, 0, 0, 0.4);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
  z-index: 10;
}

.btn-back {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  padding: 0.4rem 0.9rem;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-family: inherit;
  font-size: 0.85rem;
  transition: background 0.2s;
}
.btn-back:hover { background: rgba(255, 255, 255, 0.1); }

.timer-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.4rem;
  font-weight: 700;
  color: #22d3ee;
  font-variant-numeric: tabular-nums;
}

.enigma-title {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.iframe-container {
  flex: 1;
  position: relative;
}

.game-iframe {
  width: 100%;
  height: 100%;
  border: none;
  display: block;
}

.loading-screen {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
  color: rgba(255, 255, 255, 0.7);
}

.spinner {
  width: 48px;
  height: 48px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #22d3ee;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.success-screen {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #0f0f2e, #1a0a30);
}

.success-content {
  text-align: center;
  padding: 3rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(34, 211, 238, 0.3);
  border-radius: 20px;
  max-width: 420px;
}

.success-icon {
  margin-bottom: 1.5rem;
}

.success-content h2 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: #22d3ee;
}

.success-content p {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 0.5rem;
}

.btn-retour {
  margin-top: 2rem;
  background: #06b6d4;
  color: white;
  border: none;
  padding: 0.875rem 2rem;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.2s, transform 0.2s;
}
.btn-retour:hover { background: #0891b2; transform: translateY(-2px); }

.error-msg {
  position: fixed;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  background: #ef4444;
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 0.9rem;
}
</style>