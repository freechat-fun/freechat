import { useCallback, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Alert,
  Box,
  Button,
  Stack,
  useTheme,
  SxProps,
  Theme,
} from '@mui/material';
import {
  AvatarWithStatus,
  ChatBubble,
  MessageInput,
  MessagesPaneHeader,
} from '.';
import {
  ChatContentDTO,
  ChatMessageDTO,
  ChatMessageRecordDTO,
  ChatSessionDTO,
  LlmResultDTO,
} from 'freechat-sdk';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  PROACTIVE_CHAT_PROMPT_EN,
  PROACTIVE_CHAT_PROMPT_ZH,
  getSenderReply,
  getSenderStatus,
} from '../../libs/chat_utils';
import { processBackground } from '../../libs/ui_utils';
import { getMessageText } from '../../libs/template_utils';
import { useChatRun } from '../../libs/use_chat_run';
import { chatRunErrorKey, hasChatRunContent } from '../../libs/chat_run';
import type { ChatRunOutcome, ChatRunStatus } from '../../libs/chat_run';

const ENABLE_BACKGROUND_KEY = 'MessagesPane.enableBackground';

type MessagesPaneProps = {
  session?: ChatSessionDTO;
  defaultDebugMode?: boolean;
  fullscreenMode?: boolean;
  setFullscreenMode?: (fullscreenMode: boolean) => void;
  onOpen?: () => void;
  onReceivedMessage?: (result: LlmResultDTO) => void;
  sx?: SxProps<Theme>;
};

export default function MessagesPane(props: MessagesPaneProps) {
  return (
    <MessagesPaneSession key={props.session?.context?.chatId} {...props} />
  );
}

function MessagesPaneSession(props: MessagesPaneProps) {
  const {
    session,
    defaultDebugMode = false,
    fullscreenMode = false,
    setFullscreenMode,
    onOpen,
    onReceivedMessage,
    sx,
  } = props;
  const { t } = useTranslation('chat');
  const theme = useTheme();
  const { chatApi, serverUrl } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [chatMessages, setChatMessages] = useState<ChatMessageRecordDTO[]>([]);
  const [textAreaValue, setTextAreaValue] = useState('');
  const [messageToSend, setMessageToSend] =
    useState<ChatMessageRecordDTO | null>(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [runStatus, setRunStatus] = useState<ChatRunStatus>();
  const [assistantStatus, setAssistantStatus] = useState<ChatRunStatus>();
  const [streamResult, setStreamResult] = useState<LlmResultDTO>();
  const failedMessage = useRef<ChatMessageRecordDTO | null>(null);
  const messagesRevision = useRef(0);
  const historyCallbacks = useRef({ onOpen, handleError });
  historyCallbacks.current = { onOpen, handleError };
  const [debugMode, setDebugMode] = useState(defaultDebugMode);
  const [background, setBackground] = useState('');
  const [enableBackground, setEnableBackground] = useState(
    localStorage.getItem(
      ENABLE_BACKGROUND_KEY + '-' + (session?.context?.chatId ?? '')
    ) !== '0'
  );

  const proactiveChatHandler = useRef<number | null>(null);

  const sender = session?.character;
  const context = session?.context;
  const avatarWithStatus = (
    <AvatarWithStatus status={getSenderStatus(session)} src={sender?.avatar} />
  );

  const showDebug = debugMode && !!session?.isDebugEnabled;
  const visibleStream =
    !!messageToSend &&
    hasChatRunContent(streamResult?.message, showDebug, getSenderReply);

  useChatRun({
    enabled: !!messageToSend,
    url: `${serverUrl}/api/v2/chat/send/stream/${context?.chatId}`,
    body: messageToSend ? JSON.stringify(messageToSend.message) : undefined,
    sessionId: context?.chatId,
    onUpdate: (snapshot) => {
      setRunStatus(snapshot.phase);
      setStreamResult(snapshot.result);
    },
    onFinish: handleReceiveFinish,
  });

  const handleSend = useCallback(
    (
      chatId?: string,
      userNickname?: string,
      textToSend?: string,
      processingMessage?: ChatMessageRecordDTO | null
    ) => {
      if (!textToSend?.trim() || !chatId || processingMessage) {
        return;
      }

      const content = new ChatContentDTO();
      content.type = 'text';
      content.content = textToSend.trim();

      const userMessage = new ChatMessageDTO();
      userMessage.role = 'user';
      userMessage.contents = [content];
      userMessage.name = userNickname;

      const messageRecord = new ChatMessageRecordDTO();
      messageRecord.message = userMessage;
      messageRecord.gmtCreate = new Date();

      setChatMessages((prevMessages) => {
        return [...prevMessages, messageRecord];
      });

      messagesRevision.current++;
      setStreamResult(undefined);
      setRunStatus('sending');
      setAssistantStatus(undefined);
      setMessageToSend(messageRecord);
      failedMessage.current = null;
      setErrorMessage('');
    },
    []
  );

  useEffect(() => {
    if (!context?.chatId || !chatApi) return;
    let active = true;
    const revision = messagesRevision.current;
    const request = showDebug
      ? chatApi.listDebugMessages2(context.chatId)
      : chatApi.listMessages2(context.chatId);
    void request
      .then(async (resp) => {
        if (!active) return;
        const bg = sender?.picture
          ? await processBackground(sender.picture, theme.palette.mode, 0.6)
          : '';
        if (!active) return;
        setBackground(bg);
        if (resp && revision === messagesRevision.current)
          setChatMessages(resp);
        historyCallbacks.current.onOpen?.();
      })
      .catch((error) => {
        if (active) historyCallbacks.current.handleError(error);
      });
    return () => {
      active = false;
    };
  }, [
    chatApi,
    showDebug,
    context?.chatId,
    theme.palette.mode,
    sender?.picture,
  ]);

  useEffect(() => {
    const savedEnableBackground = localStorage.getItem(
      ENABLE_BACKGROUND_KEY + '-' + (session?.context?.chatId ?? '')
    );
    if (savedEnableBackground) {
      setEnableBackground(savedEnableBackground !== '0');
    }
  }, [session?.context?.chatId]);

  useEffect(() => {
    localStorage.setItem(
      ENABLE_BACKGROUND_KEY + '-' + (session?.context?.chatId ?? ''),
      enableBackground ? '1' : '0'
    );
  }, [enableBackground, session?.context?.chatId]);

  useEffect(() => {
    if (proactiveChatHandler.current) {
      clearTimeout(proactiveChatHandler.current);
      proactiveChatHandler.current = null;
    }

    const proactiveChatWaitingTime = session?.proactiveChatWaitingTime ?? 0;
    const proactiveChatPrompt =
      'zh' === sender?.lang
        ? PROACTIVE_CHAT_PROMPT_ZH
        : PROACTIVE_CHAT_PROMPT_EN;

    if (
      proactiveChatWaitingTime > 0 &&
      chatMessages.length > 2 &&
      !messageToSend &&
      !assistantStatus &&
      !errorMessage
    ) {
      const lastMessage = chatMessages[chatMessages.length - 1];
      const secondToLastMessage = chatMessages[chatMessages.length - 2];

      if (
        lastMessage.message?.role === 'assistant' &&
        secondToLastMessage.message?.role === 'user' &&
        getMessageText(secondToLastMessage.message) !== proactiveChatPrompt
      ) {
        proactiveChatHandler.current = setTimeout(
          () => {
            handleSend(
              context?.chatId,
              context?.userNickname,
              proactiveChatPrompt,
              messageToSend
            );
          },
          proactiveChatWaitingTime * 60 * 1000
        );
      }
    }
    return () => {
      if (proactiveChatHandler.current)
        clearTimeout(proactiveChatHandler.current);
    };
  }, [
    assistantStatus,
    errorMessage,
    chatMessages,
    context?.chatId,
    context?.userNickname,
    handleSend,
    messageToSend,
    sender?.lang,
    session?.proactiveChatWaitingTime,
  ]);

  function handleResend(): void {
    if (!context?.chatId || !failedMessage.current || messageToSend) return;
    messagesRevision.current++;
    setStreamResult(undefined);
    setRunStatus('sending');
    setAssistantStatus(undefined);
    setMessageToSend(failedMessage.current);
    setErrorMessage('');
  }

  function handleReceiveFinish(outcome: ChatRunOutcome): void {
    const { result, status } = outcome;
    messagesRevision.current++;
    failedMessage.current = status === 'completed' ? null : messageToSend;
    setMessageToSend(null);
    setStreamResult(undefined);
    setRunStatus(status === 'completed' ? undefined : status);
    setErrorMessage(
      status === 'completed'
        ? ''
        : status === 'cancelled'
          ? 'The request was cancelled.'
          : chatRunErrorKey(outcome.error)
    );

    if (hasChatRunContent(result.message, showDebug, getSenderReply)) {
      const messageRecord = new ChatMessageRecordDTO();
      messageRecord.message = result.message;
      messageRecord.gmtCreate = new Date();
      if (session?.isDebugEnabled && result.tokenUsage) {
        messageRecord.ext = JSON.stringify([
          result.tokenUsage.inputTokenCount,
          result.tokenUsage.outputTokenCount,
          result.tokenUsage.totalTokenCount,
        ]);
      }
      setChatMessages((prevMessages) => [...prevMessages, messageRecord]);
    }
    if (status === 'completed') onReceivedMessage?.(result);
  }

  function handleChatReplay(index: number): void {
    if (!context?.chatId || messageToSend || index >= chatMessages.length) {
      return;
    }

    let rollbackIndex = index;
    while (
      chatMessages[rollbackIndex].message?.role !== 'user' &&
      rollbackIndex > 0
    ) {
      rollbackIndex = rollbackIndex - 1;
    }
    const rollbackCount = chatMessages.length - rollbackIndex;

    chatApi
      ?.rollbackMessages(context?.chatId, rollbackCount)
      .then(() => {
        const newMessageToSend = chatMessages[rollbackIndex];
        const newChatMessages = chatMessages.slice(0, rollbackIndex + 1);
        messagesRevision.current++;
        setChatMessages(newChatMessages);
        setStreamResult(undefined);
        setRunStatus('sending');
        setErrorMessage('');
        setMessageToSend(newMessageToSend);
      })
      .catch(handleError);
  }

  function handleClearMemory(chatId: string): void {
    chatApi?.clearMemory(chatId).then(setChatMessages).catch(handleError);
  }

  function getDisplayMessages(): ChatMessageRecordDTO[] {
    const proactiveChatPrompt =
      'zh' === sender?.lang
        ? PROACTIVE_CHAT_PROMPT_ZH
        : PROACTIVE_CHAT_PROMPT_EN;
    return chatMessages.filter(
      (record) =>
        !!record.message &&
        getMessageText(record.message) !== proactiveChatPrompt &&
        (record.message.role === 'user' ||
          hasChatRunContent(record.message, showDebug, getSenderReply))
    );
  }

  return (
    <Stack
      sx={{
        height: { xs: 'var(--App-height-xs)', sm: '100dvh' },
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: theme.palette.background.paper,
        position: 'relative',
        overflow: 'hidden',
        backgroundImage: `url(${enableBackground ? background : ''})`,
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        ...sx,
      }}
    >
      <MessagesPaneHeader
        session={session}
        onClearHistory={handleClearMemory}
        debugMode={showDebug}
        runStatus={
          messageToSend
            ? (runStatus ?? 'sending')
            : (assistantStatus ?? runStatus)
        }
        fullscreenMode={fullscreenMode}
        disabled={!!messageToSend}
        disableBackgroundSetting={background === ''}
        enableBackground={enableBackground && background !== ''}
        setEnableBackground={setEnableBackground}
        setDebugMode={setDebugMode}
        setFullscreenMode={setFullscreenMode}
      />
      <Box
        sx={{
          display: 'flex',
          flex: 1,
          minHeight: 0,
          px: 2,
          py: 3,
          overflowY: 'scroll',
          flexDirection: 'column-reverse',
        }}
      >
        <Stack spacing={2} sx={{ justifyContent: 'flex-end' }}>
          {getDisplayMessages().map((record, index) => {
            const message = record.message as ChatMessageDTO;
            const isYou = message.role === 'user';
            return (
              <Stack
                key={`message-container-${index}`}
                direction={isYou ? 'row-reverse' : 'row'}
                spacing={2}
              >
                {!isYou && avatarWithStatus}
                <ChatBubble
                  key={`message-${index}`}
                  debugMode={showDebug}
                  session={session}
                  record={record}
                  variant={isYou ? 'sent' : 'received'}
                  pending={!!messageToSend}
                  onReplay={() =>
                    handleChatReplay(chatMessages.indexOf(record))
                  }
                />
              </Stack>
            );
          })}

          {visibleStream && (
            <Stack key="streaming-message" direction="row" spacing={2}>
              {avatarWithStatus}
              <ChatBubble
                session={session}
                record={{
                  message: streamResult?.message,
                  gmtCreate: messageToSend?.gmtCreate,
                }}
                variant="received"
                debugMode={showDebug}
                pending
              />
            </Stack>
          )}
          {errorMessage && (
            <Alert
              severity={runStatus === 'cancelled' ? 'info' : 'error'}
              action={
                <Button color="inherit" onClick={handleResend}>
                  {t('Retry the request.')}
                </Button>
              }
            >
              {t(errorMessage)}
            </Alert>
          )}
        </Stack>
      </Box>

      {/* input message */}
      <MessageInput
        chatId={context?.chatId}
        onRunStatus={setAssistantStatus}
        disabled={!!messageToSend}
        textAreaValue={textAreaValue}
        setTextAreaValue={setTextAreaValue}
        onSubmit={() =>
          handleSend(
            context?.chatId,
            context?.userNickname,
            textAreaValue,
            messageToSend
          )
        }
      />
    </Stack>
  );
}
