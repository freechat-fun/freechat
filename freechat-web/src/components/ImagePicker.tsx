/* eslint-disable prettier/prettier */
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  CloseRounded,
  DoneRounded,
  PhotoCameraRounded,
  SvgIconComponent,
  UndoRounded,
} from '@mui/icons-material';
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  IconButtonProps,
  Input,
  Stack,
  Typography,
} from '@mui/material';
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
    if (file && image) {
      onImageSelect(file, extractFilenameFromUrl(image));
    }
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
        inputProps={{
          ref: inputRef,
          accept: 'image/jpeg, image/png, image/gif, image/bmp, image/tiff, image/webp, .jpg, .jpeg, .png, .gif, .bmp, .tiff, .webp',
        }}
      />
      <label htmlFor={inputId}>
        <IconButton onClick={handleModify} {...iconButtonProps} size="small">
          <Icon />
        </IconButton>
      </label>

      <Dialog 
        open={open} 
        onClose={handleClose}
        maxWidth="sm"
      >
        <DialogTitle
          sx={{
            px: 2,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          <Typography variant="h6" sx={{ mr: 2 }}>
            {t('Choose a picture')}
          </Typography>
          <IconButton
            aria-label="close"
            onClick={(event) => handleClose(event, 'escapeKeyDown')}
          >
            <CloseRounded />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <Stack
            sx={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              mx: 2,
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
      </Dialog>
    </>
  );
}
