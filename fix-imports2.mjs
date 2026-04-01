import fs from 'fs';
import path from 'path';

const enigmesDir = path.join(process.cwd(), 'Interface_utilisateur_front', 'src', 'components', 'enigmes');

function processFile(filePath) {
  let content = fs.readFileSync(filePath, 'utf8');
  let originalContent = content;

  // Fix old cross-project absolute paths
  content = content.replace(/\.\.\/\.\.\/\.\.\/\.\.\/Interface_utilisateur_front\/src\//g, '@/');
  content = content.replace(/\.\.\/\.\.\/\.\.\/Interface_utilisateur_front\/src\//g, '@/');
  content = content.replace(/\.\.\/\.\.\/Interface_utilisateur_front\/src\//g, '@/');

  if (content !== originalContent) {
    fs.writeFileSync(filePath, content);
  }
}

function traverseDir(dir) {
  for (const file of fs.readdirSync(dir)) {
    const fullPath = path.join(dir, file);
    if (fs.statSync(fullPath).isDirectory()) {
      traverseDir(fullPath);
    } else if (file.endsWith('.js') || file.endsWith('.vue')) {
      processFile(fullPath);
    }
  }
}

traverseDir(enigmesDir);
console.log('Fixed deep cross-project imports.');
