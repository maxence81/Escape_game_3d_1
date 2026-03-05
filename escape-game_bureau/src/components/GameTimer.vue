<template>
  <div class="game-timer">{{ displayTime }}</div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const hours = ref(0)
const minutes = ref(0)
const seconds = ref(0)
const displayTime = ref('00:00:00')
let timer = null

function formatTime(val) {
  return val < 10 ? '0' + val : val
}

function updateDisplay() {
  displayTime.value = `${formatTime(hours.value)}:${formatTime(minutes.value)}:${formatTime(seconds.value)}`
}

function startTimer() {
  if (timer) return
  timer = setInterval(() => {
    seconds.value++
    if (seconds.value === 60) {
      seconds.value = 0
      minutes.value++
      if (minutes.value === 60) {
        minutes.value = 0
        hours.value++
      }
    }
    updateDisplay()
  }, 1000)
}

function stopTimer() {
  clearInterval(timer)
  timer = null
}

function getTimerValue() {
  return displayTime.value
}

// Expose to window for sub-iframes
window.startTimer = startTimer
window.stopTimer = stopTimer
window.getTimerValue = getTimerValue

onMounted(() => {
  startTimer()
})

onBeforeUnmount(() => {
  stopTimer()
})
</script>

<style scoped>
.game-timer {
  position: fixed;
  top: 20px;
  right: 20px;
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  color: #50ef87;
  font-family: 'Source Code Pro', monospace;
  font-size: 24px;
  font-weight: bold;
  padding: 10px 20px;
  border-radius: 8px;
  border: 2px solid #50ef87;
  z-index: 100;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}
</style>
