import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  DoneRounded,
  PhotoCameraRounded,
  SvgIconComponent,
  UndoRounded,
} from '@mui/icons-material';
import {
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  IconButtonProps,
  Input,
  Modal,
  ModalClose,
  ModalDialog,
  Stack,
} from '@mui/joy';
import { extractFilenameFromUrl } from '../libs/url_utils';
import { ImagePreview } from '.';

let idCounter = 0;

type ImagePickerProps = IconButtonProps & {
  onImageSelect: (file: Blob, name: string) => void;
  previewProps?: {
    maxWidth?: string | number;
    maxHeight?: string | number;
    borderRadius?: string | number;
  };
  Icon?: SvgIconComponent;
};

export default function ImagePicker(props: ImagePickerProps) {
  const {
    onImageSelect,
    previewProps,
    Icon = PhotoCameraRounded,
    ...iconButtonProps
  } = props;

  const { t } = useTranslation('button');
  const [image, setImage] = useState<string | undefined>();
  const [file, setFile] = useState<Blob | null>(null);
  const [open, setOpen] = useState(false);

  const inputId = useRef(`image-upload-input-${idCounter}`).current;
  const inputRef = useRef<HTMLInputElement | null>(null);

  const preview = {
    maxWidth: 'auto',
    maxHeight: 'auto',
    borderRadius: 0,
    ...previewProps,
  };

  useEffect(() => {
    idCounter++;
  }, []);

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

  function handleClose(
    _event: React.MouseEvent<HTMLButtonElement>,
    reason: string
  ): void {
    if (reason !== 'backdropClick') {
      setFile(null);
      setImage(undefined);
      setOpen(false);
    }
  }

  function handleModify() {
    if (inputRef.current) {
      inputRef.current.click();
    }
  }

  function handleConfirm() {
    file && image && onImageSelect(file, extractFilenameFromUrl(image));
    setFile(null);
    setImage(undefined);
    setOpen(false);
  }

  return (
    <>
      <Input
        type="file"
        id={inputId}
        onChange={handleImageChange}
        sx={{ display: 'none' }}
        slotProps={{
          input: {
            ref: inputRef,
            accept: 'image/*',
          },
        }}
      />
      <label htmlFor={inputId}>
        <IconButton onClick={handleModify} {...iconButtonProps}>
          <Icon />
        </IconButton>
      </label>

      <Modal open={open} onClose={handleClose}>
        <ModalDialog>
          <ModalClose />
          <DialogTitle>{t('Choose a picture')}</DialogTitle>
          <DialogContent>
            <Stack
              spacing={2}
              sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
              }}
            >
              {image && (
                <ImagePreview
                  src={image}
                  maxWidth={
                    preview.maxWidth !== 'auto' ? preview.maxWidth : undefined
                  }
                  maxHight={
                    preview.maxHeight !== 'auto' ? preview.maxHeight : undefined
                  }
                  borderRadius={preview.borderRadius}
                />
              )}
            </Stack>
          </DialogContent>
          <DialogActions>
            <IconButton onClick={handleConfirm}>
              <DoneRounded />
            </IconButton>
            <IconButton onClick={handleModify}>
              <UndoRounded />
            </IconButton>
          </DialogActions>
        </ModalDialog>
      </Modal>
    </>
  );
}
