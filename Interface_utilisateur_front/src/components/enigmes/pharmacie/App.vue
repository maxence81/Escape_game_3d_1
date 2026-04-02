<script setup>
import Scene3D from './components/Scene3D.vue'
import GameUI from './components/GameUI.vue'
import HintSystem from './components/HintSystem.vue'
</script>

<template>
  <Scene3D />
  <GameUI />
  <HintSystem :hints="['Observe bien le bureau', 'Clique sur l\'ours en peluche', 'Clique sur le pc']" />
</template>

<style scoped>
</style>
