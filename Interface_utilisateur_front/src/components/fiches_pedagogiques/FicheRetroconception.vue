<template>
  <div class="glass-panel fiche-container">
    <div class="fiche-header">
      <span class="badge domaine">Domaine : Cybersécurité</span>
      <span class="badge thematique">Thématique : Rétroconception SQL</span>
    </div>
    
    <h2>Bilan de la 4ème Salle</h2>
    
    <div class="intro-text">
      <p>
        Dans la pharmacie, vous avez exploité des documents médicaux et utilisé un terminal pour retracer la chaîne de prescription, comprendre les équivalences médicamenteuses et identifier une erreur médicale.
      </p>
    </div>

    <div class="concepts-grid">
      <div class="concept-card">
        <div class="icon-wrapper">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#22d3ee" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
            <path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"></path>
            <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"></path>
          </svg>
        </div>
        <div class="concept-content">
          <h3>Bases de Données (Relationnelles)</h3>
          <p>Le registre utilisé est un exemple concret de BDD : un code pathologie pointe vers une alternative de traitement, elle-même liée à un code de médicament aboutissant à nom précis (sa DCI).</p>
        </div>
      </div>

      <div class="concept-card">
        <div class="icon-wrapper">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#a855f7" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="8" y1="6" x2="21" y2="6"></line>
            <line x1="8" y1="12" x2="21" y2="12"></line>
            <line x1="8" y1="18" x2="21" y2="18"></line>
            <line x1="3" y1="6" x2="3.01" y2="6"></line>
            <line x1="3" y1="12" x2="3.01" y2="12"></line>
            <line x1="3" y1="18" x2="3.01" y2="18"></line>
          </svg>
        </div>
        <div class="concept-content">
          <h3>Nomenclatures Médicales</h3>
          <p>L'utilisation de codes normalisés (comme la CIM-10 : J10 pour la grippe) garantit que tous les systèmes d'un hôpital parlent le même langage diagnostique sans erreur d'interprétation.</p>
        </div>
      </div>

      <div class="concept-card">
        <div class="icon-wrapper">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ec4899" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="21 8 21 21 3 21 3 8"></polyline>
            <rect x="1" y="3" width="22" height="5"></rect>
            <line x1="10" y1="12" x2="14" y2="12"></line>
          </svg>
        </div>
        <div class="concept-content">
          <h3>L'Interopérabilité</h3>
          <p>Croiser le dossier d'autopsie avec la base médicamenteuse démontre l'importance de l'interopérabilité : si deux logiciels ne croisent pas parfaitement ces données, l'humain court un risque vital.</p>
        </div>
      </div>
    </div>

    <!-- Le bouton 'Continuer' émet un événement que le binôme pourra écouter -->
    <button @click="$emit('continue')" class="btn-continue">
      Terminer et Continuer
    </button>
  </div>
</template>

<script setup>
defineEmits(['continue'])
</script>

<style scoped>
.fiche-container {
  width: 100%;
  max-width: 850px;
  margin: 2rem auto;
  padding: 3rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(45, 27, 84, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}
.fiche-header {
  display: flex; gap: 1rem; margin-bottom: 2rem; width: 100%; justify-content: flex-start; flex-wrap: wrap;
}
.badge {
  padding: 0.4rem 1rem; border-radius: 20px; font-size: 0.85rem; font-weight: 700; color: white;
}
.badge.domaine { background: rgba(34, 211, 238, 0.2); border: 1px solid rgba(34, 211, 238, 0.4); color: #22d3ee; }
.badge.thematique { background: rgba(168, 85, 247, 0.2); border: 1px solid rgba(168, 85, 247, 0.4); color: #d8b4fe; }
h2 {
  font-size: 2.2rem; font-weight: 700; color: white; margin-bottom: 1.5rem; text-align: center; text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}
.intro-text {
  font-size: 1.1rem; color: rgba(255, 255, 255, 0.9); line-height: 1.6; text-align: center; margin-bottom: 3rem; padding: 0 2rem;
}
.concepts-grid {
  display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem; width: 100%; margin-bottom: 3rem;
}
.concept-card {
  background: rgba(0, 0, 0, 0.2); border: 1px solid rgba(255, 255, 255, 0.05); border-radius: 12px; padding: 1.5rem; display: flex; flex-direction: column; align-items: flex-start; transition: transform 0.2s;
}
.concept-card:hover { transform: translateY(-5px); background: rgba(255, 255, 255, 0.05); }
.icon-wrapper { background: rgba(255, 255, 255, 0.05); padding: 1rem; border-radius: 12px; margin-bottom: 1rem; }
.concept-content h3 { font-size: 1.1rem; font-weight: 600; color: white; margin-bottom: 0.5rem; }
.concept-content p { font-size: 0.95rem; color: rgba(255, 255, 255, 0.7); line-height: 1.5; }
.btn-continue {
  background: linear-gradient(to right, #22d3ee, #a855f7); color: white; border: none; padding: 1rem 3rem; border-radius: 30px; font-size: 1.1rem; font-weight: 600; cursor: pointer; transition: all 0.2s ease; box-shadow: 0 4px 15px rgba(168, 85, 247, 0.3);
}
.btn-continue:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(168, 85, 247, 0.5); }
.btn-continue:active { transform: translateY(0); }
</style>
