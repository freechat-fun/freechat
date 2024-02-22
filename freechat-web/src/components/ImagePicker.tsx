import { useState } from "react";
import { useTranslation } from "react-i18next";
import { DoneRounded, PhotoCameraRounded, SvgIconComponent, UndoRounded } from "@mui/icons-material";
import { DialogActions, DialogContent, DialogTitle, IconButton, IconButtonProps, Input, Modal, ModalClose, ModalDialog, Stack } from "@mui/joy";
import { extractFilenameFromUrl } from "../libs/url_utils";
import { ImagePreview } from ".";

type ImagePickerProps = IconButtonProps & {
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
  const [file, setFile] = useState<Blob | null>(null);
  const [open, setOpen] = useState(false);

  const preview = { width: 'auto', height: 'auto', borderRadius: 0, ...previewProps };

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
                width={preview.width !== 'auto' ? preview.width : undefined}
                height={preview.height !== 'auto' ? preview.height : undefined}
                borderRadius={preview.borderRadius}
              />}
            </Stack>
          </DialogContent>
          <DialogActions>
            <IconButton onClick={handleConfirm}><DoneRounded /></IconButton>
            <IconButton onClick={handleModify}><UndoRounded /></IconButton>
          </DialogActions>
        </ModalDialog>
      </Modal>
    </>
  );
}