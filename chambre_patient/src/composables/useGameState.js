import { ref, reactive } from 'vue'

// ── Overlays visibles ──────────────────────────────────────────
const showComputer   = ref(false)
const showCodeBox    = ref(false)
const showPlaquette  = ref(false)
const currentPlaqueIndex = ref(0)

// ── Déblocage des cartes/objets ───────────────────────────────
const discoveredComputer = ref(false)
const discoveredBox = ref(false)
const unlockedPlaques = reactive([false, false, false, false])

// ── Progression ────────────────────────────────────────────────
const boxUnlocked        = ref(false)   // code 124 trouvé
const computerCompleted  = ref(false)   // interface ordinateur validée
const gamePassed         = ref(false)   // toutes enigmes résolues

// ── Chronomètre ────────────────────────────────────────────────
const timeSeconds = ref(0)
const isTimerRunning = ref(false)
let timerInterval = null

function formatTime(totalSeconds) {
  const h = Math.floor(totalSeconds / 3600)
  const m = Math.floor((totalSeconds % 3600) / 60)
  const s = totalSeconds % 60
  return [h, m, s].map(v => v < 10 ? '0' + v : v).join(':')
}

function startTimer() {
  if (timerInterval) return
  isTimerRunning.value = true
  timerInterval = setInterval(() => {
    if (isTimerRunning.value) {
      timeSeconds.value++
    }
  }, 1000)
}

function stopTimer() {
  isTimerRunning.value = false
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
}

function unlockPlaque(index) {
  if (index >= 0 && index < unlockedPlaques.length) {
    unlockedPlaques[index] = true
  }
}

function unlockedPlaquesCount() {
  return unlockedPlaques.filter(Boolean).length
}

export function useGameState() {
  return {
    showComputer,
    showCodeBox,
    showPlaquette,
    currentPlaqueIndex,
    discoveredComputer,
    discoveredBox,
    unlockedPlaques,
    boxUnlocked,
    computerCompleted,
    gamePassed,
    unlockPlaque,
    unlockedPlaquesCount,
    // Timer
    timeSeconds,
    isTimerRunning,
    startTimer,
    stopTimer,
    formatTime
  }
}
