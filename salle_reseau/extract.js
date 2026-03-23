const fs = require('fs');

let vueFile = fs.readFileSync('src/components/Scene3D.vue', 'utf8');
let scriptMatches = vueFile.match(/<script setup>([\s\S]*?)<\/script>/);

if (!scriptMatches) { console.error('No script found'); process.exit(1); }

let rawScript = scriptMatches[1];

// We need to wrap it inside "export function useThreeScene(containerRef, props, emit) {"
// Remove "import { ref, onMounted, onBeforeUnmount, watch } from 'vue'"
rawScript = rawScript.replace(/import \{.*?\} from 'vue'/, "import { ref, watch } from 'vue'");

// Replace "const props = defineProps({ ... })"
rawScript = rawScript.replace(/const props = defineProps\(\{[\s\S]*?\}\)/, "");

// Replace "const emit = defineEmits([...])"
rawScript = rawScript.replace(/const emit = defineEmits\(\[.*?\]\)/, "");

// Replace "const containerRef = ref(null)"
rawScript = rawScript.replace(/const containerRef = ref\(null\)/, "");

// Change "onMounted(() => { initThree() })" 
rawScript = rawScript.replace(/onMounted\(\(\) => \{[\s\S]*?initThree\(\)[\s\S]*?\}\)/, "");

// Change "onBeforeUnmount( ... )" to assign to cleanup
rawScript = rawScript.replace(/onBeforeUnmount\(\(\) => \{([\s\S]*?)\}\)/, "const cleanup = () => {\}");

// Build the module file
let finalJs = "import { ref, watch } from 'vue';\n" + 
    "import * as THREE from 'three';\n" +
    "import { OrbitControls } from 'three/addons/controls/OrbitControls.js';\n" +
    "import { DragControls } from 'three/addons/controls/DragControls.js';\n" +
    "import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';\n" +
    "import { DRACOLoader } from 'three/addons/loaders/DRACOLoader.js';\n" +
    "import { KTX2Loader } from 'three/addons/loaders/KTX2Loader.js';\n" +
    "import { EffectComposer } from 'three/addons/postprocessing/EffectComposer.js';\n" +
    "import { RenderPass } from 'three/addons/postprocessing/RenderPass.js';\n" +
    "import { UnrealBloomPass } from 'three/addons/postprocessing/UnrealBloomPass.js';\n\n" +
    "export function useThreeScene(containerRef, props, emit) {\n" +
    rawScript.replace(/import .*? from 'three.*';/g, '') + "\n" +
    "  return { isLoading, loadingProgress, initThree, cleanup };\n}\n";

fs.writeFileSync('src/composables/useThreeScene.js', finalJs, 'utf8');

// Now overwrite Scene3D.vue
let newScene3D = <template>
  <div class="scene-wrapper">
    <div ref="containerRef" class="scene-container"></div>
    <SceneLoadingOverlay :isLoading="isLoading" :loadingProgress="loadingProgress" />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useThreeScene } from '../composables/useThreeScene'
import SceneLoadingOverlay from './SceneLoadingOverlay.vue'

const props = defineProps({
  wifiConnected: Boolean,
  safeOpened: Boolean
})

const emit = defineEmits(['onWifiConnected', 'onMonitorClick', 'onSafeClick'])

const containerRef = ref(null)

const { isLoading, loadingProgress, initThree, cleanup } = useThreeScene(containerRef, props, emit)

onMounted(() => {
  initThree()
})

onBeforeUnmount(() => {
  cleanup()
})
</script>

<style scoped>
.scene-wrapper { width: 100%; height: 100vh; position: relative; }
.scene-container { width: 100%; height: 100%; display: block; }
</style>;

fs.writeFileSync('src/components/Scene3D.vue', newScene3D, 'utf8');

console.log('Extraction complete');
