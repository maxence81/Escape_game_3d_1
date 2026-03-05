<template>
  <div class="hint-btn" :style="hintStyle" @click="getHint">
    💡 Hint <span class="hint-counter">({{ remaining }})</span>
  </div>

  <Teleport to="body">
    <div class="hint-toast" :style="toastStyle">
      <span style="margin-right: 8px;">🔍</span>
      <span>{{ toastMessage }}</span>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed } from 'vue'

const hintLimit = 3
const hintCount = ref(0)
const remaining = computed(() => hintLimit - hintCount.value)
const toastMessage = ref('')
const toastVisible = ref(false)
const exhausted = ref(false)

const hints = [
  'Regarde bien les plantes 🌿',
  'Date de naissance 🎂',
  'Maladie et allergies 💊'
]

const hintStyle = computed(() => ({
  borderColor: exhausted.value ? '#ff6b6b' : '#50ef87',
  color: exhausted.value ? '#ff6b6b' : '#50ef87'
}))

const toastStyle = computed(() => ({
  opacity: toastVisible.value ? '1' : '0',
  transform: toastVisible.value
    ? 'translateX(-50%) translateY(0)'
    : 'translateX(-50%) translateY(-20px)'
}))

function getHint() {
  if (hintCount.value < hintLimit) {
    toastMessage.value = hints[hintCount.value]
    hintCount.value++
    if (hintCount.value === hintLimit) exhausted.value = true
  } else {
    toastMessage.value = "Plus d'indices disponibles !"
  }
  toastVisible.value = true
  setTimeout(() => { toastVisible.value = false }, 4000)
}
</script>

<style scoped>
.hint-btn {
  position: fixed;
  top: 20px;
  left: 100px;
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  font-family: 'Source Code Pro', monospace;
  font-size: 24px;
  font-weight: bold;
  padding: 10px 20px;
  border-radius: 8px;
  border: 2px solid #50ef87;
  z-index: 100;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
}
.hint-counter {
  font-size: 14px;
  color: #aaa;
}
.hint-toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%) translateY(-20px);
  background: linear-gradient(135deg, #16213e, #0f3460);
  color: #fff;
  font-family: 'Source Code Pro', monospace;
  font-size: 16px;
  padding: 15px 30px;
  border-radius: 10px;
  border: 2px solid #ffd700;
  z-index: 200;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.5);
  pointer-events: none;
  transition: opacity 0.4s ease, transform 0.4s ease;
}
</style>
