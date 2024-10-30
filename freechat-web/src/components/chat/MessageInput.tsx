import { useRef, KeyboardEvent, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Avatar, Button, IconButton, Input, Stack, Textarea } from "@mui/joy";
import { AndroidRounded, SendRounded } from "@mui/icons-material";
import { CharacterSummaryDTO, LlmResultDTO } from "freechat-sdk";
import { MessageAssistantsWindow } from ".";
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { getMessageText } from "../../libs/template_utils";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";

export type MessageInputProps = {
  chatId?: string;
  textAreaValue: string;
  setTextAreaValue: (value: string) => void;
  onSubmit: () => void;
  disabled?: boolean;
};

export default function MessageInput(props: MessageInputProps) {
  const { chatId, textAreaValue, setTextAreaValue, onSubmit, disabled = false } = props;
  const { t } = useTranslation('chat');
  const { handleError } = useErrorMessageBusContext();
  const { serverUrl } = useFreeChatApiContext();

  const [assistantAvatar, setAssistantAvatar] = useState<string>();
  const [assistantName, setAssistantName] = useState<string>();
  const [assistantUid, setAssistantUid] = useState<string>();
  const [assistantWindowOpen, setAssistantWindowOpen] = useState<boolean>(false);
  const [assistantHelp, setAssistantHelp] = useState(false);
  const [assistantMessage, setAssistantMessaage] = useState('');
  
  const textAreaRef = useRef<HTMLTextAreaElement>(null);

  const ASSISTANT_UID_KEY_PREFIX = 'MessageInput.assistantUid.';
  const ASSISTANT_NAME_KEY_PREFIX = 'MessageInput.assistantName.';
  const ASSISTANT_AVATAR_KEY_PREFIX = 'MessageInput.assistantAvatar.';
  const apiPath = '/api/v2/chat/send/stream/assistant';

  useEffect(() => {
    const storedAssistantUid = localStorage.getItem(ASSISTANT_UID_KEY_PREFIX + chatId);
    if (storedAssistantUid) {
      setAssistantUid(storedAssistantUid);
      setAssistantName(localStorage.getItem(ASSISTANT_NAME_KEY_PREFIX + chatId) ?? undefined)
      setAssistantAvatar(localStorage.getItem(ASSISTANT_AVATAR_KEY_PREFIX + chatId) ?? undefined);
    }
  }, [chatId]);

  useEffect(() => {
    if (!disabled) {
      textAreaRef.current?.focus();
    }
  }, [disabled]);

  useEffect(() => {
    if (!assistantHelp || !chatId || !assistantUid) {
      return;
    }

    const url = `${serverUrl}${apiPath}/${chatId}/${assistantUid}`;
    const controller = new AbortController();
    let shouldReconnect = true;

    const startListening = async () => {
      try {
        await fetchEventSource(url, {
          method: 'GET',
          signal: controller.signal,
          async onopen(response) {
            if (response.ok) {
              setAssistantMessaage('');
            } else if (response.status === 429) {
              const message = `[${t('Quota has been used up, you can use your API key to continue chatting.')}]`;
              setTextAreaValue(message);
            } else {
              const message = `[${response.statusText}]`;
              setTextAreaValue(message);
            }
            return Promise.resolve();
          },
          onmessage(event) {
            const result = JSON.parse(event.data) as LlmResultDTO;
            if (result.finishReason) {
              const fullMessage = getMessageText(result.message);
              if (fullMessage) {
                setTextAreaValue(fullMessage);
              }
              setAssistantMessaage('');
              setAssistantHelp(false);
            } else if (result.text) {
              setAssistantMessaage(prevMessage => prevMessage + result.text);
            }
          },
          onerror(error) {
            handleError(error);
          },
          onclose() {
            if (shouldReconnect) {
              console.error("The connection has been closed...");
            }
          }
        });
      } catch (error) {
        handleError(error);
      }
    };

    startListening();
    return () => {
      shouldReconnect = false;
      controller.abort();
      setAssistantHelp(false);
    };
    
  }, [assistantHelp, assistantUid, chatId, handleError, serverUrl, setTextAreaValue, t]);

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

    localStorage.setItem(ASSISTANT_UID_KEY_PREFIX + chatId, assistant?.characterUid ?? '');
    localStorage.setItem(ASSISTANT_NAME_KEY_PREFIX + chatId, assistant?.name ?? '');
    localStorage.setItem(ASSISTANT_AVATAR_KEY_PREFIX + chatId, assistant?.avatar ?? '');
  }

  function handleAssistantSend() {
    setAssistantHelp(true);
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
              flex={1}
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

      <Stack flex={1} direction="row">
        <Input
          disabled={disabled}
          placeholder="Type something here…"
          aria-label="Message"
          onChange={(e) => setTextAreaValue(e.target.value)}
          value={assistantMessage || textAreaValue}
          sx={{
            flex: 1,
          }}
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
                flex: 1
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
              sx={{
                py: 1,
                pr: 1,
                gap: 1,
              }}
            >
              <IconButton
                disabled={disabled || assistantHelp}
                loading={assistantHelp}
                size="sm"
                variant="plain"
                sx={{
                  display: assistantUid && !textAreaValue ? 'inherit' : 'none',
                  alignSelf: 'center',
                }}
                onClick={handleAssistantSend}
              >
                <Avatar variant="plain" size="sm" alt={assistantName} src={assistantAvatar}>{assistantName}</Avatar>
              </IconButton>
              <Button
                disabled={disabled || assistantHelp || !textAreaValue}
                size="sm"
                color="primary"
                sx={{
                  display: { xs: 'none', lg: assistantUid && !textAreaValue ? 'none' : 'inherit' },
                  alignSelf: 'center',
                }}
                endDecorator={<SendRounded />}
                onClick={handleClick}
              >
                {`${t('Send')} (Ctrl/⌘ + ⏎)`}
              </Button>
              <IconButton
                disabled={disabled || assistantHelp || !textAreaValue}
                size="sm"
                color="primary"
                variant="solid"
                sx={{
                  display: { xs: assistantUid && !textAreaValue ? 'none' : 'inherit', lg: 'none' },
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
        <IconButton
          disabled={disabled || assistantHelp }
          size="sm"
          variant="outlined"
          sx={{
            mx: 1,
            mb: 0.1,
            px: 1,
            py: 0.5,
            justifySelf: 'flex-end',
            alignSelf: 'flex-end',
            alignItems: 'flex-end',
          }}
          onClick={handleAssistantChoose}
        >
          <AndroidRounded fontSize="large" />
        </IconButton>
      </Stack>
      <MessageAssistantsWindow
        assistantUid={assistantUid}
        setAssistant={handleAssistantSelect}
        open={assistantWindowOpen}
        setOpen={setAssistantWindowOpen}
      />
    </Stack>
  );
}
