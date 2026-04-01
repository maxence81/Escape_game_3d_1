import fs from 'fs';
import path from 'path';

const enigmesDir = path.join(process.cwd(), 'Interface_utilisateur_front', 'src', 'components', 'enigmes');

function processFile(filePath, enigmaName) {
  let content = fs.readFileSync(filePath, 'utf8');
  let originalContent = content;

  content = content.replace(/['"]\/model\/(.*?)['"]/g, `'/enigmes/${enigmaName}/model/$1'`);

  if (content !== originalContent) {
    fs.writeFileSync(filePath, content);
  }
}

function traverseDir(dir, enigmaName) {
  for (const file of fs.readdirSync(dir)) {
    const fullPath = path.join(dir, file);
    if (fs.statSync(fullPath).isDirectory()) {
      traverseDir(fullPath, enigmaName);
    } else if (file.endsWith('.js') || file.endsWith('.vue')) {
      processFile(fullPath, enigmaName);
    }
  }
}

const enigmas = fs.readdirSync(enigmesDir);
for (const enigma of enigmas) {
  traverseDir(path.join(enigmesDir, enigma), enigma);
}
console.log('Fixed model imports.');
