<template>
  <div class="cable-overlay" @mousemove="onMouseMove" @mouseup="onMouseUp" @touchmove.prevent="onTouchMove" @touchend="onTouchEnd">
    <!-- Fond panneau électrique -->
    <div class="panel-bg">
      <div class="panel-inner">
        <!-- Titre -->
        <div class="panel-header">
          <div class="header-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#22d3ee" stroke-width="2">
              <path d="M5 12.55a11 11 0 0 1 14.08 0" /><path d="M1.42 9a16 16 0 0 1 21.16 0" />
              <path d="M8.53 16.11a6 6 0 0 1 6.95 0" /><circle cx="12" cy="20" r="1" fill="#22d3ee" />
            </svg>
          </div>
          <h2>Réparer le routeur</h2>
          <p class="subtitle">Reliez chaque câble au connecteur portant le même symbole</p>
        </div>

        <!-- Zone SVG principale -->
        <div class="cable-area" ref="cableArea">
          <svg class="cable-svg" :viewBox="`0 0 ${svgW} ${svgH}`" preserveAspectRatio="xMidYMid meet">
            <!-- Câbles de fond (décoratifs, croisés) -->
            <g class="bg-cables" opacity="0.15">
              <path v-for="(cable, i) in bgCables" :key="'bg'+i" :d="cable" 
                    fill="none" stroke="#888" stroke-width="6" stroke-linecap="round"/>
            </g>

            <!-- Connexions réussies -->
            <g class="solved-cables">
              <path v-for="conn in solvedConnections" :key="'s'+conn.leftId"
                    :d="getCablePath(conn.leftPos, conn.rightPos)"
                    fill="none" :stroke="conn.color" stroke-width="5" stroke-linecap="round"
                    class="cable-solved" />
            </g>

            <!-- Câble en cours de drag -->
            <path v-if="dragging" :d="getCablePath(dragStart, dragCurrent)"
                  fill="none" :stroke="dragColor" stroke-width="4" stroke-linecap="round"
                  stroke-dasharray="8 4" class="cable-dragging" opacity="0.8" />

            <!-- Erreur flash -->
            <path v-if="errorCable" :d="getCablePath(errorCable.from, errorCable.to)"
                  fill="none" stroke="#ef4444" stroke-width="5" stroke-linecap="round"
                  class="cable-error" />

            <!-- Connecteurs gauche -->
            <g v-for="(c, i) in leftConnectors" :key="'L'+i">
              <rect :x="c.x - 30" :y="c.y - 18" width="60" height="36" rx="6"
                    :fill="c.solved ? '#065f46' : c.bgColor" stroke="#333" stroke-width="2"
                    class="connector-box" :class="{ 'connector-hover': !c.solved }"
                    @mousedown.prevent="startDragLeft(i, $event)"
                    @touchstart.prevent="startDragLeftTouch(i, $event)" />
              <!-- Symbole -->
              <text :x="c.x" :y="c.y + 1" text-anchor="middle" dominant-baseline="central"
                    :fill="c.solved ? '#34d399' : '#fff'" font-size="18" font-weight="bold"
                    pointer-events="none">{{ c.symbol }}</text>
              <!-- Câble sortant -->
              <line :x1="c.x + 30" :y1="c.y" :x2="c.x + 50" :y2="c.y"
                    :stroke="c.color" stroke-width="4" stroke-linecap="round" />
              <circle :cx="c.x + 50" :cy="c.y" r="5" :fill="c.color" class="cable-end" />
            </g>

            <!-- Connecteurs droite -->
            <g v-for="(c, i) in rightConnectors" :key="'R'+i">
              <rect :x="c.x - 30" :y="c.y - 18" width="60" height="36" rx="6"
                    :fill="c.solved ? '#065f46' : c.bgColor" stroke="#333" stroke-width="2"
                    class="connector-box" :class="{ 'connector-hover': !c.solved }"
                    @mouseup.prevent="dropOnRight(i)"
                    @touchend.prevent="dropOnRight(i)" />
              <!-- Symbole -->
              <text :x="c.x" :y="c.y + 1" text-anchor="middle" dominant-baseline="central"
                    :fill="c.solved ? '#34d399' : '#fff'" font-size="18" font-weight="bold"
                    pointer-events="none">{{ c.symbol }}</text>
              <!-- Câble sortant -->
              <line :x1="c.x - 50" :y1="c.y" :x2="c.x - 30" :y2="c.y"
                    :stroke="c.color" stroke-width="4" stroke-linecap="round" />
              <circle :cx="c.x - 50" :cy="c.y" r="5" :fill="c.color" class="cable-end" />
            </g>

            <!-- Étincelles au succès final -->
            <g v-if="allSolved" class="sparks">
              <circle v-for="s in sparks" :key="s.id" :cx="s.x" :cy="s.y" :r="s.r"
                      fill="#22d3ee" opacity="0.9" class="spark" />
            </g>
          </svg>
        </div>

        <!-- Indicateur de progression -->
        <div class="progress-row">
          <div v-for="i in 4" :key="i" class="progress-dot" :class="{ 'dot-filled': i <= solvedCount }"></div>
          <span class="progress-label">{{ solvedCount }} / 4 câbles connectés</span>
        </div>

        <!-- Bouton fermer -->
        <button v-if="!allSolved" class="btn-close-panel" @click="$emit('close')">✕</button>
      </div>
    </div>

    <!-- Réussite overlay -->
    <transition name="success-fade">
      <div v-if="allSolved" class="success-overlay">
        <div class="success-content">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#22d3ee" stroke-width="2">
            <path d="M5 12.55a11 11 0 0 1 14.08 0" /><path d="M1.42 9a16 16 0 0 1 21.16 0" />
            <path d="M8.53 16.11a6 6 0 0 1 6.95 0" /><circle cx="12" cy="20" r="1" fill="#22d3ee" />
          </svg>
          <h3>Routeur reconnecté !</h3>
          <p>Le réseau WiFi est de nouveau fonctionnel.</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'

const emit = defineEmits(['solved', 'close'])

const svgW = 600
const svgH = 400

// Définition des 4 câbles
const CABLES = [
  { id: 0, symbol: '○', color: '#eab308', bgColor: '#854d0e' },   // Jaune
  { id: 1, symbol: '✕', color: '#3b82f6', bgColor: '#1e3a5f' },   // Bleu
  { id: 2, symbol: '△', color: '#ef4444', bgColor: '#7f1d1d' },   // Rouge
  { id: 3, symbol: '☆', color: '#d946ef', bgColor: '#701a75' },   // Magenta
]

// Ordre mélangé à droite (Fisher-Yates)
function shuffleArray(arr) {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]]
  }
  return a
}

const rightOrder = ref([])

// Positions
const leftX = 60
const rightX = svgW - 60
const startY = 60
const gapY = 85

const leftConnectors = computed(() =>
  CABLES.map((c, i) => ({
    ...c,
    x: leftX,
    y: startY + i * gapY,
    solved: solvedPairs.value.has(c.id),
  }))
)

const rightConnectors = computed(() =>
  rightOrder.value.map((c, i) => ({
    ...c,
    x: rightX,
    y: startY + i * gapY,
    solved: solvedPairs.value.has(c.id),
  }))
)

// État du jeu
const solvedPairs = ref(new Set())
const solvedConnections = ref([])
const solvedCount = computed(() => solvedPairs.value.size)
const allSolved = ref(false)

// État du drag
const dragging = ref(false)
const dragLeftIndex = ref(-1)
const dragStart = reactive({ x: 0, y: 0 })
const dragCurrent = reactive({ x: 0, y: 0 })
const dragColor = ref('#fff')

// Erreur flash
const errorCable = ref(null)

// Étincelles
const sparks = ref([])

// Câbles de fond décoratifs
const bgCables = computed(() => {
  const paths = []
  for (let i = 0; i < 4; i++) {
    const ly = startY + i * gapY
    // Chaque câble va vers une position aléatoire
    const ry = startY + ((i + 2) % 4) * gapY
    paths.push(getCablePath({ x: leftX + 50, y: ly }, { x: rightX - 50, y: ry }))
  }
  return paths
})

// Référence zone SVG
const cableArea = ref(null)

function getCablePath(from, to) {
  const cx1 = from.x + (to.x - from.x) * 0.4
  const cx2 = from.x + (to.x - from.x) * 0.6
  return `M ${from.x} ${from.y} C ${cx1} ${from.y}, ${cx2} ${to.y}, ${to.x} ${to.y}`
}

function getSvgPoint(event) {
  if (!cableArea.value) return { x: 0, y: 0 }
  const rect = cableArea.value.getBoundingClientRect()
  const clientX = event.touches ? event.touches[0].clientX : event.clientX
  const clientY = event.touches ? event.touches[0].clientY : event.clientY
  return {
    x: ((clientX - rect.left) / rect.width) * svgW,
    y: ((clientY - rect.top) / rect.height) * svgH,
  }
}

function startDragLeft(index, event) {
  const c = leftConnectors.value[index]
  if (c.solved) return
  dragging.value = true
  dragLeftIndex.value = index
  dragColor.value = c.color
  dragStart.x = c.x + 50
  dragStart.y = c.y
  const pt = getSvgPoint(event)
  dragCurrent.x = pt.x
  dragCurrent.y = pt.y
}

function startDragLeftTouch(index, event) {
  const c = leftConnectors.value[index]
  if (c.solved) return
  dragging.value = true
  dragLeftIndex.value = index
  dragColor.value = c.color
  dragStart.x = c.x + 50
  dragStart.y = c.y
  const pt = getSvgPoint(event)
  dragCurrent.x = pt.x
  dragCurrent.y = pt.y
}

function onMouseMove(event) {
  if (!dragging.value) return
  const pt = getSvgPoint(event)
  dragCurrent.x = pt.x
  dragCurrent.y = pt.y
}

function onTouchMove(event) {
  if (!dragging.value) return
  const pt = getSvgPoint(event)
  dragCurrent.x = pt.x
  dragCurrent.y = pt.y
}

function onMouseUp() {
  if (dragging.value) {
    dragging.value = false
    dragLeftIndex.value = -1
  }
}

function onTouchEnd() {
  // Check if we're over a right connector
  if (!dragging.value) return
  const threshold = 40 // pixels in SVG space
  for (let i = 0; i < rightConnectors.value.length; i++) {
    const rc = rightConnectors.value[i]
    const dx = dragCurrent.x - (rc.x - 50)
    const dy = dragCurrent.y - rc.y
    if (Math.sqrt(dx * dx + dy * dy) < threshold) {
      dropOnRight(i)
      return
    }
  }
  dragging.value = false
  dragLeftIndex.value = -1
}

function dropOnRight(rightIndex) {
  if (!dragging.value) return
  dragging.value = false

  const leftCable = CABLES[dragLeftIndex.value]
  const rightCable = rightConnectors.value[rightIndex]

  if (rightCable.solved || leftCable.solved) {
    dragLeftIndex.value = -1
    return
  }

  const leftPos = { x: leftConnectors.value[dragLeftIndex.value].x + 50, y: leftConnectors.value[dragLeftIndex.value].y }
  const rightPos = { x: rightCable.x - 50, y: rightCable.y }

  if (leftCable.id === rightCable.id) {
    // Correct !
    const newSet = new Set(solvedPairs.value)
    newSet.add(leftCable.id)
    solvedPairs.value = newSet

    solvedConnections.value.push({
      leftId: leftCable.id,
      leftPos: { ...leftPos },
      rightPos: { ...rightPos },
      color: leftCable.color,
    })

    // Vérifier victoire
    if (solvedPairs.value.size === 4) {
      allSolved.value = true
      triggerSparks()
      setTimeout(() => {
        emit('solved')
      }, 2000)
    }
  } else {
    // Erreur — flash rouge
    errorCable.value = { from: { ...leftPos }, to: { ...rightPos } }
    setTimeout(() => { errorCable.value = null }, 600)
  }

  dragLeftIndex.value = -1
}

function triggerSparks() {
  const s = []
  for (let i = 0; i < 30; i++) {
    s.push({
      id: i,
      x: Math.random() * svgW,
      y: Math.random() * svgH,
      r: 1 + Math.random() * 3,
    })
  }
  sparks.value = s
}

onMounted(() => {
  rightOrder.value = shuffleArray(CABLES)
})
</script>

<style scoped>
.cable-overlay {
  position: fixed;
  inset: 0;
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(8px);
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.panel-bg {
  width: 90%;
  max-width: 720px;
  background: linear-gradient(145deg, #1a1a2e, #0f0f23);
  border-radius: 16px;
  border: 1px solid rgba(34, 211, 238, 0.2);
  box-shadow: 0 0 60px rgba(34, 211, 238, 0.1), 0 20px 60px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  position: relative;
}

.panel-inner {
  padding: 1.5rem 2rem 2rem;
  position: relative;
}

.panel-header {
  text-align: center;
  margin-bottom: 1rem;
}

.header-icon {
  margin-bottom: 0.5rem;
}

.panel-header h2 {
  color: #e2e8f0;
  font-size: 1.3rem;
  font-weight: 700;
  margin: 0 0 0.25rem 0;
  font-family: 'Segoe UI', system-ui, sans-serif;
}

.subtitle {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.85rem;
  margin: 0;
}

.cable-area {
  width: 100%;
  aspect-ratio: 600 / 400;
  background: linear-gradient(180deg, #0a0a1a 0%, #111128 50%, #0a0a1a 100%);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  overflow: hidden;
  cursor: crosshair;
  position: relative;
}

.cable-svg {
  width: 100%;
  height: 100%;
  display: block;
}

/* Connecteurs */
.connector-box {
  cursor: pointer;
  transition: filter 0.2s;
}

.connector-hover:hover {
  filter: brightness(1.4);
}

.cable-end {
  pointer-events: none;
}

/* Câble résolu */
.cable-solved {
  animation: cableGlow 1.5s ease-in-out;
  filter: drop-shadow(0 0 4px currentColor);
}

@keyframes cableGlow {
  0% { opacity: 0; stroke-width: 8; }
  50% { opacity: 1; stroke-width: 6; }
  100% { opacity: 1; stroke-width: 5; }
}

/* Câble en drag */
.cable-dragging {
  animation: dashMove 0.4s linear infinite;
  filter: drop-shadow(0 0 6px currentColor);
}

@keyframes dashMove {
  to { stroke-dashoffset: -12; }
}

/* Câble erreur */
.cable-error {
  animation: errorFlash 0.6s ease-out forwards;
}

@keyframes errorFlash {
  0% { opacity: 1; stroke-width: 6; }
  30% { opacity: 1; stroke-width: 4; }
  60% { opacity: 0.5; stroke-width: 6; }
  100% { opacity: 0; stroke-width: 2; }
}

/* Étincelles */
.spark {
  animation: sparkAnim 1.5s ease-out forwards;
}

@keyframes sparkAnim {
  0% { opacity: 0; r: 0; }
  20% { opacity: 1; }
  100% { opacity: 0; r: 8; }
}

/* Progression */
.progress-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.progress-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.dot-filled {
  background: #22d3ee;
  border-color: #22d3ee;
  box-shadow: 0 0 8px rgba(34, 211, 238, 0.6);
}

.progress-label {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.8rem;
  margin-left: 0.5rem;
}

/* Bouton fermer */
.btn-close-panel {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-close-panel:hover {
  background: rgba(239, 68, 68, 0.3);
  color: #ef4444;
}

/* Overlay de succès */
.success-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.7);
  z-index: 10;
  border-radius: 16px;
}

.success-content {
  text-align: center;
  animation: successBounce 0.5s ease;
}

.success-content h3 {
  color: #22d3ee;
  font-size: 1.5rem;
  margin: 1rem 0 0.5rem;
  font-family: 'Segoe UI', system-ui, sans-serif;
}

.success-content p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
  margin: 0;
}

@keyframes successBounce {
  0% { transform: scale(0.5); opacity: 0; }
  60% { transform: scale(1.1); }
  100% { transform: scale(1); opacity: 1; }
}

.success-fade-enter-active { transition: all 0.3s ease; }
.success-fade-leave-active { transition: all 0.3s ease; }
.success-fade-enter-from, .success-fade-leave-to { opacity: 0; }
</style>
