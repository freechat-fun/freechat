import { useEffect, useState, Dispatch, SetStateAction } from 'react';
import { TransitionStatus } from 'react-transition-group';

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

export const defaultTransitionSetting = `opacity 200ms ease-in-out`;
export const defaultTransitionInterval = 100;
export const transitionStyles: {
  [key in TransitionStatus]?: React.CSSProperties;
} = {
  entering: { opacity: 0 },
  entered: { opacity: 1 },
  exiting: { opacity: 0 },
  exited: { opacity: 0 },
};

export function initTransitionSequence(
  setShowItems: Dispatch<SetStateAction<boolean>>,
  setShowItemsFinish?: Dispatch<SetStateAction<boolean>>,
  itemCount: number = 1,
  delay: number = 200,
  interval: number = defaultTransitionInterval
): () => void {
  if (typeof window === 'undefined') {
    return () => {};
  }

  const timeouts: number[] = [];

  timeouts.push(
    window.setTimeout(() => {
      setShowItems(true);
    }, delay)
  );

  if (setShowItemsFinish) {
    timeouts.push(
      window.setTimeout(
        () => {
          setShowItemsFinish(true);
        },
        delay + itemCount * interval
      )
    );
  }

  return () => timeouts.forEach(window.clearTimeout);
}

export const DEFAULT_TEXT_MAX_WIDTH = 720;
export const DEFAULT_IMAGE_MAX_WIDTH = 512;
export const DEFAULT_IMAGE_MAX_SIZE = 20 * 1024;

export type ImageInfo = { dataUrl: string; blob: Blob };

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
            canvas.height = (img.height * maxWidth) / img.width;
          } else {
            canvas.width = img.width;
            canvas.height = img.height;
          }
          const ctx = canvas.getContext('2d');
          ctx?.drawImage(img, 0, 0, canvas.width, canvas.height);
          const dataUrl = canvas.toDataURL('image/jpeg', quality);
          canvas.toBlob(
            (blob) => {
              if (blob) {
                resolve({ dataUrl, blob });
              } else {
                reject(new Error('Compression failed'));
              }
            },
            'image/jpeg',
            quality
          );
        };
        img.onerror = reject;
        img.src = e.target!.result as string;
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  };

  const checkSizeAndCompress = ({
    dataUrl,
    blob,
  }: ImageInfo): Promise<ImageInfo> => {
    if (dataUrl.length < maxSize) {
      return Promise.resolve({ dataUrl, blob });
    } else {
      if (currentQuality > 0.01) {
        currentQuality *= 0.8;
        return compressImage(file, currentQuality).then(checkSizeAndCompress);
      } else {
        return Promise.reject(
          new Error(`Unable to compress the image to less than ${maxSize}`)
        );
      }
    }
  };

  return compressImage(file, currentQuality).then(checkSizeAndCompress);
}

export function processBackground(
  imageUrl: string | undefined,
  mode: string = 'dark',
  opacity: number = 0.3
): Promise<string> {
  return new Promise((resolve, reject) => {
    if (!imageUrl) {
      resolve('');
      return;
    }
    const image = new Image();
    image.crossOrigin = 'Anonymous';

    image.onload = () => {
      const canvas = document.createElement('canvas');
      canvas.width = image.width;
      canvas.height = image.height;

      const ctx = canvas.getContext('2d');
      if (!ctx) {
        resolve('');
        return;
      }

      ctx.drawImage(image, 0, 0);

      ctx.fillStyle = `rgba(${mode === 'dark' ? '0 0 0' : '255 255 255'} / ${1 - opacity})`;
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      canvas.style.filter = 'blur(10px)';

      resolve(canvas.toDataURL());
    };

    image.onerror = (error) => {
      reject(error);
    };

    image.src = imageUrl;

    if (image.complete || image.complete === undefined) {
      image.src =
        'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';
      image.src = imageUrl;
    }
  });
}
