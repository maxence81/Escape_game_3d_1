export function notifyEnigmaCompleted(success = true, enigmaId = null, hintsUsed = null) {
  const id = enigmaId || window.enigmaId || null
  const timeSeconds = getTimerSeconds()

  // ✅ NUEVO: Recuperamos las pistas usadas (las pasamos por parámetro o las leemos de localStorage)
  const hints = hintsUsed !== null ? hintsUsed : parseInt(localStorage.getItem('currentEnigmaHints') || '0');

  const message = {
    type: 'ENIGMA_COMPLETED',
    enigmaId: id,
    success: success,
    timeSeconds: timeSeconds,
    hintsUsed: hints // Enviamos las pistas al parent
  }

  // Envoyer au parent (si dans un iframe)
  if (window.parent && window.parent !== window) {
    window.parent.postMessage(message, '*')
  }  
  // Envoyer aussi à la fenêtre courante pour le mode SFC
  window.postMessage(message, '*')
  
  // Stocker dans localStorage pour récupération
  localStorage.setItem('lastEnigmaResult', JSON.stringify({
    ...message,
    timestamp: Date.now(),
  }))

  console.log('[Enigme] Résultat envoyé:', message)

  // Limpiar el contador de pistas para la siguiente sala
  localStorage.removeItem('currentEnigmaHints');
}

/**
 * Récupère le nombre de secondes depuis le timer global
 */
function getTimerSeconds() {
  if (window.getTimerValue) {
    const timeStr = window.getTimerValue() // format "MM:SS"
    const parts = timeStr.split(':')
    if (parts.length === 2) {
      return parseInt(parts[0]) * 60 + parseInt(parts[1])
    }
  }
  // Fallback: calculer depuis le localStorage
  const startTime = localStorage.getItem('enigmaStartTime')
  if (startTime) {
    return Math.floor((Date.now() - parseInt(startTime)) / 1000)
  }
  return 0
}