import { useRef, KeyboardEvent, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Box, Button, IconButton, Input, Stack, Textarea } from "@mui/joy";
import { SendRounded } from "@mui/icons-material";

export type MessageInputProps = {
  textAreaValue: string;
  setTextAreaValue: (value: string) => void;
  onSubmit: () => void;
  disabled?: boolean;
};

export default function MessageInput(props: MessageInputProps) {
  const { textAreaValue, setTextAreaValue, onSubmit, disabled = false } = props;
  const { t } = useTranslation('chat');
  
  const textAreaRef = useRef<HTMLTextAreaElement>(null);

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
    <Box sx={{
      px: 2,
      pb: { xs: 1, sm: 3 },
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
              }}
            >
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
  );
}
