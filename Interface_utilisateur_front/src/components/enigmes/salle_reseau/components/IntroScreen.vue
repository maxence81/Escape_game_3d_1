<template>
  <div class="intro-container">
    <div class="bd-container">
      <ComicEnigmePanel 
        title="Enigme 1: La Salle Réseau" 
        :bgImage="bgImageImport" 
        @start="skipIntro"
      >
        <p>Une mort suspecte a eu lieu.</p>
        <p>Vous êtes dans la salle réseau du laboratoire, un lieu clé pour comprendre ce qui s'est réellement passé.</p>
        <p>Votre mission : rétablir la connexion Internet, accéder au système informatique et trouver le code du coffre-fort.</p>
        <p>En analysant les fichiers secrets et le rapport d'autopsie, vous devrez découvrir la véritable cause du décès.</p>
        <p>Le temps presse... La vérité ne tient qu'à un fil.</p>
      </ComicEnigmePanel>
    </div>
  </div>
</template>

<script setup>
import ComicEnigmePanel from '../../../ComicEnigmePanel.vue'
import bgImageImport from '../../../../assets/comics/strip_reseau.png'

const emit = defineEmits(['finish'])

function skipIntro() {
  emit('finish')
}
</script>

<style scoped>
.intro-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: #000;
  overflow: hidden;
  z-index: 10000;
  display: flex;
  flex-direction: column;
}

.bd-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #111;
}
</style>
