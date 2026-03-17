<script setup>
import Scene3D from './components/Scene3D.vue'
import GameUI from './components/GameUI.vue'
import GameTimer from './components/GameTimer.vue'
import HintSystem from './components/HintSystem.vue'
import ComputerOverlay from './components/ComputerOverlay.vue'
import CodeBoxOverlay from './components/CodeBoxOverlay.vue'
import PlaquetteOverlay from './components/PlaquetteOverlay.vue'
import { useGameState } from './composables/useGameState.js'

const { showComputer, showCodeBox, showPlaquette } = useGameState()
</script>

<template>
  <Scene3D />
  <GameUI />
  <GameTimer />
  <HintSystem />
  <ComputerOverlay  v-if="showComputer"  />
  <CodeBoxOverlay   v-if="showCodeBox"   />
  <PlaquetteOverlay v-if="showPlaquette" />
</template>

<style scoped>
</style>
