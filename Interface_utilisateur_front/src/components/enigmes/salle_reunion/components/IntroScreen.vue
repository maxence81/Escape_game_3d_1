<template>
  <div class="intro-container">
    <div class="bd-container">
      <ComicEnigmePanel 
        title="Enigme 5: La Salle de Réunion" 
        :bgImage="bgImageImport" 
        @start="skipIntro"
      >
        <p>Vous avez rassemblé toutes les pièces du puzzle au cours de vos précédentes investigations.</p>
        <p>Vous voici dans la salle de réunion, le lieu de toutes les décisions.</p>
        <p>Tout indique qu'une erreur a été commise par un de vos collègues. Qui est le véritable responsable ?</p>
        <p>L'heure est venue de confronter les suspects et de faire éclater la vérité.</p>
        <p>La vérité sur la mort de Mme Calvin est entre vos mains.</p>
      </ComicEnigmePanel>
    </div>
  </div>
</template>

<script setup>
import ComicEnigmePanel from '../../../ComicEnigmePanel.vue'
import bgImageImport from '../../../../assets/comics/strip_reunion.png'

const emit = defineEmits(['finish'])

function skipIntro() {
  emit('finish')
}
</script>

<style scoped>
.intro-container {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: #000; overflow: hidden; z-index: 10000;
  display: flex; flex-direction: column;
}
.bd-container {
  flex: 1; display: flex; align-items: center; justify-content: center;
  background: #000 url('https://www.transparenttextures.com/patterns/stardust.png') repeat top center;
}
</style>
