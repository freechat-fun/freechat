export async function exportCharacter(characterId: number) {
  const a = document.createElement('a');
  a.href = `/api/v1/character/export/${characterId}`;
  document.body.appendChild(a);
  a.click();

  document.body.removeChild(a);
}
