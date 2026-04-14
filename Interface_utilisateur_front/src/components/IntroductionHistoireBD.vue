<template>
  <div class="comic-container">
    
    <!-- Panneau 1 : L'avocat -->
    <div class="comic-panel">
      <img src="@/assets/comics/panel_lawyer.png" alt="Mr Laborde Avocat" class="panel-bg" />
      <div class="speech-bubble bubble-left">
        Bonjour, je suis Mr Laborde, l'avocat de la famille Calvin.<br>
        J'ai besoin de gens capables de mener une enquête rapide et fiable.<br>
        Cela tombe bien, car d'après ce qu'on m'a dit, vous êtes le/la meilleur(e).
      </div>
    </div>

    <!-- Panneau 2 : Le Tribunal -->
    <div class="comic-panel">
      <img src="@/assets/comics/panel_court.png" alt="Le Tribunal" class="panel-bg" />
      <div class="speech-bubble bubble-right">
        Dans quelques jours, le procès opposant la famille Calvin au Dr Deckard ainsi que son assistante Emma va débuter.<br>
        Malheureusement, il y a encore de nombreuses zones d'ombre et certains détails importants nous échappent.
      </div>
    </div>

    <!-- Panneau 3 : L'Hôpital -->
    <div class="comic-panel">
      <img src="@/assets/comics/panel_hospital.png" alt="L'hôpital" class="panel-bg" />
      <div class="speech-bubble bubble-center">
        Voici un rappel des faits : Mme Calvin est entrée à l’hôpital de Castres pour des difficultés respiratoires.<br>
        C'est le Dr Deckard qui s'est occupé d'elle. Après une série d'examens, il a demandé à son assistante Emma de prendre le relais et placer Mme Calvin sous traitement à base d'Ibuprofène.
      </div>
    </div>

    <!-- Panneau 4 : La Dispute -->
    <div class="comic-panel">
      <img src="@/assets/comics/panel_argument.png" alt="La Dispute" class="panel-bg" />
      <div class="speech-bubble bubble-bottom-left">
        Cependant, Emma a changé le traitement et Mme Calvin est décédée.<br>
        Le Dr Deckard a immédiatement accusé son assistante de faute grave. De son côté, Emma rejette l'accusation.<br>
        Nous ignorons si nous devons poursuivre le Dr Deckard ou son assistante.
      </div>
    </div>

    <!-- Panneau 5 : Le Robot (Loi Batty) -->
    <div class="comic-panel">
      <img src="@/assets/comics/panel_robot.png" alt="Robot de la Loi Batty" class="panel-bg" />
      <div class="speech-bubble bubble-top-right">
        Je vous rappelle également que dans quelques mois, la très controversée loi Batty doit être votée.<br>
        Elle accordera aux IA médicales le droit de prendre des décisions sans la validation d'un humain.<br>
        Dans ce contexte tendu, vous n'avez pas le droit à l'erreur !
      </div>
    </div>

    <!-- Action pour commencer -->
    <div class="action-panel">
      <p class="final-instruction">
        Voici un questionnaire qui devrait vous permettre de déterminer le responsable. Vous devez vous positionner clairement.
      </p>
      <button class="btn-start" @click="$emit('commencer')">
        COMMENCER L'ENQUÊTE
      </button>
    </div>

  </div>
</template>

<script setup>
defineEmits(['commencer'])
</script>

<style scoped>
.comic-container {
  display: flex;
  flex-direction: column;
  gap: 30px;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  background-color: #111;
}

.comic-panel {
  position: relative;
  width: 100%;
  border: 5px solid #fff;
  border-radius: 8px;
  overflow: hidden;
  min-height: 400px;
  box-shadow: 8px 8px 0px rgba(0, 255, 255, 0.5), -8px -8px 0px rgba(255, 0, 255, 0.5);
  background: #000;
  transition: transform 0.3s ease;
}

.comic-panel:hover {
  transform: scale(1.02);
  z-index: 10;
}

.panel-bg {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
  opacity: 0.85;
}

/* Styles Communs des Bulles */
.speech-bubble {
  position: absolute;
  background: white;
  color: black;
  border: 3px solid black;
  border-radius: 20px;
  padding: 15px 25px;
  font-family: 'Comic Sans MS', 'Chalkboard SE', sans-serif;
  font-size: 1.1rem;
  font-weight: bold;
  line-height: 1.4;
  max-width: 60%;
  box-shadow: 4px 4px 0px rgba(0,0,0,1);
  z-index: 2;
}

/* Queues de bulles */
.speech-bubble::before {
  content: '';
  position: absolute;
  width: 0;
  height: 0;
  border: 15px solid transparent;
}

/* Positionnement spécifique */
.bubble-left {
  top: 10%;
  left: 5%;
}
.bubble-left::before {
  border-top-color: black;
  border-right-color: black;
  bottom: -30px;
  left: 30px;
}

.bubble-right {
  bottom: 10%;
  right: 5%;
}
.bubble-right::before {
  border-top-color: black;
  border-left-color: black;
  bottom: -30px;
  right: 30px;
}

.bubble-center {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.bubble-bottom-left {
  bottom: 10%;
  left: 5%;
}
.bubble-bottom-left::before {
  border-top-color: black;
  border-right-color: black;
  top: -30px;
  left: 30px;
  border-bottom-color: transparent !important;
}

.bubble-top-right {
  top: 10%;
  right: 5%;
}
.bubble-top-right::before {
  border-bottom-color: black;
  border-left-color: black;
  top: -30px;
  right: 30px;
}

.action-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  border: 2px dashed #0cebeb;
}

.final-instruction {
  font-size: 1.3rem;
  color: white;
  text-align: center;
  margin-bottom: 25px;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.8);
}

.btn-start {
  background: linear-gradient(135deg, #ff0f7b, #f89b29);
  color: white;
  border: 4px solid white;
  padding: 15px 40px;
  border-radius: 50px;
  font-size: 1.5rem;
  font-weight: 900;
  text-transform: uppercase;
  cursor: pointer;
  box-shadow: 0 10px 20px rgba(255, 15, 123, 0.4);
  transition: all 0.2s ease;
  letter-spacing: 2px;
}

.btn-start:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 15px 25px rgba(255, 15, 123, 0.6);
}

.btn-start:active {
  transform: translateY(2px);
}

@media (max-width: 768px) {
  .speech-bubble {
    font-size: 0.95rem;
    max-width: 85%;
    position: relative;
    top: auto; left: auto; right: auto; bottom: auto;
    transform: none;
    margin: 20px auto;
  }
  .bubble-center {
    transform: none;
  }
}
</style>
