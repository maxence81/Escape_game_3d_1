<template>
  <div class="intro-container">
    <div class="bd-container">
      <ComicEnigmePanel 
        title="Enigme 3: La Chambre de Mme Calvin" 
        :bgImage="bgImageImport" 
        @start="skipIntro"
      >
        <p>La chambre d'hôpital de Mme Calvin est la dernière zone qu'elle a occupée avant son décès.</p>
        <p>Il est crucial d'examiner attentivement ce lieu pour comprendre ce qui lui est arrivé.</p>
        <p>Cherchez des indices dissimulés, décryptez le dossier médical et recomposez les pièces du puzzle.</p>
        <p>Soyez perspicaces, la moindre erreur de jugement pourrait tout fausser...</p>
      </ComicEnigmePanel>
    </div>
  </div>
</template>

<script setup>
import ComicEnigmePanel from '../../../ComicEnigmePanel.vue'
import bgImageImport from '../../../../assets/comics/strip_chambre.png'

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
