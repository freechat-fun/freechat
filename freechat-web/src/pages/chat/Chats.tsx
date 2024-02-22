import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { Sheet } from "@mui/joy";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { ChatSessionDTO, ChatUpdateDTO } from "freechat-sdk";
import { ChatInfoPane, ChatsPane, MessagesPane } from "../../components/chat";

export default function Chats() {
  const { id, mode } = useParams();

  const defaultChatId = id;
  const defaultDebugMode = mode === 'debug';

  const { chatApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [sessions, setSessions] = useState<ChatSessionDTO[]>([]);
  const [selectedChatId, setSelectedChatId] = useState<string | undefined>(defaultChatId);

  const selectedSession = useMemo(() => {
    return sessions.find(session => session.context?.chatId === selectedChatId);
  }, [selectedChatId, sessions]);

  useEffect(() => {
    chatApi?.listChats()
      .then(setSessions)
      .catch(handleError);
  }, [chatApi, handleError]);

  useEffect(() => {
    if (!sessions || sessions.length === 0) {
      return;
    }

    const defaultSession = sessions.find(session => session.context?.chatId === defaultChatId);
    if (defaultSession) {
      setSelectedChatId(defaultChatId);
    } else {
      setSelectedChatId(sessions[0].context?.chatId);
    }
  }, [defaultChatId, sessions]);

  function handleChatUpdate(
    userNickname: string,
    userProfile: string,
    characterNickname: string,
    about: string,
    onSaved: () => void,
  ): void {
    if (!selectedChatId) {
      return;
    }

    const request = new ChatUpdateDTO();
    request.userNickname = userNickname;
    request.userProfile = userProfile;
    request.characterNickname = characterNickname;
    request.about = about;

    chatApi?.updateChat(selectedChatId, request)
      .then(resp => {
        if (!resp || !selectedSession?.context) {
          return;
        }

        selectedSession.context.userNickname = userNickname;
        selectedSession.context.userProfile = userProfile;
        selectedSession.context.characterNickname = characterNickname;
        selectedSession.context.about = about;

        onSaved();
      });
  }

  function handleMessagesPaneOpen(): void {
    if (!selectedChatId) {
      return;
    }

    const request = new ChatUpdateDTO();
    request.gmtRead = new Date();

    chatApi?.updateChat(selectedChatId, request)
      .then(resp => {
        if (!resp || !selectedSession?.context) {
          return;
        }

        selectedSession.context.gmtRead = request.gmtRead;
      });
  }

  return (
    <Sheet
      sx={{
        flex: 1,
        width: '100%',
        mx: 'auto',
        display: 'grid',
        gridTemplateColumns: {
          xs: '1fr',
          sm: 'minmax(min-content, min(25%, 400px)) 1fr minmax(min-content, min(25%, 400px))',
        },
      }}
    >
      <Sheet
        sx={{
          position: { xs: 'fixed', sm: 'sticky' },
          transform: {
            xs: 'translateX(calc(100% * (var(--MessagesPane-slideIn, 0) - 1)))',
            sm: 'none',
          },
          transition: 'transform 0.4s, width 0.4s',
          zIndex: 100,
          width: '100%',
        }}
      >
        <ChatsPane
          sessions={sessions}
          selectedChatId={selectedChatId}
          onSelectChat={setSelectedChatId}
        />
      </Sheet>

      <MessagesPane
        session={selectedSession}
        defaultDebugMode={defaultDebugMode}
        onOpen={handleMessagesPaneOpen}
      />

      <Sheet
        sx={{
          position: { xs: 'none', sm: 'sticky' },
          width: '100%',
        }}
      >
        <ChatInfoPane
          session={selectedSession}
          onSave={handleChatUpdate}
        />
      </Sheet>
    </Sheet>
  );
}