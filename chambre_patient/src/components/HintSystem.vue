<template>
  <div class="hint-container">
    <button 
      class="hint-btn" 
      :class="{ exhausted: hintCount >= hintLimit }" 
      @click="getHint"
      :disabled="toastVisible"
    >
      <span class="btn-icon">?</span>
      <span class="btn-text">INDICE</span>
      <span class="hint-counter">{{ remaining }} / {{ hintLimit }}</span>
    </button>

    <Teleport to="body">
      <transition name="fade">
        <div v-if="toastVisible" class="hint-toast">
          <div class="toast-header">
            <span class="toast-title">TRANSMISSION D'INDICE</span>
            <span class="toast-id">ID-{{ 500 + hintCount }}</span>
          </div>
          <div class="toast-body">
            {{ toastMessage }}
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const hintLimit = 3
const hintCount = ref(0)
const remaining = computed(() => hintLimit - hintCount.value)
const toastMessage = ref('')
const toastVisible = ref(false)

const hints = [
  "Localisez les séquences de mots colorés dans la conclusion du rapport pour extraire le code de l'unité de stockage.",
  "Consultez les dossiers médicaux (RÉF-101) pour confirmer le type d'allergie et le modèle IA associé.",
  "Utilisez les calques de calibration LSTM pour identifier le coefficient α maximisant la précision."
]

function getHint() {
  if (toastVisible.value) return

  if (hintCount.value < hintLimit) {
    toastMessage.value = hints[hintCount.value]
    hintCount.value++
  } else {
    toastMessage.value = "PROTOCOL D'AIDE ÉPUISÉ : Plus d'indices disponibles."
  }
  
  toastVisible.value = true
  setTimeout(() => { 
    toastVisible.value = false 
  }, 6000)
}
</script>

<style scoped>
.hint-container {
  position: fixed;
  bottom: 24px;
  left: 24px;
  z-index: 100;
  pointer-events: all;
}

.hint-btn {
  background: rgba(15, 23, 42, 0.9);
  border: 1px solid #38bdf8;
  color: #38bdf8;
  padding: 10px 16px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: 'Inter', sans-serif;
  font-weight: 700;
  font-size: 0.75rem;
  letter-spacing: 0.05em;
  transition: all 0.2s;
  backdrop-filter: blur(8px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.hint-btn:hover:not(:disabled) {
  background: #38bdf8;
  color: #fff;
}

.hint-btn.exhausted {
  border-color: #64748b;
  color: #64748b;
  opacity: 0.7;
}

.btn-icon {
  width: 18px;
  height: 18px;
  background: rgba(56, 189, 248, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
}
.hint-btn:hover .btn-icon { background: rgba(255, 255, 255, 0.2); }

.hint-counter {
  font-size: 0.65rem;
  opacity: 0.7;
  padding-left: 8px;
  border-left: 1px solid currentColor;
}

.hint-toast {
  position: fixed;
  top: 100px;
  left: 50%;
  transform: translateX(-50%);
  width: min(450px, 90%);
  background: #1e293b;
  border-left: 4px solid #f08040;
  color: #f1f5f9;
  padding: 20px;
  z-index: 1000;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3);
  font-family: 'Inter', sans-serif;
}

.toast-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 0.65rem;
  font-weight: 900;
  letter-spacing: 0.1em;
}

.toast-title { color: #f08040; }
.toast-id { color: #64748b; }

.toast-body {
  font-size: 0.9rem;
  line-height: 1.6;
  font-weight: 500;
}

/* Transitions */
.fade-enter-active, .fade-leave-active {
  transition: all 0.4s ease;
}
.fade-enter-from {
  opacity: 0;
  transform: translateX(-50%) translateY(-20px);
}
.fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-10px);
}
</style>
