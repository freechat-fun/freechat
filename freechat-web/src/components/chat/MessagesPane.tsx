import { Fragment, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Box, IconButton, Sheet, Stack } from "@mui/joy";
import { AvatarWithStatus, ChatBubble, MessageInput, MessagesPaneHeader } from "."
import { ChatContentDTO, ChatMessageDTO, ChatMessageRecordDTO, ChatSessionDTO, LlmResultDTO } from "freechat-sdk";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { getSenderName, getSenderStatus } from "../../libs/chat_utils";
import { ReplayCircleFilledRounded } from "@mui/icons-material";

type MessagesPaneProps = {
  session?: ChatSessionDTO;
  defaultDebugMode?: boolean;
  onOpen?: () => void;
};

export default function MessagesPane(props: MessagesPaneProps) {
  const { session, defaultDebugMode = false, onOpen } = props;
  const { t } = useTranslation('chat');
  const { chatApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [chatMessages, setChatMessages] = useState<ChatMessageRecordDTO[]>([]);
  const [textAreaValue, setTextAreaValue] = useState('');
  const [messageToSend, setSessageToSend] = useState<ChatMessageRecordDTO | null>(null);
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

    setSessageToSend(messageRecord);
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

    setSessageToSend(null);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  function handleReceiveError(): void {
    // setFailedToSend(true);
    // handleError(reason);
  }

  return (
    <Sheet
      sx={(theme) => ({
        height: { xs: 'calc(100dvh - var(--Header-height))', lg: '100dvh' },
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: theme.palette.background.level1,
      })}
    >
      <MessagesPaneHeader
        session={session}
        onClearHistory={handleClearMemory}
        debugMode={debugMode}
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
