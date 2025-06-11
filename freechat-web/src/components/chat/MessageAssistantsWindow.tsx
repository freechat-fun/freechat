import { PropsWithChildren, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { MessageAssistantsPane } from '.';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Stack,
  TypographyProps,
} from '@mui/material';
import { DoneRounded } from '@mui/icons-material';
import { CharacterSummaryDTO } from 'freechat-sdk';

type MessageAssistantsWindowProps = TypographyProps<'div'> & {
  assistantUid?: string;
  setAssistant?: (assistant?: CharacterSummaryDTO | null) => void;
  open?: boolean;
  setOpen?: (open: boolean) => void;
};

const MessageAssistantsWindow: React.FC<
  PropsWithChildren<MessageAssistantsWindowProps>
> = (props) => {
  const { t } = useTranslation('chat');

  const {
    assistantUid,
    setAssistant,
    open = false,
    setOpen = () => {},
  } = props;

  const [selectedAssistant, setSelectedAssistant] =
    useState<CharacterSummaryDTO | null>();

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
      <Dialog
        open={open}
        onClose={handleClose}
        maxWidth="md"
        sx={{
          '& .MuiDialog-paper': {
            borderRadius: '6px',
            marginBottom: 'var(--Footer-height)',
          },
        }}
      >
        <DialogTitle>{title}</DialogTitle>
        <DialogContent>
          <Stack
            spacing={2}
            sx={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
            }}
          >
            <MessageAssistantsPane
              assistantUid={getAssistantUid()}
              setAssistant={setSelectedAssistant}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <IconButton onClick={handleConfirm}>
            <DoneRounded />
          </IconButton>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default MessageAssistantsWindow;
