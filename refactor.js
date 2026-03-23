const fs = require('fs');

const useThreeSetup = \import { ref, watch } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import { DragControls } from 'three/addons/controls/DragControls.js'
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'
import { DRACOLoader } from 'three/addons/loaders/DRACOLoader.js'
import { KTX2Loader } from 'three/addons/loaders/KTX2Loader.js'
import { EffectComposer } from 'three/addons/postprocessing/EffectComposer.js'
import { RenderPass } from 'three/addons/postprocessing/RenderPass.js'
import { UnrealBloomPass } from 'three/addons/postprocessing/UnrealBloomPass.js'

export function useThreeScene(containerRef, props, emit) {
  const isLoading = ref(true)
  const loadingProgress = ref(0)

  let scene, camera, renderer, composer
  let orbitControls, dragControls
  let reqId
  let raycaster, mouse
  let interactableMeshes = []
  let objectsToDrag = []

  let wifiMesh = null
  let socketMesh = null
  let monitorMesh = null
  let safeMesh = null
  let safeDoorMesh = null
  let bloomPass

  const initThree = () => {
    if (!containerRef.value) return

    scene = new THREE.Scene()
    scene.background = new THREE.Color(0x050510)
    scene.fog = new THREE.FogExp2(0x050510, 0.05)

    camera = new THREE.PerspectiveCamera(60, window.innerWidth / window.innerHeight, 0.1, 100)
    camera.position.set(7, 3, 0)

    renderer = new THREE.WebGLRenderer({ antialias: false, powerPreference: "high-performance" })
    renderer.setSize(window.innerWidth, window.innerHeight)
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.2))
    renderer.shadowMap.enabled = false
    renderer.toneMapping = THREE.ACESFilmicToneMapping
    renderer.toneMappingExposure = 1.3
    containerRef.value.appendChild(renderer.domElement)

    const renderScene = new RenderPass(scene, camera)
    bloomPass = new UnrealBloomPass(new THREE.Vector2(window.innerWidth, window.innerHeight), 1.5, 0.4, 0.85)
    bloomPass.threshold = 0.95
    bloomPass.strength = 0.2
    bloomPass.radius = 0.5
    
    composer = new EffectComposer(renderer)
    composer.addPass(renderScene)
    composer.addPass(bloomPass)

    orbitControls = new OrbitControls(camera, renderer.domElement)
    orbitControls.enableDamping = true
    orbitControls.dampingFactor = 0.05
    orbitControls.minDistance = 1
    orbitControls.maxDistance = 10
    orbitControls.target.set(0, 1, 0)

    const ambientLight = new THREE.AmbientLight(0xffffff, 0.8)
    scene.add(ambientLight)

    const hemiLight = new THREE.HemisphereLight(0xffffff, 0x444444, 1.2)
    hemiLight.position.set(0, 10, 0)
    scene.add(hemiLight)

    const dirLightFront = new THREE.DirectionalLight(0xffffff, 1.0)
    dirLightFront.position.set(10, 10, 10)
    scene.add(dirLightFront)

    const dirLightBack = new THREE.DirectionalLight(0xffffff, 0.6)
    dirLightBack.position.set(-10, 10, -10)
    scene.add(dirLightBack)

    const gltfLoader = new GLTFLoader()
    const dracoLoader = new DRACOLoader()
    dracoLoader.setDecoderPath('/draco/')
    gltfLoader.setDRACOLoader(dracoLoader)

    const ktx2Loader = new KTX2Loader()
    ktx2Loader.setTranscoderPath('/basis/')
    ktx2Loader.detectSupport(renderer)
    gltfLoader.setKTX2Loader(ktx2Loader)

    gltfLoader.load(
      '/model/reseau_st001_compressed.glb',
      (gltf) => {
        const model = gltf.scene
        model.traverse((child) => {
          if (child.isMesh) {
            const name = child.name.toLowerCase()
            interactableMeshes.push(child)

            if (name.includes('wifi')) wifiMesh = child
            if (name.includes('socket')) socketMesh = child
            if (name.includes('cylinder003_13')) monitorMesh = child
            if (name.includes('safe') ) {
               safeMesh = child
               if (name.includes('door') || name.includes('porte')) safeDoorMesh = child
            }
          }
        })
        scene.add(model)
        postLoadSetup(true)
      },
      (xhr) => {
        if (xhr.total > 0) loadingProgress.value = Math.round((xhr.loaded / xhr.total) * 100)
      },
      (err) => {
        console.warn("Model /model/salle_reseau.glb non trouvé, génération d'une scène factice", err)
        injectPlaceholders()
        postLoadSetup(false)
      }
    )

    raycaster = new THREE.Raycaster()
    mouse = new THREE.Vector2()
    window.addEventListener('resize', onWindowResize)
    window.addEventListener('click', onClick)

    animate()
  }

  function injectPlaceholders() {
    const roomGeo = new THREE.BoxGeometry(10, 5, 10)
    const roomMat = new THREE.MeshStandardMaterial({ color: 0x111111, side: THREE.BackSide, roughness: 0.1, metalness: 0.9 })
    const room = new THREE.Mesh(roomGeo, roomMat)
    room.position.set(0, 2.5, 0)
    scene.add(room)

    socketMesh = new THREE.Mesh(new THREE.BoxGeometry(0.5, 0.5, 0.1), new THREE.MeshStandardMaterial({color: 0x333333, roughness:0.5, metalness:0.5}))
    socketMesh.name = "socket"
    socketMesh.position.set(2, 1, -4.9)
    scene.add(socketMesh)

    wifiMesh = new THREE.Mesh(new THREE.BoxGeometry(0.4, 0.2, 0.3), new THREE.MeshStandardMaterial({color: 0x00ffff, emissive: 0x00ffff, emissiveIntensity: 0.5}))
    wifiMesh.name = "wifi"
    wifiMesh.position.set(0, 1, 0)
    scene.add(wifiMesh)

    monitorMesh = new THREE.Mesh(new THREE.PlaneGeometry(2, 1), new THREE.MeshStandardMaterial({color: 0x111111, emissive: 0xff0000, emissiveIntensity:0.5}))
    monitorMesh.name = "monitor"
    monitorMesh.position.set(-2, 1.5, -4.9)
    scene.add(monitorMesh)

    const safeGroup = new THREE.Group()
    safeGroup.position.set(4, 0.5, 0)
    safeGroup.name = "safe_group"
    
    const safeBody = new THREE.Mesh(new THREE.BoxGeometry(1, 1, 1), new THREE.MeshStandardMaterial({color: 0x444444, metalness: 1.0, roughness: 0.2}))
    safeBody.name = "safe_body"
    safeGroup.add(safeBody)
    
    safeDoorMesh = new THREE.Mesh(new THREE.BoxGeometry(0.1, 0.9, 0.9), new THREE.MeshStandardMaterial({color: 0x666666, metalness: 1.0, roughness: 0.2}))
    safeDoorMesh.position.set(-0.45, 0, 0)
    safeDoorMesh.name = "safe_door"
    safeGroup.add(safeDoorMesh)

    scene.add(safeGroup)
    safeMesh = safeGroup

    interactableMeshes.push(socketMesh, wifiMesh, monitorMesh, safeBody, safeDoorMesh)
  }

  function postLoadSetup(hasRealModel) {
    isLoading.value = false
    
    if (wifiMesh) {
      objectsToDrag.push(wifiMesh)
      dragControls = new DragControls(objectsToDrag, camera, renderer.domElement)
      
      dragControls.addEventListener('dragstart', function (event) {
        orbitControls.enabled = false
        if (event.object.material) event.object.material.emissiveIntensity = 1.0
      })
      
      dragControls.addEventListener('drag', function(event) {
         if (socketMesh) {
            const dist = event.object.position.distanceTo(socketMesh.position)
            if (dist < 1.0) {
               event.object.position.copy(socketMesh.position)
               event.object.position.z += 0.2
               emit('onWifiConnected')
               dragControls.enabled = false
               orbitControls.enabled = true
               if (event.object.material) event.object.material.emissive.setHex(0x00ff00)
            }
         }
      })

      dragControls.addEventListener('dragend', function (event) {
        orbitControls.enabled = true
        if (props.wifiConnected && event.object.material) {
           event.object.material.emissive.setHex(0x00ff00)
        } else if (event.object.material) {
           event.object.material.emissiveIntensity = 0.5
        }
      })
    }

    updateMonitorState()
  }

  function updateMonitorState() {
    if (monitorMesh && monitorMesh.material) {
       if (props.wifiConnected) {
           monitorMesh.material.emissive.setHex(0x00ffff)
           monitorMesh.material.emissiveIntensity = 1.0
       } else {
           monitorMesh.material.emissive.setHex(0xff0000)
           monitorMesh.material.emissiveIntensity = 0.5
       }
    }
  }

  watch(() => props.wifiConnected, () => {
     updateMonitorState()
  })

  watch(() => props.safeOpened, (newVal) => {
     if (newVal) {
        const target = safeDoorMesh || safeMesh
        if (target) {
           const startR = target.rotation.y
           const targetR = startR + Math.PI / 2
           const duration = 1000
           const startTime = performance.now()
           
           function animDoor(time) {
              const elaps = time - startTime
              const t = Math.min(elaps / duration, 1)
              target.rotation.y = startR + (targetR - startR) * t
              if (t < 1) requestAnimationFrame(animDoor)
           }
           requestAnimationFrame(animDoor)
        }
     }
  })

  const onWindowResize = () => {
    if (!camera || !renderer || !composer) return
    camera.aspect = window.innerWidth / window.innerHeight
    camera.updateProjectionMatrix()
    renderer.setSize(window.innerWidth, window.innerHeight)
    composer.setSize(window.innerWidth, window.innerHeight)
  }

  const onClick = (event) => {
    if (!camera || !scene || !raycaster) return
    if (event.target.tagName !== 'CANVAS') return

    const rect = event.target.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;

    mouse.x = (x / rect.width) * 2 - 1;
    mouse.y = -(y / rect.height) * 2 + 1;

    raycaster.setFromCamera(mouse, camera)
    const intersects = raycaster.intersectObjects(interactableMeshes, true)

    if (intersects.length > 0) {
      const clickedOb = intersects[0].object
      console.log("Objet cliqué (débogage) :", clickedOb.name, clickedOb)

      let current = clickedOb
      let clickedName = ""
      while (current && current !== scene) {
         clickedName += current.name.toLowerCase()
         current = current.parent
      }

      if (clickedName.includes('monitor') || clickedName.includes('ecran') || clickedName.includes('pc') || clickedName.includes('ordinateur') || clickedName.includes('screen') || clickedName.includes('display') || clickedName.includes('cylinder003_13')) {
         emit('onMonitorClick')
      } else if (clickedName.includes('safe'))  {
         emit('onSafeClick')
      }
    }
  }

  const animate = () => {
    reqId = requestAnimationFrame(animate)
    if (orbitControls) orbitControls.update()
    if (composer) {
      composer.render()
    } else if (renderer && scene && camera) {
      renderer.render(scene, camera)
    }
  }

  const cleanup = () => {
    if (reqId) cancelAnimationFrame(reqId)
    window.removeEventListener('resize', onWindowResize)
    window.removeEventListener('click', onClick)
    if (renderer) renderer.dispose()
  }

  return { isLoading, loadingProgress, initThree, cleanup }
}\;

const loadingOverlayVue = \<template>
  <div v-if="isLoading" class="loading-overlay">
    <h2>INITIALISATION DES SYSTEMES... {{ loadingProgress }}%</h2>
    <p>Chargement des modèles 3D & shaders de post-traitement...</p>
  </div>
</template>

<script setup>
defineProps({
  isLoading: Boolean,
  loadingProgress: Number
})
</script>

<style scoped>
.loading-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: black;
  color: magenta;
  z-index: 100;
  font-family: 'Courier New', Courier, monospace;
}
.loading-overlay h2 {
  text-shadow: 0 0 10px magenta;
  font-size: 2rem;
}
</style>\;

const scene3DVue = \<template>
  <div class="scene-wrapper">
    <!-- Le canvas Three.js est injecté ici -->
    <div ref="containerRef" class="scene-container"></div>
    
    <!-- L'écran de chargement rendu conditionnellement -->
    <SceneLoadingOverlay 
      :isLoading="isLoading" 
      :loadingProgress="loadingProgress" 
    />
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

// Extraction de toute la logique 3D complexe vers un composable
const { isLoading, loadingProgress, initThree, cleanup } = useThreeScene(containerRef, props, emit)

onMounted(() => {
  initThree()
})

onBeforeUnmount(() => {
  cleanup()
})
</script>

<style scoped>
.scene-wrapper {
  position: relative;
  width: 100%;
  height: 100vh;
}
.scene-container {
  width: 100%;
  height: 100%;
  display: block;
}
</style>\;

fs.writeFileSync('salle_reseau/src/composables/useThreeScene.js', useThreeSetup, 'utf8');
fs.writeFileSync('salle_reseau/src/components/SceneLoadingOverlay.vue', loadingOverlayVue, 'utf8');
fs.writeFileSync('salle_reseau/src/components/Scene3D.vue', scene3DVue, 'utf8');

console.log("Refactoring complete");
