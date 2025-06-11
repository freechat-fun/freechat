import { useCallback, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Stack, useTheme, SxProps, Theme } from '@mui/material';
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
  getSenderName,
  getSenderStatus,
} from '../../libs/chat_utils';
import { processBackground } from '../../libs/ui_utils';
import { getMessageText } from '../../libs/template_utils';

type MessagesPaneProps = {
  session?: ChatSessionDTO;
  defaultDebugMode?: boolean;
  onOpen?: () => void;
  onReceivedMessage?: (result: LlmResultDTO) => void;
  sx?: SxProps<Theme>;
};

export default function MessagesPane(props: MessagesPaneProps) {
  const {
    session,
    defaultDebugMode = false,
    onOpen,
    onReceivedMessage,
    sx,
  } = props;
  const { t } = useTranslation('chat');
  const theme = useTheme();
  const { chatApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [chatMessages, setChatMessages] = useState<ChatMessageRecordDTO[]>([]);
  const [textAreaValue, setTextAreaValue] = useState('');
  const [messageToSend, setMessageToSend] =
    useState<ChatMessageRecordDTO | null>(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [debugMode, setDebugMode] = useState(defaultDebugMode);
  const [background, setBackground] = useState('');
  const [enableBackground, setEnableBackground] = useState(true);

  const proactiveChatHandler = useRef<number | null>(null);

  const sender = session?.character;
  const context = session?.context;
  const avatarWithStatus = (
    <AvatarWithStatus status={getSenderStatus(session)} src={sender?.avatar} />
  );

  const ENABLE_BACKGROUND_KEY = 'MessagesPane.enableBackground';

  const errorMessageRecord = useCallback(
    (message?: string) => {
      const content = new ChatContentDTO();
      content.type = 'text';
      content.content = message || t('Sorry, something went wrong!');

      const chatMessage = new ChatMessageDTO();
      chatMessage.contents = [content];
      chatMessage.role = 'assistant';
      chatMessage.name = getSenderName(sender);

      const record = new ChatMessageRecordDTO();
      record.message = chatMessage;

      return record;
    },
    [sender, t]
  );

  const handleSend = useCallback(
    (
      chatId?: string,
      userNickname?: string,
      textToSend?: string,
      processingMessage?: ChatMessageRecordDTO | null
    ) => {
      if (!textToSend || !chatId || processingMessage) {
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

      setMessageToSend(messageRecord);
      setErrorMessage('');
    },
    []
  );

  useEffect(() => {
    if (debugMode) {
      if (context?.chatId) {
        chatApi
          ?.listDebugMessages2(context.chatId)
          .then((resp) => {
            if (!resp) {
              return;
            }
            if (sender?.picture) {
              processBackground(sender.picture, theme.palette.mode, 0.6).then(
                (bg) => {
                  setBackground(bg);
                  setChatMessages(resp);
                }
              );
            } else {
              setBackground('');
              setChatMessages(resp);
            }
            onOpen?.();
          })
          .catch(handleError);
      }
    } else {
      if (context?.chatId) {
        chatApi
          ?.listMessages2(context.chatId)
          .then((resp) => {
            if (!resp) {
              return;
            }
            if (sender?.picture) {
              processBackground(sender.picture, theme.palette.mode, 0.6).then(
                (bg) => {
                  setBackground(bg);
                  setChatMessages(resp);
                }
              );
            } else {
              setBackground('');
              setChatMessages(resp);
            }
            onOpen?.();
          })
          .catch(handleError);
      }
    }
  }, [
    chatApi,
    debugMode,
    context?.chatId,
    handleError,
    theme.palette.mode,
    onOpen,
    sender?.picture,
  ]);

  useEffect(() => {
    const savedEnableBackground = localStorage.getItem(ENABLE_BACKGROUND_KEY);
    if (savedEnableBackground) {
      setEnableBackground(savedEnableBackground === '1');
    }
  }, []);

  useEffect(() => {
    localStorage.setItem(ENABLE_BACKGROUND_KEY, enableBackground ? '1' : '0');
  }, [enableBackground]);

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

    if (proactiveChatWaitingTime > 0 && chatMessages.length > 2) {
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
  }, [
    chatMessages,
    context?.chatId,
    context?.userNickname,
    handleSend,
    messageToSend,
    sender?.lang,
    session?.proactiveChatWaitingTime,
  ]);

  function handleResend(): void {
    if (!context?.chatId) {
      return;
    }
    if (chatMessages.length > 0) {
      setMessageToSend(chatMessages[chatMessages.length - 1]);
    }
    setErrorMessage('');
  }

  function handleReceiveFinish(result: LlmResultDTO | undefined): void {
    setMessageToSend(null);

    if (!result) {
      return;
    }

    const messageRecord = new ChatMessageRecordDTO();
    messageRecord.message = result.message;
    messageRecord.gmtCreate = new Date();
    if (session?.isDebugEnabled) {
      messageRecord.ext = `[${result.tokenUsage?.inputTokenCount},${result.tokenUsage?.outputTokenCount},${result.tokenUsage?.outputTokenCount}]`;
    }

    setChatMessages((prevMessages) => [...prevMessages, messageRecord]);
    onReceivedMessage?.(result);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  function handleReceiveError(reason: any): void {
    setMessageToSend(null);
    setErrorMessage(reason?.message ?? '');
  }

  function handleChatReplay(index: number): void {
    if (!context?.chatId || index >= chatMessages.length) {
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
        setChatMessages(newChatMessages);
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
        getMessageText(record.message) !== proactiveChatPrompt
    );
  }

  return (
    <Stack
      sx={{
        height: { xs: 'calc(100dvh - var(--Footer-height))', sm: '100dvh' },
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
        debugMode={debugMode}
        disabled={!!messageToSend}
        enableBackground={background ? enableBackground : undefined}
        setEnableBackground={setEnableBackground}
        setDebugMode={setDebugMode}
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
        <Stack spacing={2} justifyContent="flex-end">
          {getDisplayMessages().map((record, index) => {
            const message = record.message as ChatMessageDTO;
            const isYou = message.role === 'user';
            return (
              <Stack
                key={`message-container-${index}`}
                direction="row"
                spacing={2}
                flexDirection={isYou ? 'row-reverse' : 'row'}
              >
                {!isYou && avatarWithStatus}
                <ChatBubble
                  key={`message-${index}`}
                  debugMode={debugMode}
                  session={session}
                  record={record}
                  variant={isYou ? 'sent' : 'received'}
                  onReplay={() => handleChatReplay(index)}
                />
              </Stack>
            );
          })}

          {/* streaming message */}
          {(messageToSend || errorMessage) && context?.chatId && (
            <Stack
              key={`message-container-${getDisplayMessages().length}`}
              direction="row"
              spacing={2}
              flexDirection="row"
            >
              {avatarWithStatus}
              {errorMessage ? (
                <ChatBubble
                  session={session}
                  record={errorMessageRecord(errorMessage)}
                  variant="received"
                  onReplay={handleResend}
                />
              ) : (
                messageToSend && (
                  <ChatBubble
                    session={session}
                    record={messageToSend}
                    variant="received"
                    debugMode={debugMode}
                    apiPath={`/api/v2/chat/send/stream/${context?.chatId}`}
                    onFinish={handleReceiveFinish}
                    onError={handleReceiveError}
                  />
                )
              )}
            </Stack>
          )}
        </Stack>
      </Box>

      {/* input message */}
      <MessageInput
        chatId={context?.chatId}
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
