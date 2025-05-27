/* eslint-disable @typescript-eslint/no-explicit-any */
import { forwardRef } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Stack,
  IconButton,
  DialogProps,
  DialogContentText,
  Typography,
} from '@mui/material';
import { CloseRounded } from '@mui/icons-material';

interface ConfirmModalProps extends DialogProps {
  obj?: any;
  dialog?: {
    title?: string;
    color?: 'error' | 'info' | 'primary' | 'secondary' | 'success' | 'warning';
  };
  button?: {
    text?: string;
    color?: 'error' | 'info' | 'primary' | 'secondary' | 'success' | 'warning';
    startIcon?: React.ReactNode;
  };
  onConfirm?: (obj: any) => void;
  children: React.ReactNode;
}

const ConfirmModal = forwardRef<HTMLDivElement, ConfirmModalProps>(
  (props, ref) => {
    const {
      obj,
      dialog,
      button,
      onConfirm,
      children,
      onClose,
      ...dialogProps
    } = props;

    const { t } = useTranslation(['button']);

    const handleClose = (event: React.MouseEvent<HTMLButtonElement>) => {
      if (onClose) {
        onClose(event, 'escapeKeyDown');
      }
    };

    return (
      <Dialog ref={ref} {...dialogProps} onClose={onClose}>
        <DialogTitle
          sx={{
            px: 2,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          <Typography variant="h6" color={dialog?.color} sx={{ mr: 2 }}>
            {dialog?.title}
          </Typography>
          <IconButton aria-label="close" onClick={handleClose}>
            <CloseRounded />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <Stack
            sx={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
            }}
          >
            <DialogContentText>{children}</DialogContentText>
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button
            autoFocus
            color={button?.color}
            onClick={() => onConfirm?.(obj)}
            startIcon={button?.startIcon}
            variant="contained"
          >
            {button?.text || t('button:Confirm')}
          </Button>
        </DialogActions>
      </Dialog>
    );
  }
);

export default ConfirmModal;
