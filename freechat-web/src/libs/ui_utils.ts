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

export const DEFAULT_TEXT_MAX_WIDTH = 720;
export const DEFAULT_IMAGE_MAX_WIDTH = 512;
export const DEFAULT_IMAGE_MAX_SIZE = 20 * 1024;

export type ImageInfo = { dataUrl: string, blob: Blob };

export function getCompressedImage(
  file: Blob,
  maxSize: number = DEFAULT_IMAGE_MAX_SIZE,
  maxWidth: number = DEFAULT_IMAGE_MAX_WIDTH
): Promise<ImageInfo> {
  let currentQuality = 0.8;

  const compressImage = (file: Blob, quality: number): Promise<ImageInfo> => {
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
          canvas.toBlob(blob => {
            if (blob) {
              resolve({ dataUrl, blob });
            } else {
              reject(new Error('Compression failed'));
            }
          }, 'image/jpeg', quality);
        };
        img.onerror = reject;
        img.src = e.target!.result as string;
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  };

  const checkSizeAndCompress = ({dataUrl, blob}: ImageInfo): Promise<ImageInfo> => {
    if (dataUrl.length < maxSize) {
      return Promise.resolve({dataUrl, blob});
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