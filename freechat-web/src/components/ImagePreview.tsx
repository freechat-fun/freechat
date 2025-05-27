import { useEffect, useState } from 'react';
import { Box } from '@mui/material';
import { DEFAULT_IMAGE_MAX_WIDTH } from '../libs/ui_utils';

export type ImagePreviewProps = {
  src: string;
  maxWidth?: string | number;
  maxHight?: string | number;
  borderRadius?: string | number;
};

export default function ImagePreview(props: ImagePreviewProps) {
  const { src, maxWidth, maxHight, borderRadius = 0 } = props;

  const [imageSize, setImageSize] = useState({
    width: maxWidth,
    height: maxHight,
  });

  useEffect(() => {
    if (src) {
      const img = new Image();
      img.onload = () => {
        let newWidth = img.width;
        let newHeight = img.height;
        if (newWidth > DEFAULT_IMAGE_MAX_WIDTH) {
          newWidth = DEFAULT_IMAGE_MAX_WIDTH;
          newHeight = (img.height * DEFAULT_IMAGE_MAX_WIDTH) / img.width;
        }
        setImageSize({ width: newWidth, height: newHeight });
      };
      img.src = src;

      if (img.complete || img.complete === undefined) {
        img.src =
          'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';
        img.src = src;
      }
    }
  }, [src]);

  return (
    <Box
      sx={{
        maxWidth: maxWidth,
        maxHeight: maxHight,
        width: imageSize.width,
        height: imageSize.height,
        backgroundImage: `url(${src})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        borderRadius: borderRadius,
      }}
    />
  );
}
