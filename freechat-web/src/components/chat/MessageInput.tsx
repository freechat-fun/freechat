import { useRef, KeyboardEvent, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Alert, Avatar, IconButton, Stack, useTheme } from '@mui/material';
import { AndroidRounded, SendRounded } from '@mui/icons-material';
import { CharacterSummaryDTO } from 'freechat-sdk';
import { MessageAssistantsWindow } from '.';
import { chatRunErrorKey } from '../../libs/chat_run';
import type { ChatRunStatus } from '../../libs/chat_run';
import { useChatRun } from '../../libs/use_chat_run';
import { getMessageText } from '../../libs/template_utils';
import { useFreeChatApiContext } from '../../contexts';
import { getSenderReply, isChatsPaneOpened } from '../../libs/chat_utils';
import { TinyInput } from '..';

export type MessageInputProps = {
  chatId?: string;
  textAreaValue: string;
  setTextAreaValue: (value: string) => void;
  onSubmit: () => void;
  disabled?: boolean;
  onRunStatus?: (status?: ChatRunStatus) => void;
};

export default function MessageInput(props: MessageInputProps) {
  const {
    chatId,
    textAreaValue,
    setTextAreaValue,
    onSubmit,
    onRunStatus,
    disabled = false,
  } = props;
  const theme = useTheme();
  const { t } = useTranslation('chat');
  const { serverUrl } = useFreeChatApiContext();

  const [assistantAvatar, setAssistantAvatar] = useState<string>();
  const [assistantName, setAssistantName] = useState<string>();
  const [assistantUid, setAssistantUid] = useState<string>();
  const [assistantWindowOpen, setAssistantWindowOpen] =
    useState<boolean>(false);
  const [assistantRequest, setAssistantRequest] = useState<{
    chatId: string;
    uid: string;
  }>();
  const assistantHelp =
    !!assistantRequest && assistantRequest.chatId === chatId;
  const [assistantMessage, setAssistantMessage] = useState('');
  const [assistantError, setAssistantError] = useState('');

  const textAreaRef = useRef<HTMLTextAreaElement>(null);

  const ASSISTANT_UID_KEY_PREFIX = 'MessageInput.assistantUid.';
  const ASSISTANT_NAME_KEY_PREFIX = 'MessageInput.assistantName.';
  const ASSISTANT_AVATAR_KEY_PREFIX = 'MessageInput.assistantAvatar.';
  const apiPath = '/api/v2/chat/send/stream/assistant';

  useEffect(() => {
    const storedAssistantUid = localStorage.getItem(
      ASSISTANT_UID_KEY_PREFIX + chatId
    );
    setAssistantUid(storedAssistantUid || undefined);
    setAssistantName(
      localStorage.getItem(ASSISTANT_NAME_KEY_PREFIX + chatId) ?? undefined
    );
    setAssistantAvatar(
      localStorage.getItem(ASSISTANT_AVATAR_KEY_PREFIX + chatId) ?? undefined
    );
    setAssistantMessage('');
    setAssistantError('');
    setAssistantRequest(undefined);
  }, [chatId]);

  useEffect(() => {
    if (!disabled && !(theme.breakpoints.down('sm') && isChatsPaneOpened())) {
      textAreaRef.current?.focus();
    }
  }, [disabled, theme.breakpoints]);

  useChatRun({
    enabled: assistantHelp && !disabled,
    url: `${serverUrl}${apiPath}/${chatId}/${assistantRequest?.uid}`,
    sessionId: chatId,
    onUpdate: (snapshot) => {
      onRunStatus?.(snapshot.phase);
      setAssistantMessage(
        getSenderReply(getMessageText(snapshot.result.message) ?? '', false)
      );
    },
    onFinish: (outcome) => {
      const text = getSenderReply(
        getMessageText(outcome.result.message) ?? '',
        false
      );
      if (text) setTextAreaValue(text);
      setAssistantMessage('');
      setAssistantRequest(undefined);
      onRunStatus?.(
        outcome.status === 'completed' ? undefined : outcome.status
      );
      setAssistantError(
        outcome.status === 'completed'
          ? ''
          : outcome.status === 'cancelled'
            ? 'The request was cancelled.'
            : chatRunErrorKey(outcome.error)
      );
    },
  });

  useEffect(() => {
    if (!disabled || !assistantHelp) return;
    if (assistantMessage) setTextAreaValue(assistantMessage);
    setAssistantMessage('');
    setAssistantRequest(undefined);
    onRunStatus?.(undefined);
  }, [
    disabled,
    assistantHelp,
    assistantMessage,
    setTextAreaValue,
    onRunStatus,
  ]);

  function handleClick() {
    if (!disabled && !assistantHelp && textAreaValue.trim() !== '') {
      onSubmit();
      setTextAreaValue('');
    }
  }

  function handleSend(event: KeyboardEvent<HTMLInputElement>) {
    if (event.nativeEvent.isComposing) {
      return;
    }

    if (event.key === 'Enter') {
      if (!(event.shiftKey || event.ctrlKey || event.metaKey)) {
        event.preventDefault();
        handleClick();
      }
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
    if (!chatId || !assistantUid || disabled || assistantHelp) return;
    setAssistantError('');
    setAssistantMessage('');
    onRunStatus?.('sending');
    setAssistantRequest({ chatId, uid: assistantUid });
  }

  return (
    <Stack
      sx={{
        px: 1,
        pb: { xs: 1, sm: 3 },
        flexDirection: 'column',
      }}
    >
      {assistantError && <Alert severity="warning">{t(assistantError)}</Alert>}
      <Stack direction="row" sx={{ flex: 1 }}>
        <TinyInput
          disabled={disabled || assistantHelp}
          placeholder="Type something here…"
          aria-label="Message"
          onChange={(e) => setTextAreaValue(e.target.value)}
          value={assistantMessage || textAreaValue || ''}
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
                  sx={{
                    py: 1,
                    pr: 1,
                    gap: 1,
                    maxHeight: '48px',
                    justifyContent: 'flex-end',
                    alignItems: 'center',
                    alignSelf: 'flex-end',
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
                  <IconButton
                    disabled={disabled || assistantHelp || !textAreaValue}
                    size="small"
                    sx={{
                      display:
                        assistantUid && !textAreaValue ? 'none' : 'inherit',
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
