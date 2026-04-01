import fs from 'fs';
import path from 'path';

const enigmesDir = path.join(process.cwd(), 'Interface_utilisateur_front', 'src', 'components', 'enigmes');
const enigmes = fs.readdirSync(enigmesDir);

function processFile(filePath, enigmaName) {
  let content = fs.readFileSync(filePath, 'utf8');
  let originalContent = content;

  content = content.replace(/(src|image\-src|iframe\-src|href)=(['"])\/(.*?)\2/g, `$1=$2/enigmes/${enigmaName}/$3$2`);
  content = content.replace(/\.load\(['"]\/(.*?)['"]/g, `.load('/enigmes/${enigmaName}/$1'`);
  content = content.replace(/value \= ['"]\/(.*?)['"]/g, `value = '/enigmes/${enigmaName}/$1'`);
  
  // Custom case for specific assignments
  content = content.replace(/computerSrc\.value = ['"]\/(.*?)['"]/g, `computerSrc.value = '/enigmes/${enigmaName}/$1'`);

  const relativeFromRoot = path.relative(path.dirname(filePath), path.join(enigmesDir, enigmaName));
  const prefix = relativeFromRoot === '' ? './' : relativeFromRoot.replace(/\\/g, '/') + '/';
  
  content = content.replace(/['"]@\/(.*?)['"]/g, `"${prefix}$1"`);

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

for (const enigma of enigmes) {
  traverseDir(path.join(enigmesDir, enigma), enigma);
}
console.log('Fixed imports successfully.');
