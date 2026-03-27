<script setup>
import Scene3D from './components/Scene3D.vue'
import GameUI from './components/GameUI.vue'
import GameTimer from './components/GameTimer.vue'
import HintSystem from './components/HintSystem.vue'
</script>

<template>
  <Scene3D />
  <GameUI />
  <GameTimer />
  <HintSystem :hints="['Vérifie les ordonnances', 'Cherche bien dans les tiroirs', 'Le mot de passe PC est en évidence']" />
</template>

<style scoped>
</style>
