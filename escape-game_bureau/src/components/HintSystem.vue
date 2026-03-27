<template>
  <div class="hint-btn" :class="{ exhausted: limitReached }" @click="getHint">
     Indice <span class="hint-counter">({{ remaining }})</span>
  </div>

  <Teleport to="body">
    <div class="hint-toast" :class="{ visible: toastVisible }">
      <span style="margin-right: 8px;"></span>
      <span>{{ toastMessage }}</span>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  hints: {
    type: Array,
    default: () => [
      'Cherche bien autour de toi',
      'As-tu regardé tous les objets interactifs ?',
      'La solution est souvent sous tes yeux'
    ]
  }
})

const hintCount = ref(0)
const hintLimit = computed(() => props.hints.length)
const limitReached = computed(() => hintCount.value >= hintLimit.value)
const remaining = computed(() => hintLimit.value - hintCount.value)

const toastMessage = ref('')
const toastVisible = ref(false)
let hideTimeout = null

function getHint() {
  if (hintCount.value < hintLimit.value) {
    toastMessage.value = props.hints[hintCount.value]
    hintCount.value++
  } else {
    toastMessage.value = "Plus d'indices disponibles !"
  }
  
  toastVisible.value = true
  if (hideTimeout) clearTimeout(hideTimeout)
  hideTimeout = setTimeout(() => { toastVisible.value = false }, 4000)
}
</script>

<style scoped>
.hint-btn {
  position: fixed;
  top: 24px;
  left: 24px;
  background: rgba(15, 23, 42, 0.9);
  border-left: 4px solid #50ef87;
  color: #50ef87;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 18px;
  font-weight: bold;
  padding: 12px 20px;
  border-radius: 4px;
  z-index: 9999;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
  backdrop-filter: blur(8px);
}

.hint-btn:hover {
  background: rgba(30, 41, 59, 1);
}

.hint-btn.exhausted {
  border-left-color: #ff6b6b;
  color: #ff6b6b;
}

.hint-counter {
  font-size: 0.8em;
  opacity: 0.8;
}

.hint-toast {
  position: fixed;
  top: 90px;
  left: 50%;
  transform: translateX(-50%) translateY(-20px);
  background: rgba(15, 23, 42, 0.95);
  color: #fff;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 16px;
  padding: 15px 30px;
  border-radius: 8px;
  border: 1px solid #ffd700;
  z-index: 10000;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.4s ease, transform 0.4s ease;
  backdrop-filter: blur(4px);
}

.hint-toast.visible {
  opacity: 1;
  transform: translateX(-50%) translateY(0);
}
</style>
