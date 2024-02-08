import { useState } from "react";
import { useTranslation } from "react-i18next";
import { PhotoCamera } from "@mui/icons-material";
import { Button, DialogActions, DialogContent, DialogTitle, IconButton, IconButtonProps, Input, Modal, ModalClose, ModalDialog, Stack, styled } from "@mui/joy";
import { extractFilenameFromUrl } from "../libs/url_utils";

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
}

export default function ImagePicker(props: ImagePickerProps) {
  const {onImageSelect, previewProps, ...iconButtonProps } = props;

  const { t } = useTranslation('button');
  const [image, setImage] = useState<string | undefined>();
  const [file, setFile] = useState<Blob | null>(null);
  const [open, setOpen] = useState(false);

  const preview = { width: '200px', height: '200px', borderRadius: '5%', ...previewProps };

  function handleImageChange(event: React.ChangeEvent<HTMLInputElement>): void {
    const filePath = event.target.files && event.target.files[0];
    if (filePath) {
      setFile(filePath);
      setImage(URL.createObjectURL(filePath));
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
          <PhotoCamera />
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
                width={preview.width}
                height={preview.height}
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