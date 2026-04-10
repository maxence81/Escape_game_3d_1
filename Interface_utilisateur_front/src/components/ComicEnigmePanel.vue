<template>
  <div class="comic-wrapper">
    <div class="comic-panel-container">
      <!-- Title Box -->
      <div class="title-box">
        <h2>{{ title }}</h2>
      </div>
      
      <!-- Background Image (Generated purely visual, NO hallucinatory text) -->
      <img :src="bgImage" alt="Scène" class="panel-bg" />
      
      <!-- Exact User Text Overlaid -->
      <div class="text-box center-floating">
        <slot></slot>
      </div>

      <button @click="$emit('start')" class="btn-start-comic">
        Commencer l'énigme
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  bgImage: { type: String, required: true }
})
defineEmits(['start'])
</script>

<style scoped>
.comic-wrapper {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 1rem;
  background-color: #fce7f3; /* Fond léger pop-art */
  border-radius: 12px;
  box-shadow: inset 0 0 20px rgba(0,0,0,0.1);
  display: flex;
  justify-content: center;
}

.comic-panel-container {
  position: relative;
  width: 100%;
  background: #000;
  border: 5px solid #fff;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 450px;
  outline: 4px solid #111;
}

.panel-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.8;
  transition: transform 0.8s ease;
}

.comic-panel-container:hover .panel-bg {
  transform: scale(1.05);
}

.title-box {
  position: absolute;
  top: 10px;
  left: 10px;
  background: #fff;
  border: 3px solid #000;
  padding: 6px 15px;
  box-shadow: 4px 4px 0 #000;
  z-index: 10;
}

.title-box h2 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 800;
  color: #000;
  text-transform: uppercase;
}

.center-floating {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  max-width: 600px;
  background: rgba(255, 255, 255, 0.95);
  border: 4px solid #000;
  padding: 20px;
  box-shadow: 6px 6px 0 #000;
  border-radius: 4px;
  z-index: 10;
}

.center-floating :deep(p) {
  font-size: 1.1rem;
  font-weight: 700;
  color: #111;
  text-align: center;
  line-height: 1.5;
  margin-bottom: 12px;
  font-family: inherit;
}
.center-floating :deep(p:last-child) {
  margin-bottom: 0;
}

.btn-start-comic {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #ff0f7b, #f89b29);
  color: white;
  border: 3px solid #000;
  border-radius: 40px;
  padding: 12px 30px;
  font-size: 1.3rem;
  font-weight: 900;
  text-transform: uppercase;
  cursor: pointer;
  box-shadow: 4px 4px 0 #000;
  transition: all 0.2s ease;
  z-index: 20;
}

.btn-start-comic:hover {
  transform: translateX(-50%) translate(-2px, -2px);
  box-shadow: 6px 6px 0 #000;
}

.btn-start-comic:active {
  transform: translateX(-50%) translate(2px, 2px);
  box-shadow: 2px 2px 0 #000;
}

@media (max-width: 768px) {
  .center-floating { width: 90%; padding: 15px; }
  .center-floating :deep(p) { font-size: 0.9rem; }
  .btn-start-comic { font-size: 1rem; padding: 10px 20px; }
  .title-box { top: -2px; left: -2px; border-width: 2px; box-shadow: 2px 2px 0 #000; }
}
</style>
