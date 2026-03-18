import { reactive, ref } from 'vue';
import confetti from 'canvas-confetti';

export const gameState = reactive({
  isStarted: true,
  showChoiceOverlay: false,
  discoveredBouton: false,
  gamePassed: false,
  gameOver: false,
  choiceMessage: ''
});

export const timeSeconds = ref(0);
let timerInterval = null;

startTimer();

export function startGame() {
  gameState.isStarted = true;
  startTimer();
}

export function openChoice() {
  gameState.showChoiceOverlay = true;
}

export function closeChoice() {
  gameState.showChoiceOverlay = false;
}

export function triggerConfetti() {
  const duration = 5 * 1000;
  const animationEnd = Date.now() + duration;
  const defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 9999 };

  function randomInRange(min, max) {
    return Math.random() * (max - min) + min;
  }

  const interval = setInterval(function() {
    const timeLeft = animationEnd - Date.now();

    if (timeLeft <= 0) {
      return clearInterval(interval);
    }

    const particleCount = 50 * (timeLeft / duration);
    confetti(Object.assign({}, defaults, { particleCount, origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 } }));
    confetti(Object.assign({}, defaults, { particleCount, origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 } }));
  }, 250);
}

export function makeChoice(answer) {
  if (answer === 'Dr Deckard') {
    gameState.gamePassed = true;
    gameState.showChoiceOverlay = false;
    gameState.choiceMessage = 'Bonne réponse. Vous avez découvert la vérité sur la mort de Mme Calvin. Affaire classée.';
    stopTimer();
    triggerConfetti();
  } else {
    gameState.gameOver = true;
    gameState.showChoiceOverlay = false;
    gameState.choiceMessage = 'Mauvaise conclusion. Emma est une intelligence artificielle sous la direction du Dr Deckard... Vous vous êtes trompé de cible !';
    stopTimer();
  }
}

export function startTimer() {
  if (!timerInterval) {
    timerInterval = setInterval(() => {
      timeSeconds.value++;
    }, 1000);
  }
}

export function stopTimer() {
  if (timerInterval) {
    clearInterval(timerInterval);
    timerInterval = null;
  }
}

export function formatTime(seconds) {
  const m = Math.floor(seconds / 60).toString().padStart(2, '0');
  const s = (seconds % 60).toString().padStart(2, '0');
  return m + ':' + s;
}

export function useGameState() {
  return {
    gameState,
    timeSeconds,
    startGame,
    openChoice,
    closeChoice,
    makeChoice,
    startTimer,
    stopTimer,
    formatTime
  }
}
