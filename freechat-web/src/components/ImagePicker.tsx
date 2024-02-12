import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { PhotoCameraRounded, SvgIconComponent } from "@mui/icons-material";
import { Button, DialogActions, DialogContent, DialogTitle, IconButton, IconButtonProps, Input, Modal, ModalClose, ModalDialog, Stack, styled } from "@mui/joy";
import { extractFilenameFromUrl } from "../libs/url_utils";
import { DEFAULT_IMAGE_MAX_WIDTH } from "../libs/ui_utils";

interface ImagePreviewProps {
  src: string;
  width: string | number;
  height: string | number;
  borderRadius: string | number;
}

const ImagePreview = styled('div')<ImagePreviewProps>(({ src, width, height, borderRadius }) => ({
  width: width,
  height: height,
  backgroundImage: `url(${src})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  borderRadius: borderRadius,
}));

interface ImagePickerProps extends IconButtonProps{
  onImageSelect: (file: Blob, name: string) => void;
  previewProps?: {
    width?: string | number;
    height?: string | number;
    borderRadius?: string | number;
  };
  Icon?: SvgIconComponent;
}

export default function ImagePicker(props: ImagePickerProps) {
  const {onImageSelect, previewProps, Icon = PhotoCameraRounded, ...iconButtonProps } = props;

  const { t } = useTranslation('button');
  const [image, setImage] = useState<string | undefined>();
  const [imageSize, setImageSize] = useState<{width: number, height: number} | null>(null);
  const [file, setFile] = useState<Blob | null>(null);
  const [open, setOpen] = useState(false);

  const preview = { width: 'auto', height: 'auto', borderRadius: 0, ...previewProps };

  useEffect(() => {
    if (image) {
      const img = new Image();
      img.onload = () => {
        let newWidth = img.width;
        let newHeight = img.height;
        if (newWidth > DEFAULT_IMAGE_MAX_WIDTH) {
          newWidth = DEFAULT_IMAGE_MAX_WIDTH;
          newHeight = img.height * DEFAULT_IMAGE_MAX_WIDTH / img.width;
        }
        setImageSize({ width: newWidth, height: newHeight });
      };
      img.src = image;
    }
  }, [image]);

  function handleImageChange(event: React.ChangeEvent<HTMLInputElement>): void {
    const filePath = event.target.files && event.target.files[0];
    if (filePath) {
      setFile(filePath);
      setImage(URL.createObjectURL(filePath));
      // getCompressedImageDataURL(filePath)
      //   .then(setImage);
      setOpen(true);
    }
  }


  function handleClose(_event: React.MouseEvent<HTMLButtonElement>, reason: string): void {
    if (reason !== 'backdropClick') {
      setOpen(false);
    }
  }

  function handleModify() {
    if (typeof document !== 'undefined') {
      document.getElementById("image-upload-input")?.click();
    }
  }

  function handleConfirm() {
    file && image && onImageSelect(file, extractFilenameFromUrl(image));
    setOpen(false);
  }


  return (
    <>
      <Input
        type="file"
        id="image-upload-input"
        onChange={handleImageChange}
        sx={{ display: 'none' }}
        slotProps={{ input: {
          accept: "image/*"
        }}}
      />
      <label htmlFor="image-upload-input">
        <IconButton
          onClick={handleModify}
          {...iconButtonProps}
        >
          <Icon />
        </IconButton>
      </label>

      <Modal
        open={open}
        onClose={handleClose}
      >
        <ModalDialog>
          <ModalClose />
          <DialogTitle>{t('Choose a picture')}</DialogTitle>
          <DialogContent>
            <Stack spacing={2} sx={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center'
            }}>
              {image && <ImagePreview
                src={image}
                width={preview.width === 'auto' ? imageSize?.width ?? '200px' : preview.width}
                height={preview.height === 'auto' ? imageSize?.height ?? '200px' : preview.height}
                borderRadius={preview.borderRadius}
              />}
            </Stack>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleConfirm}>{t('Submit')}</Button>
            <Button onClick={handleModify}>{t('Modify')}</Button>
          </DialogActions>
        </ModalDialog>
      </Modal>
    </>
  );
}