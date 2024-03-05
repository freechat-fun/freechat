import { PropsWithChildren } from "react";
import { useTranslation } from "react-i18next";
import { ScrollableTypography } from ".";
import { DialogActions, DialogContent, DialogTitle, IconButton, Modal, ModalDialog, Stack, TypographyProps } from "@mui/joy";
import { DoneRounded } from "@mui/icons-material";

type TextPreviewProps = TypographyProps<'div'> & {
  title?: string,
  open?: boolean;
  setOpen?: (open: boolean) => void;
}


const TextPreview: React.FC<PropsWithChildren<TextPreviewProps>> = (({ children, ...props }) => {
  const { t } = useTranslation();

  const { title = t('Text preview'), open = false, setOpen = () => {}, ...others } = props;

  function handleClose(_event: React.MouseEvent<HTMLButtonElement>, reason: string): void {
    if (reason !== 'backdropClick') {
      setOpen(false);
    }
  }

  return (
    <>
      <Modal
        open={open}
        onClose={handleClose}
      >
        <ModalDialog>
          <DialogTitle>{title}</DialogTitle>
          <DialogContent>
            <Stack spacing={2} sx={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center'
            }}>
              <ScrollableTypography {...others}>
                {children}
              </ScrollableTypography>
            </Stack>
          </DialogContent>
          <DialogActions>
            <IconButton onClick={() => setOpen(false)}><DoneRounded /></IconButton>
          </DialogActions>
        </ModalDialog>
      </Modal>
    </>
  );
});

export default TextPreview;