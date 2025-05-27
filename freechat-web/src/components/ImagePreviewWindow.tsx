/* eslint-disable prettier/prettier */
import { PropsWithChildren } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
} from '@mui/material';
import { Done as DoneIcon } from '@mui/icons-material';
import ImagePreview, { ImagePreviewProps } from './ImagePreview';

type ImagePreviewWindowProps = ImagePreviewProps & {
  title?: string;
  open?: boolean;
  setOpen?: (open: boolean) => void;
};

const ImagePreviewWindow: React.FC<
  PropsWithChildren<ImagePreviewWindowProps>
> = (props) => {
  const { t } = useTranslation();

  const {
    title = t('Image preview'),
    open = false,
    setOpen = () => {},
    ...others
  } = props;

  function handleClose(
    _event: React.MouseEvent<HTMLButtonElement>,
    reason: string
  ): void {
    if (reason !== 'backdropClick') {
      setOpen(false);
    }
  }

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="md"
    >
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <ImagePreview {...others} />
      </DialogContent>
      <DialogActions>
        <IconButton onClick={() => setOpen(false)}>
          <DoneIcon />
        </IconButton>
      </DialogActions>
    </Dialog>
  );
};

export default ImagePreviewWindow;
