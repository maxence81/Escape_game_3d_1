<template>
  <div class="victory-overlay">
    <div class="victory-card">
      <h2 class="victory-title">Salle Reseau Terminee</h2>
      <p class="victory-subtitle">Vous avez resolu toutes les enigmes.</p>
      <p class="final-time">Temps final : {{ formattedTime }}</p>

    </div>
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

const formattedTime = computed(() => {
  const m = Math.floor(props.timeSeconds / 60).toString().padStart(2, '0')
  const s = (props.timeSeconds % 60).toString().padStart(2, '0')
  return m + ':' + s
})
</script>

<style scoped>
.victory-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 10, 20, 0.9);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 60;
}

.victory-card {
  background: rgba(0, 15, 25, 0.95);
  border: 2px solid cyan;
  border-radius: 12px;
  padding: 40px;
  max-width: 650px;
  width: 90%;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 0 40px rgba(0, 255, 255, 0.2);
  color: #e0e0e0;
}

.victory-title {
  margin-top: 0;
  font-size: 2rem;
  color: cyan;
  text-align: center;
  text-shadow: 0 0 10px cyan;
}

.victory-subtitle {
  text-align: center;
  color: #aaa;
  margin-bottom: 10px;
}

.final-time {
  text-align: center;
  font-size: 1.4rem;
  font-weight: bold;
  color: cyan;
  font-family: 'Courier New', monospace;
  margin-bottom: 30px;
}
</style>
