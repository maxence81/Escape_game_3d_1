<template>
  <div class="game-timer">
    <div class="timer-label">TEMPS ÉCOULÉ</div>
    <div class="timer-value">{{ displayTime }}</div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useGameState } from '@/composables/useGameState.js'

const { timeSeconds, startTimer, formatTime } = useGameState()

const displayTime = computed(() => formatTime(timeSeconds.value))

onMounted(() => {
  startTimer()
})
</script>

<style scoped>
.game-timer {
  position: fixed;
  top: 24px;
  right: 24px;
  background: rgba(15, 23, 42, 0.9);
  border-right: 4px solid #38bdf8;
  padding: 12px 20px;
  color: #f1f5f9;
  font-family: 'JetBrains Mono', monospace;
  z-index: 100;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.3);
  text-align: right;
  backdrop-filter: blur(8px);
}

.timer-label {
  font-size: 0.6rem;
  font-weight: 900;
  color: #38bdf8;
  letter-spacing: 0.1em;
  margin-bottom: 4px;
}

.timer-value {
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: 0.05em;
}
</style>
