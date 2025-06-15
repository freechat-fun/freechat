import { Fragment, useEffect, useMemo, useState } from 'react';
import {
  Box,
  ListItem,
  ListItemButton,
  ListItemButtonProps,
  Stack,
  Typography,
  Divider,
} from '@mui/material';
import { ChatSessionDTO } from 'freechat-sdk';
import {
  getSenderName,
  getSenderReply,
  getSenderStatus,
  toggleChatsPane,
} from '../../libs/chat_utils';
import { AvatarWithStatus } from '.';
import {
  GridBox,
  HighlightedTypography,
  TransparentIconButton,
} from '..';
import { CircleRounded, RemoveCircleRounded } from '@mui/icons-material';
import { getDateLabel } from '../../libs/date_utils';
import { useTranslation } from 'react-i18next';
import { getMessageText } from '../../libs/template_utils';

type ChatListItemProps = ListItemButtonProps & {
  session: ChatSessionDTO;
  selectedChatId?: string;
  highlight?: string;
  editMode?: boolean;
  onSelectChat?: () => void;
  onRemoveChat?: () => void;
};

export default function ChatListItem(props: ChatListItemProps) {
  const {
    session,
    selectedChatId,
    highlight,
    editMode = false,
    onSelectChat,
    onRemoveChat,
  } = props;

  const { i18n } = useTranslation();

  const [selected, setSelected] = useState(
    selectedChatId === session.context?.chatId
  );
  const [context, setContext] = useState(session.context);
  const [sender, setSender] = useState(session.character);
  const [nickname, setNickname] = useState(
    session.context?.characterNickname ?? getSenderName(sender)
  );
  const [record, setRecord] = useState(session.latestMessageRecord);

  const unread = useMemo(() => {
    if (!context?.gmtRead || !record?.gmtCreate) {
      return false;
    }
    return context?.gmtRead < record.gmtCreate;
  }, [context?.gmtRead, record?.gmtCreate]);

  useEffect(() => {
    setSelected(selectedChatId === session.context?.chatId);
    setContext(session.context);
    setSender(session.character);
    setNickname(
      session.context?.characterNickname ?? getSenderName(session.character)
    );
    setRecord(session.latestMessageRecord);
  }, [
    selectedChatId,
    session.character,
    session.context,
    session.latestMessageRecord,
  ]);

  return (
    <Fragment>
      <ListItem disablePadding>
        <ListItemButton
          onClick={() => {
            if (!editMode) {
              toggleChatsPane();
              onSelectChat?.();
            }
          }}
          selected={selected}
          sx={{
            flexDirection: 'column',
            alignItems: 'initial',
            gap: 1,
            mx: 0,
          }}
        >
          <GridBox
            sx={{
              alignItems: 'center',
              gridTemplateColumns: editMode ? '80% 20%' : 'auto 1fr',
            }}
          >
            <Stack>
              <Stack direction="row" spacing={1.5}>
                <AvatarWithStatus
                  status={getSenderStatus(session)}
                  src={sender?.avatar}
                />
                <HighlightedTypography
                  highlight={highlight}
                  fontWeight="bold"
                  fontSize="1.125rem"
                  component="h2"
                  noWrap
                >
                  {nickname}
                </HighlightedTypography>
                <Box
                  sx={{
                    lineHeight: 1.5,
                    textAlign: 'right',
                  }}
                >
                  {unread && (
                    <CircleRounded sx={{ fontSize: 12 }} color="primary" />
                  )}
                  {record?.gmtCreate && (
                    <Typography
                      variant="caption"
                      sx={{ display: { xs: 'none', md: 'block' } }}
                      noWrap
                    >
                      {getDateLabel(record.gmtCreate, i18n.language)}
                    </Typography>
                  )}
                </Box>
              </Stack>
              <Typography
                variant="body2"
                sx={{
                  display: '-webkit-box',
                  WebkitLineClamp: '2',
                  WebkitBoxOrient: 'vertical',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  maxWidth: { xs: '100%', sm: '15vw' },
                }}
              >
                {getSenderReply(
                  getMessageText(record?.message) ?? '',
                  false,
                  true
                )}
              </Typography>
            </Stack>
            {editMode && (
              <TransparentIconButton
                size="small"
                onClick={(event) => {
                  event.stopPropagation();
                  onRemoveChat?.();
                }}
                onTouchStart={(event) => {
                  event.stopPropagation();
                }}
              >
                <RemoveCircleRounded fontSize="small" />
              </TransparentIconButton>
            )}
          </GridBox>
        </ListItemButton>
      </ListItem>
      <Divider sx={{ mx: 2, my: 0 }} />
    </Fragment>
  );
}
