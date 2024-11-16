export function extractFilenameFromUrl(url: string | undefined): string {
  if (!url) {
    return '';
  }

  try {
    const parsedUrl = new URL(url);
    const pathname = parsedUrl.pathname;
    const filename = pathname.substring(pathname.lastIndexOf('/') + 1);
    return filename;
  } catch (error) {
    console.error('Invalid URL:', error);
    return url;
  }
}

export function base64PathDecode(key: string | undefined): string {
  if (!key) {
    return '';
  }

  let base64 = key.replace(/-/g, '+').replace(/_/g, '/');
  base64 = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');

  try {
    const base64decoded = atob(base64);
    const utf8decoder = new TextDecoder('utf-8');
    const uint8array = new Uint8Array(
      [...base64decoded].map((c) => c.charCodeAt(0))
    );
    return utf8decoder.decode(uint8array);
  } catch (e) {
    console.error('Failed to decode base64url string', e);
    return key;
  }
}
