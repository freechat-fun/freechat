import { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Box, Sheet, SheetProps, Stack, useColorScheme } from "@mui/joy";
import { AvatarWithStatus, ChatBubble, MessageInput, MessagesPaneHeader } from "."
import { ChatContentDTO, ChatMessageDTO, ChatMessageRecordDTO, ChatSessionDTO, LlmResultDTO } from "freechat-sdk";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { getSenderName, getSenderStatus } from "../../libs/chat_utils";
import { processBackground } from "../../libs/ui_utils";

type MessagesPaneProps = SheetProps & {
  session?: ChatSessionDTO;
  defaultDebugMode?: boolean;
  onOpen?: () => void;
  onReceivedMessage?: (result: LlmResultDTO) => void;
};

export default function MessagesPane(props: MessagesPaneProps) {
  const { session, defaultDebugMode = false, onOpen, onReceivedMessage, sx, ...others } = props;
  const { t } = useTranslation('chat');
  const { mode } = useColorScheme();
  const { chatApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [chatMessages, setChatMessages] = useState<ChatMessageRecordDTO[]>([]);
  const [textAreaValue, setTextAreaValue] = useState('');
  const [messageToSend, setMessageToSend] = useState<ChatMessageRecordDTO | null>(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [debugMode, setDebugMode] = useState(defaultDebugMode);
  const [background, setBackground] = useState('');
  const [enableBackground, setEnableBackground] = useState(true);

  const sender = session?.character;
  const context = session?.context;

  const errorMessageRecord = useCallback((message?: string) => {
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
  }, [sender, t]);


  useEffect(() => {
    context?.chatId && chatApi?.listMessages(context.chatId)
      .then(resp => {
        if (!resp) {
          return;
        }
        setChatMessages(resp);
        sender?.picture && processBackground(sender?.picture, mode, 0.4)
          .then(setBackground);
        onOpen?.();
      })
      .catch(handleError);
  }, [chatApi, context?.chatId, handleError, mode, onOpen, sender, debugMode]);

  useEffect(() => {
    const savedEnableBackground = localStorage.getItem('MessagesPane.enableBackground');
    if (savedEnableBackground) {
      setEnableBackground(savedEnableBackground === '1');
    }
  }, []);

  useEffect(() => {
    localStorage.setItem('MessagesPane.enableBackground', enableBackground ? '1': '0');
  }, [enableBackground]);

  function handleClearMemory(chatId: string): void {
    chatApi?.clearMemory(chatId)
      .then(setChatMessages)
      .catch(handleError);
  }

  function handleSend(): void {
    if (!textAreaValue || !context?.chatId || messageToSend) {
      return;
    }

    const content = new ChatContentDTO();
    content.type = 'text';
    content.content = textAreaValue.trim();

    const userMessage = new ChatMessageDTO();
    userMessage.role = 'user';
    userMessage.contents = [content];
    userMessage.name = context?.userNickname;

    const messageRecord = new ChatMessageRecordDTO();
    messageRecord.message = userMessage;
    messageRecord.gmtCreate = new Date();

    setChatMessages((prevMessages) => {
      return [...prevMessages, messageRecord];
    });

    setMessageToSend(messageRecord);
    setErrorMessage('');
  }

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
    while(chatMessages[rollbackIndex].message?.role !== 'user' && rollbackIndex > 0) {
      rollbackIndex = rollbackIndex - 1;
    }
    const rollbackCount = chatMessages.length - rollbackIndex;

    chatApi?.rollbackMessages(context?.chatId, rollbackCount)
      .then(() => {
        const newMessageToSend = chatMessages[rollbackIndex];
        const newChatMessages = chatMessages.slice(0, rollbackIndex + 1);
        setChatMessages(newChatMessages);
        setMessageToSend(newMessageToSend);
      })
      .catch(handleError);
  }

  return (
    <Sheet
      sx={{
        height: { xs: 'calc(100dvh - var(--Footer-height))', lg: '100dvh' },
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: 'background.level1',
        position: 'relative',
        overflow: 'hidden',
        backgroundImage: `url(${enableBackground ? background : ''})`,
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        ...sx
      }}
      {...others}
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
          {chatMessages.filter(record => !!record.message).map((record, index) => {
            const message = record.message as ChatMessageDTO;
            const isYou = message.role === 'user';
            return (
              <Stack
                key={`message-container-${record.messageId ?? '$' + index}`}
                direction="row"
                spacing={2}
                flexDirection={isYou ? 'row-reverse' : 'row'}
              >
                { !isYou && (
                  <AvatarWithStatus
                    status={getSenderStatus(session)}
                    src={sender?.avatar}
                  />
                )}
                <ChatBubble
                  key={`message-${record.messageId ?? index}`}
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
              direction="row"
              spacing={2}
              flexDirection="row" 
            >
              <AvatarWithStatus
                status={getSenderStatus(session)}
                src={sender?.avatar}
              />
              {errorMessage ? (
                <ChatBubble
                  session={session}
                  record={errorMessageRecord(errorMessage)}
                  variant="received"
                  onReplay={handleResend}
                />
              ) : ( messageToSend && 
                <ChatBubble
                  session={session}
                  record={messageToSend}
                  variant="received"
                  debugMode={debugMode}
                  apiPath={`/api/v1/chat/send/stream/${context?.chatId}`}
                  onFinish={handleReceiveFinish}
                  onError={handleReceiveError}
                />
              )}
            </Stack>
          )}
        </Stack>
      </Box>

      {/* input message */}
      <MessageInput
        disabled={!!messageToSend}
        textAreaValue={textAreaValue}
        setTextAreaValue={setTextAreaValue}
        onSubmit={handleSend}
      />
    </Sheet>
  );
}
