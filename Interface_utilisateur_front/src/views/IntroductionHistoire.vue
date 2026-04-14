<template>
  <div class="intro-page">
    <div class="mode-toggle">
      <button :class="{ active: mode === 'classique' }" @click="setMode('classique')">Mode Texte</button>
      <button :class="{ active: mode === 'bd' }" @click="setMode('bd')">Mode Bande Dessinée</button>
    </div>

    <!-- Mode Classique -->
    <div v-if="mode === 'classique'" class="glass-panel story-container">
      <h2>Le Briefing</h2>
      
      <div class="story-content">
        <p>
          Bonjour, je suis Mr Laborde, l'avocat de la famille Calvin. J'ai besoin de gens capables de mener une enquête rapide et fiable. Cela tombe bien, car d'après ce qu'on m'a dit, vous êtes le/la meilleur(e).
        </p>
        <p>
          Dans quelques jours, soit le <strong>{{ dynamicDate }}</strong>, le procès numéro 756 B opposant la famille Calvin que je représente au Dr Deckard ainsi que son assistante Emma va débuter. Malheureusement, il y a encore de nombreuses zones d'ombre et certains détails importants nous échappent.
        </p>
        <p>
          Voici un petit rappel des faits : Mme Calvin est entrée à l’hôpital de Castres pour des difficultés respiratoires (toux, essoufflements, courbatures) associées à de la fièvre et des maux de tête. C'est le Dr Deckard qui s'est occupé d'elle. Après une série d'examens, il a demandé à son assistante Emma de prendre le relais et placer Mme Calvin sous traitement à base d'Ibuprofène. Cependant, Emma a changé le traitement et Mme Calvin est décédée. Le Dr Deckard a immédiatement accusé son assistante de faute grave ayant entraîné la mort. Il estime qu'Emma a délibérément modifié le traitement initialement prescrit. De son côté, Emma rejette l'accusation et considère qu’elle n’a commis aucune erreur.
        </p>
        <p>
          Suite à l'autopsie de Mme Calvin, il est clair qu'une erreur a été commise, mais nous ignorons si nous devons poursuivre le Dr Deckard ou son assistante. Voici un questionnaire qui, une fois rempli, devrait vous permettre de déterminer qui est responsable du décès de Mme Calvin (donner le questionnaire). Nous avons besoin d'une réponse claire. Vous devez vous positionner : les deux ou aucun des deux ne sont pas des réponses acceptables.
        </p>
        <p>
          Je vous rappelle également que dans quelques mois, un projet de loi très controversé doit être voté : la loi Batty. Elle accorde aux IA médicales le droit de prendre des décisions sans la vérification ni la validation d'un être humain. Vous conviendrez donc que dans ce contexte tendu qui concerne les décisions médicales, vous n'avez pas le droit à l'erreur !
        </p>
      </div>

      <button @click="commencerJeu" class="btn-start">
        Commencer
      </button>
    </div>

    <!-- Mode Bande Dessinée -->
    <IntroductionHistoireBD v-else @commencer="commencerJeu" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import IntroductionHistoireBD from '../components/IntroductionHistoireBD.vue'

const router = useRouter()
const mode = ref('bd') // Défaut sur la BD car plus ludique

const setMode = (newMode) => {
  mode.value = newMode
}

// Calculate dynamic date (today + 4 days, year 2034)
const dynamicDate = computed(() => {
  const date = new Date()
  date.setDate(date.getDate() + 4) // Ajouter quelques jours
  
  const day = String(date.getDate()).padStart(2, '0')
  const month = String(date.getMonth() + 1).padStart(2, '0')
  
  return `${day}-${month} 2034`
})

const commencerJeu = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.intro-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 2rem;
  width: 100%;
}

.mode-toggle {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  background: rgba(15, 23, 42, 0.6);
  padding: 0.5rem;
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 100;
  margin-top: 1rem;
}

.mode-toggle button {
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.5);
  padding: 0.8rem 1.5rem;
  border-radius: 20px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.mode-toggle button:hover {
  color: white;
}

.mode-toggle button.active {
  background: #22d3ee;
  color: #0f172a;
  box-shadow: 0 0 15px rgba(34, 211, 238, 0.4);
}

/* Anciens styles préservés */
.story-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 3rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: justify;
  line-height: 1.6;
}

h2 {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: #22d3ee;
  text-align: center;
}

.story-content {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.05rem;
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

strong {
  color: #fbbf24;
  font-weight: 800;
}

.btn-start {
  background: linear-gradient(to right, #22d3ee, #a855f7);
  color: white;
  border: none;
  padding: 1rem 3rem;
  border-radius: 30px;
  font-size: 1.2rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  margin-top: 1rem;
}

.btn-start:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(168, 85, 247, 0.4);
}

.btn-start:active {
  transform: translateY(0);
}
</style>
