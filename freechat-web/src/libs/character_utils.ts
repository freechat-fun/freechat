const MISSING_DOCUMENTS_ERROR_CODE = 'CHARACTER_EXPORT_DOCUMENTS_MISSING';

type MissingCharacterDocumentsError = Error & {
  status: 409;
  errorCode: typeof MISSING_DOCUMENTS_ERROR_CODE;
};

export function isMissingCharacterDocumentsError(
  reason: unknown
): reason is MissingCharacterDocumentsError {
  return (
    reason instanceof Error &&
    'status' in reason &&
    reason.status === 409 &&
    'errorCode' in reason &&
    reason.errorCode === MISSING_DOCUMENTS_ERROR_CODE
  );
}

export function characterExportUrl(
  characterId: number,
  includeConnectors = false,
  force = false
): string {
  const params = new URLSearchParams();
  if (includeConnectors) params.set('includeConnectors', 'true');
  if (force === true) params.set('force', 'true');
  const query = params.toString();
  return `/api/v2/character/export/${characterId}${query ? `?${query}` : ''}`;
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
  includeConnectors = false,
  force = false
) {
  let status = -1;
  let errorCode: typeof MISSING_DOCUMENTS_ERROR_CODE | undefined;
  let a: HTMLAnchorElement | undefined;
  let objectUrl: string | undefined;
  try {
    const response = await fetch(
      characterExportUrl(characterId, includeConnectors, force),
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
    const contentType = response.headers
      .get('Content-Type')
      ?.split(';')[0]
      .trim()
      .toLowerCase();
    if (!response.ok) {
      if (status === 409 && contentType === 'application/json') {
        const payload: unknown = await response.json();
        if (
          payload !== null &&
          typeof payload === 'object' &&
          'errorCode' in payload &&
          payload.errorCode === MISSING_DOCUMENTS_ERROR_CODE
        ) {
          errorCode = MISSING_DOCUMENTS_ERROR_CODE;
        }
      }
      throw new Error();
    }
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
    // Only the allowlisted code and HTTP status may escape, never raw payloads.
    throw Object.assign(
      new Error('Character export failed. Please try again.'),
      {
        status,
        ...(errorCode ? { errorCode } : {}),
      }
    );
  } finally {
    a?.remove();
    if (objectUrl) URL.revokeObjectURL(objectUrl);
  }
}
