export async function exportCharacter(characterId: number) {
  const response = await fetch(`/api/v1/character/export/${characterId}`, {
    method: 'GET',
    headers: {
      'Accept': 'application/gzip',
    },
  });

  if (!response.ok) {
    throw new Error(`Error: ${response.statusText}`);
  }

  const blob = await response.blob();
  const contentDisposition = response.headers.get('Content-Disposition');
  let fileName = 'character.tar.gz';

  if (contentDisposition) {
    const matches = /filename="([^;]*)"/.exec(contentDisposition);
    if (matches && matches[1]) {
      fileName = decodeURIComponent(matches[1]);
    }
  }

  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = fileName;
  document.body.appendChild(a);
  a.click();

  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
}
