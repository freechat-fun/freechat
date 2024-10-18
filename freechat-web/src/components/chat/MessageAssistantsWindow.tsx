import { PropsWithChildren, useState } from "react";
import { useTranslation } from "react-i18next";
import { MessageAssistantsPane } from ".";
import { DialogActions, DialogContent, DialogTitle, IconButton, Modal, ModalDialog, Stack, TypographyProps } from "@mui/joy";
import { DoneRounded } from "@mui/icons-material";
import { CharacterSummaryDTO } from "freechat-sdk";

type MessageAssistantsWindowProps = TypographyProps<'div'> & {
  assistantUid?: string;
  setAssistant?: (assistant?: CharacterSummaryDTO | null) => void;
  open?: boolean;
  setOpen?: (open: boolean) => void;
}

const MessageAssistantsWindow: React.FC<PropsWithChildren<MessageAssistantsWindowProps>> = ((props) => {
  const { t } = useTranslation('chat');

  const { assistantUid, setAssistant, open = false, setOpen = () => {} } = props;

  const [selectedAssistant, setSelectedAssistant] = useState<CharacterSummaryDTO | null>();

  const title = t('Select an assistant');

  function getAssistantUid() {
    if (selectedAssistant === null) {
      return undefined;
    } else {
      return selectedAssistant?.characterUid ?? assistantUid;
    }
  }

  function handleClose(reason: string): void {
    if (reason !== 'backdropClick') {
      setOpen(false);
    }
  }

  function handleConfirm(): void {
    if (selectedAssistant !== undefined) {
      setAssistant?.(selectedAssistant);
    }
    setOpen(false);
  }

  return (
    <>
      <Modal
        open={open}
        onClose={handleClose}
        sx={{
          mb: 'var(--Footer-height)',
        }}
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
                assistantUid={getAssistantUid()}
                setAssistant={setSelectedAssistant}
              />
            </Stack>
          </DialogContent>
          <DialogActions>
            <IconButton onClick={handleConfirm}><DoneRounded /></IconButton>
          </DialogActions>
        </ModalDialog>
      </Modal>
    </>
  );
});

export default MessageAssistantsWindow;