/* eslint-disable prettier/prettier */
import { PropsWithChildren } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Box,
  TypographyProps,
} from '@mui/material';
import { DoneRounded } from '@mui/icons-material';
import { ScrollableTypography } from '.';

type TextPreviewWindowProps = TypographyProps<'div'> & {
  title?: string;
  open?: boolean;
  setOpen?: (open: boolean) => void;
};

const TextPreviewWindow: React.FC<
  PropsWithChildren<TextPreviewWindowProps>
> = ({ children, ...props }) => {
  const { t } = useTranslation();

  const {
    title = t('Text preview'),
    open = false,
    setOpen = () => {},
    ...others
  } = props;

  const handleClose = (
    _event: React.MouseEvent<HTMLButtonElement>,
    reason: string
  ): void => {
    if (reason !== 'backdropClick') {
      setOpen(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="md"
      fullWidth
    >
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            gap: 2,
          }}
        >
          <ScrollableTypography {...others}>
            {children}
          </ScrollableTypography>
        </Box>
      </DialogContent>
      <DialogActions>
        <IconButton onClick={() => setOpen(false)} sx={{ mr: 2 }}>
          <DoneRounded />
        </IconButton>
      </DialogActions>
    </Dialog>
  );
};

export default TextPreviewWindow;
