import { useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Box, DialogContentText, Typography } from '@mui/material';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  ChatSessionDTO,
  ChatUpdateDTO,
  LlmResultDTO,
  MemoryUsageDTO,
  TokenUsageDTO,
} from 'freechat-sdk';
import { ChatInfoPane, ChatsPane, MessagesPane } from '../../components/chat';
import { ConfirmModal } from '../../components';
import { useTranslation } from 'react-i18next';
import { DeleteForeverRounded } from '@mui/icons-material';

export default function Chats() {
  const { id, mode } = useParams();

  const defaultChatId = id;
  const defaultDebugMode = mode === 'debug';

  const { t } = useTranslation(['chat']);
  const { chatApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [sessions, setSessions] = useState<ChatSessionDTO[]>([]);
  const [selectedChatId, setSelectedChatId] = useState<string | undefined>(
    defaultChatId
  );
  const [chatIdDeleted, setChatIdDeleted] = useState<string | null>();
  const [selectedSession, setSelectedSession] = useState<ChatSessionDTO>();
  const [apiKeyValue, setApiKeyValue] = useState('');
  const [memoryUsage, setMemoryUsage] = useState<MemoryUsageDTO>();

  const openChatsPane = useRef(!id);

  useEffect(() => {
    const newSelectedSession = sessions.find(
      (session) => session.context?.chatId === selectedChatId
    );
    setApiKeyValue(newSelectedSession?.context?.apiKeyValue ?? '');
    setSelectedSession(newSelectedSession);
    if (selectedChatId) {
      chatApi?.getMemoryUsage(selectedChatId).then(setMemoryUsage);
    }
  }, [chatApi, selectedChatId, sessions]);

  useEffect(() => {
    chatApi?.listChats().then(setSessions).catch(handleError);
  }, [chatApi, handleError]);

  useEffect(() => {
    if (!sessions || sessions.length === 0) {
      return;
    }

    const defaultSession = sessions.find(
      (session) => session.context?.chatId === defaultChatId
    );
    if (defaultSession) {
      setSelectedChatId(defaultChatId);
    } else {
      setSelectedChatId(sessions[0].context?.chatId);
    }
  }, [defaultChatId, sessions]);

  function handleApiKeyChanged(key: string): void {
    if (!selectedChatId) {
      return;
    }

    const request = new ChatUpdateDTO();
    request.apiKeyValue = key;

    chatApi?.updateChat(selectedChatId, request).then((resp) => {
      if (!resp || !selectedSession?.context) {
        return;
      }

      const context = { ...selectedSession.context, apiKeyValue: key };
      const newSelectedSession = { ...selectedSession, context };
      setSelectedSession(newSelectedSession);
    });
  }

  function handleChatUpdate(
    userNickname: string,
    userProfile: string,
    characterNickname: string,
    about: string,
    onSaved: () => void
  ): void {
    if (!selectedChatId) {
      return;
    }

    const request = new ChatUpdateDTO();
    request.userNickname = userNickname;
    request.userProfile = userProfile;
    request.characterNickname = characterNickname;
    request.about = about;

    chatApi?.updateChat(selectedChatId, request).then((resp) => {
      if (!resp || !selectedSession?.context) {
        return;
      }

      const context = {
        ...selectedSession.context,
        userNickname,
        userProfile,
        characterNickname,
        about,
      };
      const newSelectedSession = { ...selectedSession, context };
      setSelectedSession(newSelectedSession);
      onSaved();
    });
  }

  function handleMessagesPaneOpen(): void {
    if (!selectedChatId) {
      return;
    }

    const request = new ChatUpdateDTO();
    request.gmtRead = new Date();

    chatApi?.updateChat(selectedChatId, request).then((resp) => {
      if (!resp || !selectedSession?.context) {
        return;
      }

      selectedSession.context.gmtRead = request.gmtRead;
    });
  }

  function handleReceivedMessage(result: LlmResultDTO): void {
    const preMessages = memoryUsage?.messageUsage ?? 0;
    const preInputTokens = memoryUsage?.tokenUsage?.inputTokenCount ?? 0;
    const preOutputTokens = memoryUsage?.tokenUsage?.outputTokenCount ?? 0;
    const preTotalTokens = memoryUsage?.tokenUsage?.totalTokenCount ?? 0;

    const deltaMessage = 1;
    const deltaInputTokens = result.tokenUsage?.inputTokenCount ?? 0;
    const deltaOutputTokens = result.tokenUsage?.outputTokenCount ?? 0;
    const deltaTotalTokens =
      result.tokenUsage?.totalTokenCount ??
      deltaInputTokens + deltaOutputTokens;

    const newTokenUsage = new TokenUsageDTO();
    newTokenUsage.inputTokenCount = preInputTokens + deltaInputTokens;
    newTokenUsage.outputTokenCount = preOutputTokens + deltaOutputTokens;
    newTokenUsage.totalTokenCount = preTotalTokens + deltaTotalTokens;

    const newMemoryUsage = new MemoryUsageDTO();
    newMemoryUsage.messageUsage = preMessages + deltaMessage;
    newMemoryUsage.tokenUsage = newTokenUsage;

    setMemoryUsage(newMemoryUsage);
  }

  function handleChatDelete(chatId?: string): void {
    if (chatId) {
      chatApi
        ?.deleteChat(chatId)
        .then(() => chatApi?.listChats().then(setSessions).catch(handleError))
        .finally(() => setChatIdDeleted(null))
        .catch(handleError);
    }
  }

  function getCharacterNickname(chatId?: string | null): string {
    if (!chatId) {
      return '';
    }

    const session = sessions.find((s) => s.context?.chatId === chatId);

    return (
      session?.context?.characterNickname ??
      session?.character?.nickname ??
      session?.character?.name ??
      ''
    );
  }

  function getCharacterName(chatId?: string | null): string {
    if (!chatId) {
      return '';
    }

    const session = sessions.find((s) => s.context?.chatId === chatId);

    return session?.character?.name ?? '';
  }

  return (
    <Box
      sx={{
        flex: 1,
        width: '100%',
        mx: 'auto',
        display: 'grid',
        backgroundColor: 'background.default',
        gridTemplateColumns: {
          xs: '1fr',
          sm: '3fr 11fr 6fr',
        },
      }}
    >
      <Box
        sx={{
          position: { xs: 'fixed', sm: 'sticky' },
          transform: {
            xs: 'translateX(calc(100% * (var(--ChatsPane-slideIn, 0) - 1)))',
            sm: 'none',
          },
          transition: 'transform 0.2s, width 0.2s',
          zIndex: 100,
          width: '100%',
          backgroundColor: 'background.default',
        }}
      >
        <ChatsPane
          sessions={sessions}
          selectedChatId={selectedChatId}
          open={openChatsPane.current}
          onSelectChat={(chatId) => {
            openChatsPane.current = false;
            setSelectedChatId(chatId);
          }}
          onRemoveChat={setChatIdDeleted}
        />
      </Box>

      <MessagesPane
        key={selectedSession?.context?.chatId}
        session={selectedSession}
        defaultDebugMode={defaultDebugMode}
        onOpen={handleMessagesPaneOpen}
        onReceivedMessage={handleReceivedMessage}
      />

      <Box
        sx={{
          position: { xs: 'fixed', sm: 'sticky' },
          transform: {
            xs: 'translateX(calc(100% * (var(--ChatInfoPane-slideIn, 0) + 1)))',
            sm: 'none',
          },
          transition: 'transform 0.2s, width 0.2s',
          zIndex: 100,
          width: '100%',
          backgroundColor: 'background.default',
        }}
      >
        <ChatInfoPane
          session={selectedSession}
          memoryUsage={memoryUsage}
          apiKeyValue={apiKeyValue}
          onApiKeyChanged={handleApiKeyChanged}
          onSave={handleChatUpdate}
        />
      </Box>

      <ConfirmModal
        open={!!chatIdDeleted}
        onClose={() => setChatIdDeleted(null)}
        obj={chatIdDeleted}
        dialog={{
          color: 'error',
          title: t('Do you really want to delete this chat?'),
        }}
        button={{
          color: 'error',
          text: t('button:Delete'),
          startIcon: <DeleteForeverRounded />,
        }}
        onConfirm={handleChatDelete}
      >
        <DialogContentText sx={{ whiteSpace: 'nowrap' }}>
          {getCharacterNickname(chatIdDeleted)}
          {getCharacterNickname(chatIdDeleted) !==
            getCharacterName(chatIdDeleted) && (
            <Typography
              variant="body2"
              component="span"
            >{`@${getCharacterName(chatIdDeleted)}`}</Typography>
          )}
        </DialogContentText>
      </ConfirmModal>
    </Box>
  );
}
