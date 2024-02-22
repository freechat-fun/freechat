import { useEffect, useState } from "react";
import { Box } from "@mui/joy";
import { DEFAULT_IMAGE_MAX_WIDTH } from "../libs/ui_utils";

type ImagePreviewProps = {
  src: string;
  width?: string | number;
  height?: string | number;
  borderRadius?: string | number;
}

export default function ImagePreview(props: ImagePreviewProps) {
  const { src, width, height, borderRadius = 0 } = props;

  const [imageSize, setImageSize] = useState({ width: width, height: height });

  useEffect(() => {
    if (src && (!width || !height)) {
      const img = new Image();
      img.onload = () => {
        let newWidth = img.width;
        let newHeight = img.height;
        if (newWidth > DEFAULT_IMAGE_MAX_WIDTH) {
          newWidth = DEFAULT_IMAGE_MAX_WIDTH;
          newHeight = img.height * DEFAULT_IMAGE_MAX_WIDTH / img.width;
        }
        setImageSize({ width: width || newWidth, height: height || newHeight });
      };
      img.src = src;
    }
  }, [src, width, height]);
  
  return (
    <Box sx={{
      width: imageSize.width,
      height: imageSize.height,
      backgroundImage: `url(${src})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      borderRadius: borderRadius,
    }}/>
  );
}
