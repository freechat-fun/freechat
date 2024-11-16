import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Chip,
  IconButton,
  Input,
  List,
  Sheet,
  Stack,
  Typography,
} from '@mui/joy';
import { ChatSessionDTO } from 'freechat-sdk';
import { ChatListItem } from '.';
import {
  closeChatsPane,
  getSenderName,
  openChatsPane,
  toggleChatsPane,
} from '../../libs/chat_utils';
import {
  CloseRounded,
  EditNoteRounded,
  PlaylistAddCheckRounded,
  SearchRounded,
} from '@mui/icons-material';
import { useDebounce } from '../../libs/ui_utils';
import { escapeRegExp } from '../../libs/js_utils';

type ChatsPaneProps = {
  sessions: ChatSessionDTO[];
  selectedChatId?: string;
  open?: boolean;
  onSelectChat?: (chatId: string | undefined) => void;
  onRemoveChat?: (chatId: string | undefined) => void;
};

export default function ChatsPane(props: ChatsPaneProps) {
  const { t } = useTranslation('chat');

  const {
    sessions,
    selectedChatId,
    open = true,
    onSelectChat,
    onRemoveChat,
  } = props;

  const [chats, setChats] = useState([...sessions]);
  const [keyWord, setKeyWord] = useState('');
  const [editMode, setEditMode] = useState(false);
  const debouncedSearchTerm = useDebounce<string>(keyWord, 200);

  open ? openChatsPane() : closeChatsPane();

  useEffect(() => {
    if (debouncedSearchTerm) {
      const escapedKeyWord = escapeRegExp(debouncedSearchTerm);
      const pattern = new RegExp(escapedKeyWord, 'i');
      const filteredChats = sessions.filter((session) =>
        pattern.test(getSenderName(session.character))
      );
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
        height: { xs: 'calc(100dvh - var(--Footer-height))', sm: '100dvh' },
        overflowY: 'auto',
      }}
    >
      <Stack
        direction="row"
        spacing={1}
        alignItems="center"
        justifyContent="space-between"
        px={2}
        pt={2}
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
        <Stack direction="row" sx={{ gap: 1 }}>
          <IconButton
            variant="plain"
            aria-label="edit"
            color="neutral"
            size="sm"
            sx={{ display: 'unset' }}
            onClick={() => setEditMode(!editMode)}
          >
            {editMode ? <PlaylistAddCheckRounded /> : <EditNoteRounded />}
          </IconButton>
          <IconButton
            variant="plain"
            aria-label="edit"
            color="neutral"
            size="sm"
            onClick={() => {
              toggleChatsPane();
            }}
            sx={{ display: { sm: 'none' } }}
          >
            <CloseRounded />
          </IconButton>
        </Stack>
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
            selectedChatId={selectedChatId}
            highlight={debouncedSearchTerm}
            editMode={editMode}
            onSelectChat={() => onSelectChat?.(session.context?.chatId)}
            onRemoveChat={() => onRemoveChat?.(session.context?.chatId)}
          />
        ))}
      </List>
    </Sheet>
  );
}
