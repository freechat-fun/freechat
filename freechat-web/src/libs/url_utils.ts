
export function extractFilenameFromUrl(url: string): string {
  try {
    const parsedUrl = new URL(url);
    const pathname = parsedUrl.pathname;
    const filename = pathname.substring(pathname.lastIndexOf('/') + 1);
    return filename;
  } catch (error) {
    console.error('Invalid URL:', error);
    return '';
  }
}
