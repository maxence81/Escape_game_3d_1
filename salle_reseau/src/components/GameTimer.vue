<template>
  <div class="game-timer">
    <div class="timer-label">TEMPS</div>
    <div class="timer-value">{{ displayTime }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  timeSeconds: {
    type: Number,
    default: 0
  }
})

const displayTime = computed(() => {
  const m = Math.floor(props.timeSeconds / 60).toString().padStart(2, '0')
  const s = (props.timeSeconds % 60).toString().padStart(2, '0')
  return m + ':' + s
})
</script>

<style scoped>
.game-timer {
  position: fixed;
  top: 24px;
  right: 24px;
  background: rgba(0, 10, 20, 0.9);
  border-right: 4px solid cyan;
  padding: 12px 20px;
  color: #f1f5f9;
  font-family: 'Courier New', monospace;
  z-index: 100;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.3);
  text-align: right;
  backdrop-filter: blur(8px);
}

.timer-label {
  font-size: 0.6rem;
  font-weight: 900;
  color: cyan;
  letter-spacing: 0.1em;
  margin-bottom: 4px;
}

.timer-value {
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: 0.05em;
}
</style>
