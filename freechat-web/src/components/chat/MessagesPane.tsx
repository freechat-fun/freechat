import { Fragment, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Box, IconButton, Sheet, SheetProps, Stack } from "@mui/joy";
import { AvatarWithStatus, ChatBubble, MessageInput, MessagesPaneHeader } from "."
import { ChatContentDTO, ChatMessageDTO, ChatMessageRecordDTO, ChatSessionDTO, LlmResultDTO } from "freechat-sdk";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { getSenderName, getSenderStatus } from "../../libs/chat_utils";
import { ReplayCircleFilledRounded } from "@mui/icons-material";

type MessagesPaneProps = SheetProps & {
  session?: ChatSessionDTO;
  defaultDebugMode?: boolean;
  onOpen?: () => void;
};

export default function MessagesPane(props: MessagesPaneProps) {
  const { session, defaultDebugMode = false, onOpen, sx, ...others } = props;
  const { t } = useTranslation('chat');
  const { chatApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [chatMessages, setChatMessages] = useState<ChatMessageRecordDTO[]>([]);
  const [textAreaValue, setTextAreaValue] = useState('');
  const [messageToSend, setMessageToSend] = useState<ChatMessageRecordDTO | null>(null);
  const [failedToSend, setFailedToSend] = useState(false);
  const [debugMode, setDebugMode] = useState(defaultDebugMode);

  const sender = session?.character;
  const context = session?.context;

  const errorMessage = useCallback(() => {
    const content = new ChatContentDTO();
    content.type = 'text';
    content.content = t('Sorry, something went wrong!');

    const message = new ChatMessageDTO();
    message.contents = [content];
    message.role = 'assistant';
    message.name = getSenderName(sender);
    
    const record = new ChatMessageRecordDTO();
    record.message = message;

    return record;
  }, [sender, t]);


  useEffect(() => {
    context?.chatId && chatApi?.listMessages(context.chatId)
      .then(resp => {
        if (!resp) {
          return;
        }
        setChatMessages(resp);
        onOpen?.();
      })
      .catch(handleError);
  }, [chatApi, context?.chatId, handleError, onOpen]);

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
    content.content = textAreaValue;

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
  }

  function handleReceiveFinish(result: LlmResultDTO): void {
    const messageRecord = new ChatMessageRecordDTO();
    messageRecord.message = result.message;
    messageRecord.gmtCreate = new Date();
    if (session?.isDebugEnabled) {
      messageRecord.ext = `[${result.tokenUsage?.inputTokenCount},${result.tokenUsage?.outputTokenCount},${result.tokenUsage?.outputTokenCount}]`;
    }

    setChatMessages((prevMessages) => {
      return [...prevMessages, messageRecord];
    });

    setMessageToSend(null);
  }

  function handleReceiveError(): void {
    setFailedToSend(true);
    // handleError(reason);
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
        height: { xs: 'calc(100dvh - var(--Header-height))', lg: '100dvh' },
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: 'background.level1',
        ...sx
      }}
      {...others}
    >
      <MessagesPaneHeader
        session={session}
        onClearHistory={handleClearMemory}
        debugMode={debugMode}
        disabled={!!messageToSend}
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
          {chatMessages.filter(record => !!record.message).map((record: ChatMessageRecordDTO, index: number) => {
            const message = record.message as ChatMessageDTO;
            const isYou = message.role === 'user';
            return (
              <Stack
                key={`message-container-${record.messageId ?? index}`}
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
          {messageToSend && context?.chatId && (
            <Stack
              direction="row"
              spacing={2}
              flexDirection="row" 
            >
              <AvatarWithStatus
                status={getSenderStatus(session)}
                src={sender?.avatar}
              />
              {failedToSend ? (
                <Fragment>
                  <ChatBubble
                    session={session}
                    record={errorMessage()}
                    variant="received"
                  />
                  <IconButton onClick={() => setFailedToSend(false)}>
                    <ReplayCircleFilledRounded fontSize="small" />
                  </IconButton>
                </Fragment>
              ) : (
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
