
<template>
  <div v-if="isLoading" class="loading-overlay">
    <h2>INITIALISATION DES SYSTEMES... {{ loadingProgress }}%</h2>
    <p>Chargement des modèles 3D & shaders de post-traitement...</p>
  </div>
</template>

<script setup>
defineProps({
  isLoading: Boolean,
  loadingProgress: Number
})
</script>

<style scoped>
.loading-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: black;
  color: magenta;
  z-index: 100;
  font-family: 'Courier New', Courier, monospace;
}
.loading-overlay h2 {
  text-shadow: 0 0 10px magenta;
  font-size: 2rem;
}
</style>

