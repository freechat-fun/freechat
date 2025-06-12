import { useRef, KeyboardEvent, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Avatar, Button, IconButton, Stack, useTheme } from '@mui/material';
import { AndroidRounded, SendRounded } from '@mui/icons-material';
import { CharacterSummaryDTO, LlmResultDTO } from 'freechat-sdk';
import { MessageAssistantsWindow } from '.';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { getMessageText } from '../../libs/template_utils';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import { isChatsPaneOpened } from '../../libs/chat_utils';
import { TinyInput } from '..';

export type MessageInputProps = {
  chatId?: string;
  textAreaValue: string;
  setTextAreaValue: (value: string) => void;
  onSubmit: () => void;
  disabled?: boolean;
};

export default function MessageInput(props: MessageInputProps) {
  const {
    chatId,
    textAreaValue,
    setTextAreaValue,
    onSubmit,
    disabled = false,
  } = props;
  const theme = useTheme();
  const { t } = useTranslation('chat');
  const { handleError } = useErrorMessageBusContext();
  const { serverUrl } = useFreeChatApiContext();

  const [assistantAvatar, setAssistantAvatar] = useState<string>();
  const [assistantName, setAssistantName] = useState<string>();
  const [assistantUid, setAssistantUid] = useState<string>();
  const [assistantWindowOpen, setAssistantWindowOpen] =
    useState<boolean>(false);
  const [assistantHelp, setAssistantHelp] = useState(false);
  const [assistantMessage, setAssistantMessaage] = useState('');

  const textAreaRef = useRef<HTMLTextAreaElement>(null);

  const ASSISTANT_UID_KEY_PREFIX = 'MessageInput.assistantUid.';
  const ASSISTANT_NAME_KEY_PREFIX = 'MessageInput.assistantName.';
  const ASSISTANT_AVATAR_KEY_PREFIX = 'MessageInput.assistantAvatar.';
  const apiPath = '/api/v2/chat/send/stream/assistant';

  useEffect(() => {
    const storedAssistantUid = localStorage.getItem(
      ASSISTANT_UID_KEY_PREFIX + chatId
    );
    if (storedAssistantUid) {
      setAssistantUid(storedAssistantUid);
      setAssistantName(
        localStorage.getItem(ASSISTANT_NAME_KEY_PREFIX + chatId) ?? undefined
      );
      setAssistantAvatar(
        localStorage.getItem(ASSISTANT_AVATAR_KEY_PREFIX + chatId) ?? undefined
      );
    }
  }, [chatId]);

  useEffect(() => {
    if (!disabled && !(theme.breakpoints.down('sm') && isChatsPaneOpened())) {
      textAreaRef.current?.focus();
    }
  }, [disabled, theme.breakpoints]);

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
              setAssistantMessaage((prevMessage) => prevMessage + result.text);
            }
          },
          onerror(error) {
            handleError(error);
          },
          onclose() {
            if (shouldReconnect) {
              console.error('The connection has been closed...');
            }
          },
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
  }, [
    assistantHelp,
    assistantUid,
    chatId,
    handleError,
    serverUrl,
    setTextAreaValue,
    t,
  ]);

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

    localStorage.setItem(
      ASSISTANT_UID_KEY_PREFIX + chatId,
      assistant?.characterUid ?? ''
    );
    localStorage.setItem(
      ASSISTANT_NAME_KEY_PREFIX + chatId,
      assistant?.name ?? ''
    );
    localStorage.setItem(
      ASSISTANT_AVATAR_KEY_PREFIX + chatId,
      assistant?.avatar ?? ''
    );
  }

  function handleAssistantSend() {
    setAssistantHelp(true);
  }

  return (
    <Stack
      sx={{
        px: 1,
        pb: { xs: 1, sm: 3 },
        flexDirection: 'row',
      }}
    >
      <Stack flex={1} direction="row">
        <TinyInput
          disabled={disabled}
          placeholder="Type something here…"
          aria-label="Message"
          onChange={(e) => setTextAreaValue(e.target.value)}
          value={assistantMessage || textAreaValue}
          multiline
          minRows={1}
          maxRows={5}
          inputRef={textAreaRef}
          onKeyDown={handleSend}
          sx={{
            maxWidth: undefined,
            flex: 1,
            mx: 1,
            bgcolor: 'background.paper',
            borderRadius: '4px',
            '& .MuiOutlinedInput-root': {
              py: 0,
              '&.Mui-focused': {
                '& .MuiOutlinedInput-notchedOutline': {
                  borderWidth: 1,
                },
              },
            },
          }}
          slotProps={{
            input: {
              size: 'small',
              endAdornment: (
                <Stack
                  direction="row"
                  justifyContent="flex-end"
                  alignItems="center"
                  alignSelf="flex-end"
                  sx={{
                    py: 1,
                    pr: 1,
                    gap: 1,
                    maxHeight: '48px',
                  }}
                >
                  <IconButton
                    disabled={disabled || assistantHelp}
                    size="small"
                    sx={{
                      display:
                        assistantUid && !textAreaValue ? 'inherit' : 'none',
                      alignSelf: 'center',
                    }}
                    onClick={handleAssistantSend}
                  >
                    <Avatar
                      sx={{ width: 30, height: 30 }}
                      alt={assistantName}
                      src={assistantAvatar}
                    >
                      {assistantName}
                    </Avatar>
                  </IconButton>
                  <Button
                    disabled={disabled || assistantHelp || !textAreaValue}
                    size="small"
                    variant="contained"
                    sx={{
                      display: {
                        xs: 'none',
                        lg: assistantUid && !textAreaValue ? 'none' : 'inherit',
                      },
                      alignSelf: 'center',
                      whiteSpace: 'nowrap',
                      bgcolor: '#3b82f6',
                      color: '#fefefe',
                      '&:hover, &:focus-within': {
                        bgcolor: '#2563eb',
                      },
                    }}
                    endIcon={<SendRounded />}
                    onClick={handleClick}
                  >
                    {`${t('Send')} (Ctrl/⌘ + ⏎)`}
                  </Button>
                  <IconButton
                    disabled={disabled || assistantHelp || !textAreaValue}
                    size="small"
                    sx={{
                      display: {
                        xs: assistantUid && !textAreaValue ? 'none' : 'inherit',
                        lg: 'none',
                      },
                      alignSelf: 'center',
                      border: '1px solid',
                      borderColor: 'divider',
                      borderRadius: '4px',
                      bgcolor: '#3b82f6',
                      color: '#fefefe',
                      '&:hover, &:focus-within': {
                        bgcolor: '#2563eb',
                      },
                    }}
                    onClick={handleClick}
                  >
                    <SendRounded />
                  </IconButton>
                </Stack>
              ),
            },
          }}
        />
        <IconButton
          disabled={disabled || assistantHelp}
          size="small"
          sx={{
            mx: 1,
            mb: 0.1,
            px: 1,
            py: 0.5,
            justifySelf: 'flex-end',
            alignSelf: 'flex-end',
            alignItems: 'flex-end',
            border: '1px solid',
            borderColor: 'divider',
            borderRadius: '4px',
            '&:hover': {
              bgcolor: 'background.default',
            },
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
