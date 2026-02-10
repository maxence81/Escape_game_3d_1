import * as THREE from 'three';
import './style.css';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader';
import { KTX2Loader } from 'three/examples/jsm/loaders/KTX2Loader';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';

const canvas = document.querySelector('.experience-canvas');

const sizes = {
  width: window.innerWidth,
  height: window.innerHeight
}
const scene = new THREE.Scene();
scene.background = new THREE.Color(0x87CEEB);

const camera = new THREE.PerspectiveCamera(
  75,
  sizes.width / sizes.height,
  0.1,
  1000
);
const renderer = new THREE.WebGLRenderer({ canvas: canvas, antialias: true });
renderer.shadowMap.enabled = true;
renderer.shadowMap.type = THREE.PCFSoftShadowMap;
renderer.toneMapping = THREE.ACESFilmicToneMapping;
renderer.toneMappingExposure = 1;
renderer.outputColorSpace = THREE.SRGBColorSpace;

renderer.setSize(sizes.width, sizes.height);
renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));

// Loaders
const textureLoader = new THREE.TextureLoader();
const dracoLoader = new DRACOLoader();
dracoLoader.setDecoderPath('https://www.gstatic.com/draco/versioned/decoders/1.5.6/');

const ktx2Loader = new KTX2Loader();
ktx2Loader.setTranscoderPath('https://cdn.jsdelivr.net/npm/three@0.160.0/examples/jsm/libs/basis/');
ktx2Loader.detectSupport(renderer);

const gltfLoader = new GLTFLoader();
gltfLoader.setDRACOLoader(dracoLoader);
gltfLoader.setKTX2Loader(ktx2Loader);

// Lights
const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
scene.add(ambientLight);

const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
directionalLight.position.set(5, 5, 5);
directionalLight.castShadow = true;
directionalLight.shadow.mapSize.width = 1024;
directionalLight.shadow.mapSize.height = 1024;
directionalLight.shadow.bias = -0.001;
scene.add(directionalLight);

//Lights for window
const windowLight = new THREE.PointLight(0xffffff, 40, 40);
windowLight.position.set(1, 2.5, -4);
scene.add(windowLight);

//Light for desk
const deskLight = new THREE.PointLight(0xffffff, 20, 20);
deskLight.position.set(-5.3, 2.5, 1);
scene.add(deskLight);

let isDeskLightOn = true;

const updateDeskLight = () => {
  if (!isDeskLightOn) {
    deskLight.intensity = 0;
  } else {
    deskLight.intensity = isNightMode ? 10 : 20;
  }
};

//Light for cat
const catLight = new THREE.PointLight(0xffffff, 5, 5);
catLight.position.set(7.5, 4, -2.5);
scene.add(catLight);

let isCatLightOn = true;

const updateCatLight = () => {
  if (!isCatLightOn) {
    catLight.intensity = 0;
  } else {
    catLight.intensity = isNightMode ? 10 : 20;
  }
};

// Fairy lights for night mode (manual positions)
const modelLights = [];

const raycasterDog = new THREE.Raycaster();
const mouseDog = new THREE.Vector2();
let dogObject = null;
//Skeleton overlay (completion form)
const skeletonOverlay = document.getElementById('skeleton-overlay');
const closeSkeletonBtn = document.getElementById('close-skeleton-btn');
const validateAnswersBtn = document.getElementById('validate-answers-btn');

function showSkeletonPictureInFrame() {
  skeletonOverlay.style.display = 'flex';
  controls.enabled = false;
}

function closeSkeletonPictureInFrame() {
  skeletonOverlay.style.display = 'none';
  controls.enabled = true;
}

function validateAnswers() {
  const answer1 = document.getElementById('answer1').value.toLowerCase().trim();
  const answer2 = document.getElementById('answer2').value.toLowerCase().trim();
  let correct1 = false, correct2 = false;

  if (answer1.includes('grippe')) {
    document.getElementById('feedback1').textContent = '✓ Correct !';
    document.getElementById('feedback1').style.color = '#50ef87';
    correct1 = true;
  } else {
    document.getElementById('feedback1').textContent = '✗ Incorrect';
    document.getElementById('feedback1').style.color = '#ff6b6b';
  }

  if (answer2.includes('cortisone')) {
    document.getElementById('feedback2').textContent = '✓ Correct !';
    document.getElementById('feedback2').style.color = '#50ef87';
    correct2 = true;
  } else {
    document.getElementById('feedback2').textContent = '✗ Incorrect';
    document.getElementById('feedback2').style.color = '#ff6b6b';
  }

  if (correct1 && correct2) {
    // Stop the timer and get final time
    if (window.stopTimer) window.stopTimer();
    const finalTime = window.getTimerValue ? window.getTimerValue() : '';

    document.getElementById('success-message').style.display = 'block';
    if (finalTime) {
      document.getElementById('success-message').innerHTML = `
        <h3 style="color: #50ef87; margin: 0 0 10px 0;">🎉 Félicitations !</h3>
        <p style="color: #fff; font-size: 13px; margin: 5px 0;">Vous avez terminé l'escape game avec succès !</p>
        <p style="color: #ffd700; font-size: 16px; margin-top: 10px;">⏱️ Temps : ${finalTime}</p>
      `;
    }
    validateAnswersBtn.style.display = 'none';
    localStorage.setItem('escapeGameCompleted', 'true');

    // Start confetti celebration for 10 seconds
    startConfetti();
  }
}

// Confetti celebration function
function startConfetti() {
  const duration = 10 * 1000; // 10 seconds
  const end = Date.now() + duration;

  const colors = ['#ff0000', '#00ff00', '#0000ff', '#ffff00', '#ff00ff', '#00ffff', '#ffd700', '#ff6b6b', '#50ef87'];

  (function frame() {
    confetti({
      particleCount: 5,
      angle: 60,
      spread: 55,
      origin: { x: 0 },
      colors: colors,
      zIndex: 10000
    });
    confetti({
      particleCount: 5,
      angle: 120,
      spread: 55,
      origin: { x: 1 },
      colors: colors,
      zIndex: 10000
    });

    if (Date.now() < end) {
      requestAnimationFrame(frame);
    }
  }());
}

// Close button event for skeleton
closeSkeletonBtn.addEventListener('click', closeSkeletonPictureInFrame);
validateAnswersBtn.addEventListener('click', validateAnswers);

// Dog overlay elements
const dogOverlay = document.getElementById('dog-overlay');
const dogIframe = document.getElementById('dog-iframe');
const closeDogBtn = document.getElementById('close-dog-btn');

function showDogPictureInFrame() {
  dogIframe.src = '/images/oslo.png';
  dogOverlay.style.display = 'flex';
  controls.enabled = false;
}

function closeDogPictureInFrame() {
  dogOverlay.style.display = 'none';
  dogIframe.src = '';
  controls.enabled = true;
}

// Close button event for dog
closeDogBtn.addEventListener('click', closeDogPictureInFrame);

// Close overlay when clicking outside the dog frame
dogOverlay.addEventListener('click', (event) => {
  if (event.target === dogOverlay) {
    closeDogPictureInFrame();
  }
});

// Close overlay with Escape key
window.addEventListener('keydown', (event) => {
  if (event.code === 'Escape' && dogOverlay.style.display === 'flex') {
    closeDogPictureInFrame();
  }
});
//Chest overlay(no pisture just frame)
const chestOverlay = document.getElementById('chest-overlay');
const chestIframe = document.getElementById('chest-iframe');
const closeChestBtn = document.getElementById('close-chest-btn');

function showChestPictureInFrame() {
  chestIframe.src = '/combo-lock/index.html';
  chestOverlay.style.display = 'flex';
  controls.enabled = false;
}

function closeChestPictureInFrame() {
  chestOverlay.style.display = 'none';
  chestIframe.src = '';
  controls.enabled = true;
}

// Close button event for dog
closeChestBtn.addEventListener('click', closeChestPictureInFrame);

// Close overlay when clicking outside the dog frame
chestOverlay.addEventListener('click', (event) => {
  if (event.target === chestOverlay) {
    closeChestPictureInFrame();
  }
});

// Close overlay with Escape key
window.addEventListener('keydown', (event) => {
  if (event.code === 'Escape' && chestOverlay.style.display === 'flex') {
    closeChestPictureInFrame();
  }
});
//Child draw overlay

const drawOverlay = document.getElementById('draw-overlay');
const drawIframe = document.getElementById('draw-iframe');
const closeDrawBtn = document.getElementById('close-draw-btn');

function showDrawPictureInFrame() {
  drawIframe.src = '/images/lea.png';
  drawOverlay.style.display = 'flex';
  controls.enabled = false;
}

function closeDrawPictureInFrame() {
  drawOverlay.style.display = 'none';
  drawIframe.src = '';
  controls.enabled = true;
}

// Close button event for dog
closeDrawBtn.addEventListener('click', closeDrawPictureInFrame);

// Close overlay when clicking outside the dog frame
drawOverlay.addEventListener('click', (event) => {
  if (event.target === drawOverlay) {
    closeDrawPictureInFrame();
  }
});

// Close overlay with Escape key
window.addEventListener('keydown', (event) => {
  if (event.code === 'Escape' && drawOverlay.style.display === 'flex') {
    closeDrawPictureInFrame();
  }
});

//Anniv overlay
const annivOverlay = document.getElementById('anniv-overlay');
const annivIframe = document.getElementById('anniv-iframe');
const closeAnnivBtn = document.getElementById('close-anniv-btn');

function showAnnivPictureInFrame() {
  annivIframe.src = '/images/anniv.png';
  annivOverlay.style.display = 'flex';
  controls.enabled = false;
}

function closeAnnivPictureInFrame() {
  annivOverlay.style.display = 'none';
  annivIframe.src = '';
  controls.enabled = true;
}

// Close button event for dog
closeAnnivBtn.addEventListener('click', closeAnnivPictureInFrame);

// Close overlay when clicking outside the dog frame
annivOverlay.addEventListener('click', (event) => {
  if (event.target === annivOverlay) {
    closeAnnivPictureInFrame();
  }
});

// Close overlay with Escape key
window.addEventListener('keydown', (event) => {
  if (event.code === 'Escape' && annivOverlay.style.display === 'flex') {
    closeAnnivPictureInFrame();
  }
});

// Raycaster for click detection
const raycaster = new THREE.Raycaster();
const mouse = new THREE.Vector2();
let computerObject = null;

// Computer overlay elements
const computerOverlay = document.getElementById('computer-overlay');
const computerIframe = document.getElementById('computer-iframe');
const closeComputerBtn = document.getElementById('close-computer-btn');

// Model
gltfLoader.load(
  '/model/ptut_chest_compressed.glb',
  (gltf) => {
    gltf.scene.traverse((child) => {
      if (child.isMesh) {
        child.castShadow = true;
        child.receiveShadow = true;
      }
      const name = child.name.toLowerCase();
      if (name.includes('computer') || name.includes('ordinateur') || name.includes('pc') || name.includes('ecran') || name.includes('screen') || name.includes('monitor')) {
        computerObject = child;
        console.log('Computer object found:', child.name);
      }

      if (name === 'plane001') {
        dogObject = child;
        console.log('Image object found:', child.name);
      }

      // Change color of Plane.017 and Plane.021 to match sky
      // Check for both "Plane017" and "Plane.017" formats just in case
      if (child.name === 'Plane017' || child.name === 'Plane.017' ||
        child.name === 'Plane021' || child.name === 'Plane.021') {

        console.log('Applying sky material to:', child.name);

        // Use MeshBasicMaterial to be unlit and match the background color exactly
        child.material = new THREE.MeshBasicMaterial({
          color: 0x87CEEB
        });
      }
    });
    scene.add(gltf.scene);

    // Log all object names to help identify the computer
    console.log('All objects in model:');
    gltf.scene.traverse((child) => {
      if (child.isMesh) {
        console.log('  -', child.name);
      }
    });

    // Create fairy lights manually at guirlande positions
    // 6 lights above the window + guirlande above desk
    const fairyLightPositions = [
      // Rangée au-dessus de la fenêtre (6 lumières)
      { x: -1.35, y: 5.47, z: -2.83 },
      { x: 0, y: 5.47, z: -2.83 },
      { x: 1.435, y: 5.47, z: -2.83 },
      { x: 2.98, y: 5.47, z: -2.83 },
      { x: 4.48, y: 5.47, z: -2.83 },
      { x: 6.1, y: 5.5, z: -2.85 },
      // Guirlande au-dessus du bureau (courbe descendante puis remontante)
      { x: -6.07, y: 3.67, z: -1.77 },
      { x: -5.6, y: 3.75, z: -1.935 },
      { x: -5.15, y: 3.95, z: -2.1 },
      { x: -4.72, y: 4.2, z: -2.3 },
      { x: -3.78, y: 4.15, z: -2.8 },
      { x: -3.29, y: 3.86, z: -2.78 },
      { x: -2.8, y: 3.8, z: -2.85 },

      //Guirlande à gauche du bureau
      { x: -6.3, y: 3.97, z: -0.4 },
      { x: -6.4, y: 4.1, z: 0.095 },
      { x: -6.47, y: 4.4, z: 1.49 },
      { x: -6.4, y: 4.2, z: 1.95 },
      { x: -6.4, y: 3.9, z: 2.4 },
      { x: -6.4, y: 3.8, z: 2.9 },
      { x: -6.4, y: 3.8, z: 3.45 },

      // Lumière pour éclairer le squelette 
      { x: 6.95, y: 3, z: -2.8 },
      { x: 6.95, y: 0, z: -2.8 },
    ];

    fairyLightPositions.forEach((pos) => {
      const pointLight = new THREE.PointLight(0xffeeaa, 0, 3);
      pointLight.position.set(pos.x, pos.y, pos.z);
      scene.add(pointLight);
      modelLights.push(pointLight);


    });

    console.log('Fairy lights created:', modelLights.length);
  },
  undefined,
  (error) => {
    console.error('An error happened', error);
  }
);

camera.position.set(8, 8, 8);
camera.lookAt(0, 0, 0);
const controls = new OrbitControls(camera, renderer.domElement);
controls.update();
controls.enableDamping = true;
controls.dampingFactor = 0.05;
controls.minAzimuthAngle = 0;
controls.maxAzimuthAngle = Math.PI / 2;
controls.maxPolarAngle = Math.PI / 2;

// Night Mode Logic
let isNightMode = false;
const nightModeBtn = document.getElementById('night-mode-btn');

// Create fixed position lights for night mode
const nightLights = [];
const lightPositions = [
  { x: -2, y: 2.8, z: 0, intensity: 15, distance: 8, color: 0xffcc66 },
  { x: 0, y: 2.8, z: 0, intensity: 15, distance: 8, color: 0xffcc66 },
  { x: 2, y: 2.8, z: 0, intensity: 15, distance: 8, color: 0xffcc66 },
  { x: -1.5, y: 1.2, z: 1.5, intensity: 20, distance: 6, color: 0xffeedd },
  { x: 3, y: 2, z: 0, intensity: 25, distance: 8, color: 0xffddaa },
  { x: 3.5, y: 1, z: 1, intensity: 20, distance: 6, color: 0xffeedd },
  { x: 0, y: 1.5, z: 2, intensity: 10, distance: 8, color: 0xffffff },
];

lightPositions.forEach((pos) => {
  const light = new THREE.PointLight(pos.color, 0, pos.distance);
  light.position.set(pos.x, pos.y, pos.z);
  scene.add(light);
  nightLights.push({ light, intensity: pos.intensity });
});

nightModeBtn.addEventListener('click', () => {
  isNightMode = !isNightMode;

  if (isNightMode) {
    scene.background = new THREE.Color(0x0a0a1a);
    directionalLight.intensity = 0.02;
    ambientLight.intensity = 0.15;
    ambientLight.color.setHex(0x2a2a4a);

    nightLights.forEach((item) => {
      item.light.intensity = item.intensity;
    });

    // Turn on fairy lights
    modelLights.forEach((light) => {
      light.intensity = 8;
    });

    updateDeskLight();
    updateCatLight();
  } else {
    scene.background = new THREE.Color(0x87CEEB);
    directionalLight.intensity = 1;
    ambientLight.intensity = 0.5;
    ambientLight.color.setHex(0xffffff);

    nightLights.forEach((item) => {
      item.light.intensity = 0;
    });

    // Turn off fairy lights
    modelLights.forEach((light) => {
      light.intensity = 0;
    });

    updateDeskLight();
    updateCatLight();
  }
});

// Event listeners
window.addEventListener('resize', () => {
  sizes.width = window.innerWidth;
  sizes.height = window.innerHeight;
  camera.aspect = sizes.width / sizes.height;
  camera.updateProjectionMatrix();
  renderer.setSize(sizes.width, sizes.height);
});

// Keyboard controls for movement
const keys = {
  forward: false,
  backward: false,
  left: false,
  right: false,
  up: false,
  down: false
};

const moveSpeed = 0.15;

window.addEventListener('keydown', (event) => {
  switch (event.code) {
    case 'KeyW':
    case 'KeyZ': // AZERTY
    case 'ArrowUp':
      keys.forward = true;
      break;
    case 'KeyS':
    case 'ArrowDown':
      keys.backward = true;
      break;
    case 'KeyA':
    case 'KeyQ': // AZERTY
    case 'ArrowLeft':
      keys.left = true;
      break;
    case 'KeyD':
    case 'ArrowRight':
      keys.right = true;
      break;
    case 'Space':
      keys.up = true;
      break;
  }
});

window.addEventListener('keyup', (event) => {
  switch (event.code) {
    case 'KeyW':
    case 'KeyZ':
    case 'ArrowUp':
      keys.forward = false;
      break;
    case 'KeyS':
    case 'ArrowDown':
      keys.backward = false;
      break;
    case 'KeyA':
    case 'KeyQ':
    case 'ArrowLeft':
      keys.left = false;
      break;
    case 'KeyD':
    case 'ArrowRight':
      keys.right = false;
      break;
    case 'Space':
      keys.up = false;
      break;
  }
});

const render = () => {
  // Handle keyboard movement
  const direction = new THREE.Vector3();
  camera.getWorldDirection(direction);

  const right = new THREE.Vector3();
  right.crossVectors(direction, camera.up).normalize();

  if (keys.forward) {
    camera.position.addScaledVector(direction, moveSpeed);
    controls.target.addScaledVector(direction, moveSpeed);
  }
  if (keys.backward) {
    camera.position.addScaledVector(direction, -moveSpeed);
    controls.target.addScaledVector(direction, -moveSpeed);
  }
  if (keys.left) {
    camera.position.addScaledVector(right, -moveSpeed);
    controls.target.addScaledVector(right, -moveSpeed);
  }
  if (keys.right) {
    camera.position.addScaledVector(right, moveSpeed);
    controls.target.addScaledVector(right, moveSpeed);
  }
  if (keys.up) {
    camera.position.y += moveSpeed;
    controls.target.y += moveSpeed;
  }
  if (keys.down) {
    camera.position.y -= moveSpeed;
    controls.target.y -= moveSpeed;
  }

  controls.update();
  renderer.render(scene, camera);
  window.requestAnimationFrame(render);
}

render();

// Computer interaction functions
function openComputerOverlay() {
  computerIframe.src = '/Cabinet Deckard/Enigme Cabinet Deckard.html';
  computerOverlay.style.display = 'flex';
  controls.enabled = false; // Disable camera controls when overlay is open
}

function closeComputerOverlay() {
  computerOverlay.style.display = 'none';
  computerIframe.src = '';
  controls.enabled = true; // Re-enable camera controls
}

// Close button event
closeComputerBtn.addEventListener('click', closeComputerOverlay);

// Close overlay when clicking outside the computer frame
computerOverlay.addEventListener('click', (event) => {
  if (event.target === computerOverlay) {
    closeComputerOverlay();
  }
});

// Close overlay with Escape key
window.addEventListener('keydown', (event) => {
  if (event.code === 'Escape' && computerOverlay.style.display === 'flex') {
    closeComputerOverlay();
  }
});

// Click detection on 3D objects
function onMouseClick(event) {
  // Calculate mouse position in normalized device coordinates (-1 to +1)
  mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
  mouse.y = -(event.clientY / window.innerHeight) * 2 + 1;

  // Update the raycaster with the camera and mouse position
  raycaster.setFromCamera(mouse, camera);

  // Calculate objects intersecting the ray
  const intersects = raycaster.intersectObjects(scene.children, true);

  if (intersects.length > 0) {
    const clickedObject = intersects[0].object;
    console.log('Clicked on:', clickedObject.name);

    // Check if clicked on computer or any of its parents
    let current = clickedObject;
    while (current) {
      const name = current.name.toLowerCase();
      if (name.includes('computer') || name.includes('ordinateur') || name.includes('pc') ||
        name.includes('ecran') || name.includes('screen') || name.includes('monitor')) {
        openComputerOverlay();
        return;
      }
      current = current.parent;
    }

    // If we have a stored computer object, check if it matches
    if (computerObject && (clickedObject === computerObject || clickedObject.parent === computerObject)) {
      openComputerOverlay();
    }

    // Check click on tablelight2
    if (clickedObject.name.toLowerCase().includes('tablelight2')) {
      isDeskLightOn = !isDeskLightOn;
      updateDeskLight();
      console.log('Toggled desk light:', isDeskLightOn);
    }

    // Check click on catlight
    if (clickedObject.name.toLowerCase().includes('torus') || clickedObject.name.toLowerCase().includes('sphere003')) {
      isCatLightOn = !isCatLightOn;
      updateCatLight();
      console.log('Toggled cat light:', isCatLightOn);
    }

    //check click on dog picture
    if (clickedObject.name.toLowerCase() === 'plane003_2') {
      showDogPictureInFrame();
    }

    //check click on draw picture
    if (clickedObject.name.toLowerCase() === 'béziercurve002_2') {
      showDrawPictureInFrame();
    }

    //check click on anniv picture
    if (clickedObject.name.toLowerCase() === 'sphere') {
      showAnnivPictureInFrame();
    }

    //check click on chest
    if (clickedObject.name.toLowerCase().includes('wooden') && clickedObject.name.toLowerCase().includes('chest')) {
      showChestPictureInFrame();
    }

    //check click on skeleton picture
    if (clickedObject.name.toLowerCase() === 'skeleton003') {
      showSkeletonPictureInFrame();
    }
  }
}

canvas.addEventListener('click', onMouseClick);