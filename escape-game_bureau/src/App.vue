<template>
  <div id="experience">
    <canvas ref="canvasRef" class="experience-canvas"></canvas>
    <button class="night-mode-btn" @click="toggleNightMode">🌙</button>
  </div>

  <!-- UI Components -->
  <GameTimer />
  <HintSystem />

  <!-- Computer Overlay -->
  <ComputerOverlay
    :visible="overlays.computer"
    :iframe-src="computerSrc"
    @close="closeOverlay('computer')"
  />

  <!-- Dog Picture Overlay -->
  <ImageOverlay
    :visible="overlays.dog"
    image-src="/images/oslo.png"
    frame-style="wood"
    close-style="wood-close"
    @close="closeOverlay('dog')"
  />

  <!-- Draw Picture Overlay -->
  <ImageOverlay
    :visible="overlays.draw"
    image-src="/images/lea.png"
    frame-style="white"
    @close="closeOverlay('draw')"
  />

  <!-- Anniv Picture Overlay -->
  <ImageOverlay
    :visible="overlays.anniv"
    image-src="/images/anniv.png"
    frame-style="white"
    @close="closeOverlay('anniv')"
  />

  <!-- Chest Overlay -->
  <ImageOverlay
    :visible="overlays.chest"
    iframe-src="/combo-lock/index.html"
    frame-style="chest"
    @close="closeOverlay('chest')"
  />

  <!-- Server Overlay -->
  <ServerOverlay
    :visible="overlays.server"
    @close="closeOverlay('server')"
  />

  <!-- Skeleton / Completion Overlay -->
  <SkeletonOverlay
    :visible="overlays.skeleton"
    @close="closeOverlay('skeleton')"
    @completed="onGameCompleted"
  />
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useThreeScene } from './composables/useThreeScene.js'
import GameTimer from './components/GameTimer.vue'
import HintSystem from './components/HintSystem.vue'
import ComputerOverlay from './components/ComputerOverlay.vue'
import ImageOverlay from './components/ImageOverlay.vue'
import ServerOverlay from './components/ServerOverlay.vue'
import SkeletonOverlay from './components/SkeletonOverlay.vue'

const canvasRef = ref(null)

const overlays = reactive({
  computer: false,
  dog: false,
  draw: false,
  anniv: false,
  chest: false,
  server: false,
  skeleton: false,
})

const computerSrc = ref('')

function onObjectClick(objectType) {
  if (objectType === 'computer') {
    computerSrc.value = '/Cabinet Deckard/Enigme Cabinet Deckard.html'
  }
  overlays[objectType] = true
  sceneApi.setControlsEnabled(false)
}

function closeOverlay(name) {
  overlays[name] = false
  if (name === 'computer') {
    computerSrc.value = ''
  }
  // Re-enable controls only if no overlay is open
  const anyOpen = Object.values(overlays).some(v => v)
  if (!anyOpen) {
    sceneApi.setControlsEnabled(true)
  }
}

function onGameCompleted() {
  // Game completed - confetti is handled inside SkeletonOverlay
}

// Escape key handler for all overlays
function onEscapeKey(event) {
  if (event.code === 'Escape') {
    for (const key of Object.keys(overlays)) {
      if (overlays[key]) {
        closeOverlay(key)
        return
      }
    }
  }
}

// Three.js scene
const emitProxy = (event, data) => {
  if (event === 'object-click') onObjectClick(data)
}

const sceneApi = useThreeScene(canvasRef, emitProxy)
const { toggleNightMode } = sceneApi

onMounted(() => {
  sceneApi.init()
  window.addEventListener('keydown', onEscapeKey)
})

onBeforeUnmount(() => {
  sceneApi.dispose()
  window.removeEventListener('keydown', onEscapeKey)
})
</script>

<style>
@import './style.css';

.night-mode-btn {
  position: absolute;
  top: 20px;
  left: 30px;
  z-index: 10;
  padding: 10px;
  border: none;
  font-size: 24px;
  cursor: pointer;
  border-radius: 50%;
  background: linear-gradient(135deg, #1a1a2e, #16213e);
}
</style>
