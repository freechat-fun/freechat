import { useRef } from "react";
import { useTranslation } from "react-i18next";
import { Box, Button, Chip, FormControl, Stack, Textarea } from "@mui/joy";
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
  
  const textAreaRef = useRef<HTMLDivElement>(null);
  const handleClick = () => {
    if (textAreaValue.trim() !== '') {
      onSubmit();
      setTextAreaValue('');
    }
  };
  return (
    <Box sx={{ px: 2, pb: 3 }}>
      <FormControl>
        <Textarea
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
          onKeyDown={(event) => {
            if (event.key === 'Enter') {
              if (event.metaKey || event.ctrlKey) {
                event.preventDefault();
                const textarea = event.target as HTMLTextAreaElement;
                const cursorPosition = textarea.selectionStart;
                const textBeforeCursor = textarea.value.substring(0, cursorPosition);
                const textAfterCursor = textarea.value.substring(cursorPosition);
                textarea.value = textBeforeCursor + '\n' + textAfterCursor;
                textarea.selectionStart = cursorPosition + 1;
                textarea.selectionEnd = cursorPosition + 1;
              } else {
                event.preventDefault();
                handleClick();
              }
            }
          }}
        />
      </FormControl>
    </Box>
  );
}
