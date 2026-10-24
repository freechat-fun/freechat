export function characterExportUrl(
  characterId: number,
  includeConnectors = false
): string {
  return `/api/v2/character/export/${characterId}${includeConnectors ? '?includeConnectors=true' : ''}`;
}

export async function exportCharacter(
  characterId: number,
  includeConnectors = false
) {
  const a = document.createElement('a');
  a.href = characterExportUrl(characterId, includeConnectors);
  document.body.appendChild(a);
  a.click();

  document.body.removeChild(a);
}
