import { PropsWithChildren } from "react";
import { useTranslation } from "react-i18next";
import { MessageAssistantsPane } from ".";
import { DialogActions, DialogContent, DialogTitle, IconButton, Modal, ModalDialog, Stack, TypographyProps } from "@mui/joy";
import { DoneRounded } from "@mui/icons-material";

type MessageAssistantsWindowProps = TypographyProps<'div'> & {
  characterId?: number;
  setCharacterId?: (id: number | undefined) => void;
  open?: boolean;
  setOpen?: (open: boolean) => void;
}

const MessageAssistantsWindow: React.FC<PropsWithChildren<MessageAssistantsWindowProps>> = ((props) => {
  const { t } = useTranslation('chat');

  const { characterId, setCharacterId, open = false, setOpen = () => {} } = props;

  const title = t('Select an assistant');

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
              <MessageAssistantsPane
                characterId={characterId}
                setCharacterId={setCharacterId}
              />
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

export default MessageAssistantsWindow;