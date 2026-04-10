<template>
  <div class="intro-container">
    <div class="bd-container">
      <ComicEnigmePanel 
        title="Enigme 4: Le Dispensaire" 
        :bgImage="bgImageImport" 
        @start="skipIntro"
      >
        <p>Vos investigations vous mènent finalement au dispensaire médical de l'hôpital.</p>
        <p>C'est dans ce lieu hautement sécurisé que les traitements de Mme Calvin ont été préparés et délivrés.</p>
        <p>Trouvez l'accès au système de l'ordinateur principal pour accéder au dossier de Mme Calvin.</p>
        <p>Fouillez les registres, analysez le dossier de Mme Calvin et complétez le mini jeu.</p>
        <p>Les ours en peluche et le pingouin seront vos pires ennemis...</p>
      </ComicEnigmePanel>
    </div>
  </div>
</template>

<script setup>
import ComicEnigmePanel from '../../../ComicEnigmePanel.vue'
import bgImageImport from '../../../../assets/comics/strip_pharmacie.png'

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
