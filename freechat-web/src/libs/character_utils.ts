export function characterExportUrl(
  characterId: number,
  includeConnectors = false
): string {
  return `/api/v2/character/export/${characterId}${includeConnectors ? '?includeConnectors=true' : ''}`;
}

function exportFilename(
  disposition: string | null,
  characterId: number
): string {
  const fallback = `character-${characterId}.tar.gz`;
  // Parse whole parameter values so quoted semicolons cannot introduce a filename.
  const parameters = Array.from(
    disposition?.matchAll(
      /;\s*([^=;\s]+)\s*=\s*(?:"((?:[^"\\]|\\.)*)"|([^;]*))/g
    ) ?? []
  );
  const parameter = (name: string) => {
    const match = parameters.find((part) => part[1].toLowerCase() === name);
    return match ? (match[2] ?? match[3]).trim() : undefined;
  };
  let filename = parameter('filename');
  const extended = parameter('filename*')?.match(/^UTF-8'[^']*'(.*)$/i);
  if (extended) {
    try {
      filename = decodeURIComponent(extended[1]);
    } catch {
      // Malformed extended names fall back to the plain name, if available.
    }
  }
  if (
    !filename ||
    filename.startsWith('.') ||
    filename !== filename.trim() ||
    /[\\/:*?"<>|\p{Cc}\p{Cf}\p{Cs}]/u.test(filename) ||
    new TextEncoder().encode(filename).length > 240 ||
    !/\.tar\.gz$/i.test(filename)
  ) {
    return fallback;
  }
  return filename;
}

export async function exportCharacter(
  characterId: number,
  includeConnectors = false
) {
  let status = -1;
  let a: HTMLAnchorElement | undefined;
  let objectUrl: string | undefined;
  try {
    const response = await fetch(
      characterExportUrl(characterId, includeConnectors),
      {
        credentials: 'same-origin',
        redirect: 'manual',
      }
    );
    status = response.status;
    if (
      response.type === 'opaqueredirect' ||
      response.redirected ||
      (status >= 300 && status < 400)
    ) {
      status = 401;
      throw new Error();
    }
    if (!response.ok) throw new Error();
    const contentType = response.headers
      .get('Content-Type')
      ?.split(';')[0]
      .trim()
      .toLowerCase();
    if (
      contentType !== 'application/gzip' &&
      contentType !== 'application/x-gzip'
    ) {
      status = 502;
      throw new Error();
    }

    const blob = await response.blob();
    objectUrl = URL.createObjectURL(blob);
    a = document.createElement('a');
    a.href = objectUrl;
    a.download = exportFilename(
      response.headers.get('Content-Disposition'),
      characterId
    );
    document.body.appendChild(a);
    a.click();
  } catch {
    // Preserve authentication/HTTP status without exposing response bodies or URLs.
    throw Object.assign(
      new Error('Character export failed. Please try again.'),
      {
        status,
      }
    );
  } finally {
    a?.remove();
    if (objectUrl) URL.revokeObjectURL(objectUrl);
  }
}
