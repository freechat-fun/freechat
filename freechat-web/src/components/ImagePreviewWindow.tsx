import { PropsWithChildren } from "react";
import { useTranslation } from "react-i18next";
import { DialogActions, DialogContent, DialogTitle, IconButton, Modal, ModalDialog } from "@mui/joy";
import { DoneRounded } from "@mui/icons-material";
import ImagePreview, { ImagePreviewProps } from "./ImagePreview";

type ImagePreviewWindowProps = ImagePreviewProps & {
  title?: string,
  open?: boolean;
  setOpen?: (open: boolean) => void;
}


const ImagePreviewWindow: React.FC<PropsWithChildren<ImagePreviewWindowProps>> = ((props) => {
  const { t } = useTranslation();

  const { title = t('Image preview'), open = false, setOpen = () => {}, ...others } = props;

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
            <ImagePreview {...others} />
          </DialogContent>
          <DialogActions>
            <IconButton onClick={() => setOpen(false)}><DoneRounded /></IconButton>
          </DialogActions>
        </ModalDialog>
      </Modal>
    </>
  );
});

export default ImagePreviewWindow;