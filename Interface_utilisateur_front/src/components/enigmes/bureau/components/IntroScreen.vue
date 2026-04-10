<template>
  <div class="intro-container">
    <div class="bd-container">
      <ComicEnigmePanel 
        title="Enigme 2: Le Bureau du Dr Deckard" 
        :bgImage="bgImageImport" 
        @start="skipIntro"
      >
        <p>Vous venez d'entrer dans le bureau du Dr Deckard.</p>
        <p>Ce bureau renferme de nombreux secrets liés au mystère de la mort de Mme Calvin.</p>
        <p>Inspectez chaque recoin, observez l'environnement et fouillez ses dossiers personnels.</p>
        <p>Méfiez-vous des apparences, les réponses sont souvent dissimulées sous vos yeux.</p>
        <p>Le temps est compté... Accédez au serveur et découvrez la vérité.</p>
      </ComicEnigmePanel>
    </div>
  </div>
</template>

<script setup>
import ComicEnigmePanel from '../../../ComicEnigmePanel.vue'
import bgImageImport from '../../../../assets/comics/strip_bureau.png'

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
  flex: 1; display: flex; align-items: center; justify-content: center; background: #111;
}
</style>
