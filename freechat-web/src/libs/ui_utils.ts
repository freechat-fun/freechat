import { useEffect, useState } from 'react';

export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}

export const DEFAULT_IMAGE_MAX_WIDTH = 512;
export const DEFAULT_IMAGE_MAX_SIZE = 20000;

export function getCompressedImageDataURL(
  file: Blob,
  maxSize: number = DEFAULT_IMAGE_MAX_SIZE,
  maxWidth: number = DEFAULT_IMAGE_MAX_WIDTH
): Promise<string> {
  let currentQuality = 0.8;

  const compressImage = (file: Blob, quality: number): Promise<string> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        const img = new Image();
        img.onload = () => {
          const canvas = document.createElement('canvas');
          if (img.width > maxWidth) {
            canvas.width = maxWidth;
            canvas.height = img.height * maxWidth / img.width;
          } else {
            canvas.width = img.width;
            canvas.height = img.height;
          }
          const ctx = canvas.getContext('2d');
          ctx?.drawImage(img, 0, 0, canvas.width, canvas.height);
          const dataUrl = canvas.toDataURL('image/jpeg', quality);
          resolve(dataUrl);
        };
        img.onerror = reject;
        img.src = e.target!.result as string;
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  };

  const checkSizeAndCompress = (base64: string): Promise<string> => {
    if (base64.length < maxSize) {
      return Promise.resolve(base64);
    } else {
      if (currentQuality > 0.01) {
        currentQuality *= 0.8;
        return compressImage(file, currentQuality).then(checkSizeAndCompress);
      } else {
        return Promise.reject(new Error(`Unable to compress the image to less than ${maxSize}`));
      }
    }
  };

  return compressImage(file, currentQuality).then(checkSizeAndCompress);
}