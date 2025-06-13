import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Chip,
  IconButton,
  TextField,
  List,
  Stack,
  Typography,
  InputAdornment,
} from '@mui/material';
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

  if (open) {
    openChatsPane();
  } else {
    closeChatsPane();
  }

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
    <Stack
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
          variant="h6"
          component="h1"
          fontWeight="bold"
          sx={{ mr: 'auto', display: 'flex', alignItems: 'center', gap: 1 }}
        >
          {t('Messages')}
          <Chip
            label={chats.length}
            color="primary"
            size="small"
            sx={{ ml: 1 }}
          />
        </Typography>
        <Stack direction="row" spacing={1}>
          <IconButton
            size="small"
            onClick={(event) => {
              event.stopPropagation();
              setEditMode(!editMode);
            }}
            onTouchStart={(event) => {
              event.stopPropagation();
            }}
            sx={{ display: 'unset' }}
          >
            {editMode ? <PlaylistAddCheckRounded /> : <EditNoteRounded />}
          </IconButton>
          <IconButton
            size="small"
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
        <TextField
          size="small"
          fullWidth
          placeholder={t('Search')}
          aria-label={t('Search')}
          onChange={handleInputChange}
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRounded />
                </InputAdornment>
              ),
            },
          }}
        />
      </Box>
      <List
        sx={{
          py: 0,
          '& .MuiListItem-root': {
            px: 2,
          },
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
    </Stack>
  );
}
