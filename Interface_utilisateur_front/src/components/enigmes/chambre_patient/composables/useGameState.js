import { ref, reactive } from 'vue'

const showIntro = ref(true)
const showComputer = ref(false)
const showCodeBox = ref(false)
const showPlaquette = ref(false)
const showPathHint = ref(false)
const currentPlaqueIndex = ref(0)

const discoveredComputer = ref(false)
const discoveredBox = ref(false)
const unlockedPlaques = reactive([false, false, false, false])

const boxUnlocked = ref(false)
const computerCompleted = ref(false)
const gamePassed = ref(false)

function unlockPlaque(index) {
  if (index >= 0 && index < unlockedPlaques.length) {
    unlockedPlaques[index] = true
  }
}

function unlockedPlaquesCount() {
  return unlockedPlaques.filter(Boolean).length
}

function finishIntro() {
  showIntro.value = false
}

function resetGameState() {
  showIntro.value = true
  showComputer.value = false
  showCodeBox.value = false
  showPlaquette.value = false
  showPathHint.value = false
  currentPlaqueIndex.value = 0
  discoveredComputer.value = false
  discoveredBox.value = false
  for (let i = 0; i < unlockedPlaques.length; i++) {
    unlockedPlaques[i] = false
  }
  boxUnlocked.value = false
  computerCompleted.value = false
  gamePassed.value = false
}

export function useGameState() {
  return {
    showIntro,
    finishIntro,
    resetGameState,
    showComputer,
    showCodeBox,
    showPlaquette,
    showPathHint,
    currentPlaqueIndex,
    discoveredComputer,
    discoveredBox,
    unlockedPlaques,
    boxUnlocked,
    computerCompleted,
    gamePassed,
    unlockPlaque,
    unlockedPlaquesCount,
  }
}
