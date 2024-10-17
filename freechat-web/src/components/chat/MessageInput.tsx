import { useRef, KeyboardEvent, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Avatar, Box, Button, IconButton, Input, Stack, Textarea } from "@mui/joy";
import { AndroidRounded, SendRounded } from "@mui/icons-material";
import { CharacterSummaryDTO } from "freechat-sdk";
import { MessageAssistantsWindow } from ".";

export type MessageInputProps = {
  textAreaValue: string;
  setTextAreaValue: (value: string) => void;
  onSubmit: () => void;
  disabled?: boolean;
};

export default function MessageInput(props: MessageInputProps) {
  const { textAreaValue, setTextAreaValue, onSubmit, disabled = false } = props;
  const { t } = useTranslation('chat');

  const [assistantAvatar, setAssistantAvatar] = useState<string>();
  const [assistantName, setAssistantName] = useState<string>();
  const [assistantUid, setAssistantUid] = useState<string>();
  const [assistantWindowOpen, setAssistantWindowOpen] = useState<boolean>(false);
  
  const textAreaRef = useRef<HTMLTextAreaElement>(null);

  const ASSISTANT_UID_KEY = 'MessageInput.assistantUid';
  const ASSISTANT_NAME_KEY = 'MessageInput.assistantName';
  const ASSISTANT_AVATAR_KEY = 'MessageInput.assistantAvatar';

  useEffect(() => {
    const storedAssistantUid = localStorage.getItem(ASSISTANT_UID_KEY);
    if (storedAssistantUid) {
      setAssistantUid(storedAssistantUid);
      setAssistantName(localStorage.getItem(ASSISTANT_NAME_KEY) ?? undefined)
      setAssistantAvatar(localStorage.getItem(ASSISTANT_AVATAR_KEY) ?? undefined);
    }
  }, []);

  useEffect(() => {
    if (!disabled) {
      textAreaRef.current?.focus();
    }
  }, [disabled]);

  function handleClick() {
    if (textAreaValue.trim() !== '') {
      onSubmit();
      setTextAreaValue('');
    }
  }

  function handleSend(event: KeyboardEvent<HTMLInputElement>) {
    if (event.nativeEvent.isComposing) {
      return;
    }

    if (event.key === 'Enter' && (event.metaKey || event.ctrlKey)) {
      // send
      event.preventDefault();
      handleClick();
    }
  }

  function handleAssistantChoose() {
    setAssistantWindowOpen(!assistantWindowOpen);
  }

  function handleAssistantSelect(assistant?: CharacterSummaryDTO | null) {
    if (assistant === undefined) {
      return;
    }

    setAssistantUid(assistant?.characterUid);
    setAssistantName(assistant?.name);
    setAssistantAvatar(assistant?.avatar);

    localStorage.setItem(ASSISTANT_UID_KEY, assistant?.characterUid ?? '');
    localStorage.setItem(ASSISTANT_NAME_KEY, assistant?.name ?? '');
    localStorage.setItem(ASSISTANT_AVATAR_KEY, assistant?.avatar ?? '');
  }

  function handleAssistantSend() {
    
  }

  // function handleSendByEnter(event: KeyboardEvent<HTMLTextAreaElement>) {
  //   if (event.nativeEvent.isComposing) {
  //     return;
  //   }

  //   if (event.key === 'Enter') {
  //     if (event.metaKey || event.ctrlKey) {
  //       // new line
  //       event.preventDefault();
  //       const textarea = event.target as HTMLTextAreaElement;
  //       const cursorPosition = textarea.selectionStart;
  //       const textBeforeCursor = textarea.value.substring(0, cursorPosition);
  //       const textAfterCursor = textarea.value.substring(cursorPosition);
  //       textarea.value = textBeforeCursor + '\n' + textAfterCursor;
  //       textarea.selectionStart = cursorPosition + 1;
  //       textarea.selectionEnd = cursorPosition + 1;
  //     } else {
  //       // send
  //       event.preventDefault();
  //       handleClick();
  //     }
  //   }
  // }

  return (
    <Stack sx={{
      px: 2,
      pb: { xs: 1, sm: 3 },
      flexDirection: "row",
    }}>
        {/* <Textarea
          disabled={disabled}
          placeholder="Type something here…"
          aria-label="Message"
          ref={textAreaRef}
          onChange={(e) => setTextAreaValue(e.target.value)}
          value={textAreaValue}
          minRows={2}
          maxRows={10}
          endDecorator={
            <Stack
              direction="row"
              justifyContent="space-between"
              alignItems="center"
              flexGrow={1}
              sx={{
                py: 1,
                pr: 1,
                borderTop: '1px solid',
                borderColor: 'divider',
              }}
            >
              <Chip variant="soft" color="primary">
                {`${t('Newline')} (Ctrl/⌘ + ⏎)`}
              </Chip>
              <Button
                disabled={disabled}
                size="sm"
                color="primary"
                sx={{ alignSelf: 'center', borderRadius: 'lg' }}
                endDecorator={<SendRounded />}
                onClick={handleClick}
              >
                {t('Send')}
              </Button>
            </Stack>
          }
          onKeyDown={handleSendByEnter}
        /> */}

      <Box flexGrow={1}>
        <Input
          disabled={disabled}
          placeholder="Type something here…"
          aria-label="Message"
          onChange={(e) => setTextAreaValue(e.target.value)}
          value={textAreaValue}
          slotProps={{
            input: {
              component: Textarea,
              variant: 'plain',
              minRows: 1,
              maxRows: 5,
              slotProps: { textarea: { ref: textAreaRef } },
              sx: {
                '--Textarea-focusedInset': 'inset',
                '--Textarea-focusedThickness': 0,
                p: 1.2,
              },
            },
            endDecorator: {
              sx: {
                alignSelf: 'flex-end',
              }
            }
          }}
          endDecorator={
            <Stack
              direction="row"
              justifyContent="flex-end"
              alignItems="center"
              flexGrow={1}
              sx={{
                py: 1,
                pr: 1,
                gap: 1,
              }}
            >
              <IconButton
                disabled={disabled}
                size="sm"
                variant="plain"
                sx={{
                  display: assistantUid ? 'inherit' : 'none',
                  alignSelf: 'center',
                  mr: 1,
                }}
                onClick={handleAssistantSend}
              >
                <Avatar variant="plain" size="sm" alt={assistantName} src={assistantAvatar}>{assistantName}</Avatar>
              </IconButton>
              <Button
                disabled={disabled || !textAreaValue}
                size="sm"
                color="primary"
                sx={{
                  display: { xs: 'none', lg: 'inherit' },
                  alignSelf: 'center',
                }}
                endDecorator={<SendRounded />}
                onClick={handleClick}
              >
                {`${t('Send')} (Ctrl/⌘ + ⏎)`}
              </Button>
              <IconButton
                disabled={disabled || !textAreaValue}
                size="sm"
                color="primary"
                variant="solid"
                sx={{
                  display: { xs: 'inherit', lg: 'none' },
                  alignSelf: 'center',
                }}
                onClick={handleClick}
              >
                <SendRounded />
              </IconButton>
            </Stack>
          }
          onKeyDown={handleSend}
        />
      </Box>
      <IconButton
        disabled={disabled}
        size="sm"
        variant="outlined"
        sx={{
          mx: 1,
          px: 1,
          height: '100%',
          alignSelf: 'center',
        }}
        onClick={handleAssistantChoose}
      >
        <AndroidRounded fontSize="large" />
      </IconButton>
      <MessageAssistantsWindow
        assistantUid={assistantUid}
        setAssistant={handleAssistantSelect}
        open={assistantWindowOpen}
        setOpen={setAssistantWindowOpen}
      />
    </Stack>
  );
}
