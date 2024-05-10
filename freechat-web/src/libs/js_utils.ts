import { useEffect } from 'react';

export function useDynamicScript(src: string, onLoadCallback: () => void) {
  useEffect(() => {
    const existingScript = document.querySelector(`script[src="${src}"]`);
    if (existingScript) {
      onLoadCallback();
      return;
    }

    const script = document.createElement('script');
    script.src = src;

    script.addEventListener('load', onLoadCallback);

    script.addEventListener('error', () => {
      console.error(`Script load error for ${src}`);
    });

    document.body.appendChild(script);

    return () => {
      script.removeEventListener('load', onLoadCallback);
      script.removeEventListener('error', () => {
        console.error(`Script load error for ${src}`);
      });
      if (script.parentNode) {
        script.parentNode.removeChild(script);
      }
    };
  }, [src, onLoadCallback]);
}

export function escapeRegExp(str: string): string {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

export function arraysEqual<T>(a: T[], b: T[]): boolean {
  if (a.length !== b.length) return false;

  for (let i = 0; i < a.length; i++) {
    if (a[i] !== b[i]) return false;
  }

  return true;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function objectsEqual(a: any, b: any): boolean {
  if (Array.isArray(a) && Array.isArray(b)) {
    return arraysEqual(a, b);
  } else {
    return a === b;
  }
}