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
