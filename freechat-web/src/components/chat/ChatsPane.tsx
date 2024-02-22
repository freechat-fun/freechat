import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Box, Chip, IconButton, Input, List, Sheet, Stack, Typography } from "@mui/joy";
import { ChatSessionDTO } from "freechat-sdk";
import { ChatListItem } from ".";
import { getSenderName, toggleMessagesPane } from "../../libs/chat_utils";
import { CloseRounded, EditNoteRounded, SearchRounded } from "@mui/icons-material";
import { useDebounce } from "../../libs/ui_utils";
import { escapeRegExp } from "../../libs/js_utils";

type ChatsPaneProps = {
  sessions: ChatSessionDTO[];
  selectedChatId?: string;
  onSelectChat: (chatId: string | undefined) => void;
};

export default function ChatsPane(props: ChatsPaneProps) {
  const { t } = useTranslation('chat');
  
  const { sessions, selectedChatId, onSelectChat } = props;

  const [chats, setChats] = useState([...sessions]);
  const [keyWord, setKeyWord] = useState('');
  const debouncedSearchTerm = useDebounce<string>(keyWord, 200);

  useEffect(() => {
    if (debouncedSearchTerm) {
      const escapedKeyWord = escapeRegExp(debouncedSearchTerm);
      const pattern = new RegExp(escapedKeyWord, 'i');
      const filteredChats = sessions.filter(session => pattern.test(getSenderName(session.character)));
      setChats(filteredChats);
    } else {
      setChats([...sessions]);
    }
  }, [debouncedSearchTerm, sessions]);

  function handleInputChange(event: React.ChangeEvent<HTMLInputElement>): void {
    if (event.target.value !== keyWord) {
      setKeyWord(event.target.value);
    }
  }


  return (
    <Sheet
      sx={{
        borderRight: '1px solid',
        borderColor: 'divider',
        height: '100dvh',
        overflowY: 'auto',
      }}
    >
      <Stack
        direction="row"
        spacing={1}
        alignItems="center"
        justifyContent="space-between"
        p={2}
        pb={1.5}
      >
        <Typography
          fontSize={{ xs: 'md', md: 'lg' }}
          component="h1"
          fontWeight="lg"
          endDecorator={
            <Chip
              variant="soft"
              color="primary"
              size="md"
              slotProps={{ root: { component: 'span' } }}
            >
              {chats.length}
            </Chip>
          }
          sx={{ mr: 'auto' }}
        >
          {t('Messages')}
        </Typography>
        <IconButton
          variant="plain"
          aria-label="edit"
          color="neutral"
          size="sm"
          sx={{ display: { xs: 'none', sm: 'unset' } }}
        >
          <EditNoteRounded />
        </IconButton>
        <IconButton
          variant="plain"
          aria-label="edit"
          color="neutral"
          size="sm"
          onClick={() => {
            toggleMessagesPane();
          }}
          sx={{ display: { sm: 'none' } }}
        >
          <CloseRounded />
        </IconButton>
      </Stack>
      <Box sx={{ px: 2, pb: 1.5 }}>
        <Input
          size="sm"
          startDecorator={<SearchRounded />}
          placeholder={t('Search')}
          aria-label={t('Search')}
          onChange={handleInputChange}
        />
      </Box>
      <List
        sx={{
          py: 0,
          '--ListItem-paddingY': '0.75rem',
          '--ListItem-paddingX': '1rem',
        }}
      >
        {chats.map((session, index) => (
          <ChatListItem
            key={session.context?.chatId || `chat-session-${index}`}
            session={session}
            onSelectChat={onSelectChat}
            selectedChatId={selectedChatId}
            highlight={debouncedSearchTerm}
          />
        ))}
      </List>
    </Sheet>
  );
}
