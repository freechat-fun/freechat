import { Fragment, useMemo } from "react";
import { Box, ListDivider, ListItem, ListItemButton, ListItemButtonProps, Stack, Typography } from "@mui/joy";
import { ChatSessionDTO } from "freechat-sdk";
import { getSenderName, getSenderStatus, toggleMessagesPane } from "../../libs/chat_utils";
import { AvatarWithStatus } from ".";
import { HighlightedTypography } from "..";
import { CircleRounded } from "@mui/icons-material";
import { getDateLabel } from "../../libs/date_utils";
import { useTranslation } from "react-i18next";
import { getMessageText } from "../../libs/template_utils";

type ChatListItemProps = ListItemButtonProps & {
  session: ChatSessionDTO;
  selectedChatId?: string;
  onSelectChat?: (chatId: string | undefined) => void;
  highlight?: string;
};

export default function ChatListItem(props: ChatListItemProps) {
  const { i18n } = useTranslation();
  
  const { session, selectedChatId, onSelectChat, highlight } = props;
  const selected = selectedChatId === session.context?.chatId;
  const context = session.context;
  const sender = session.character;
  const nickname = session.context?.characterNickname ?? getSenderName(sender);
  const record = session.latestMessageRecord;

  const unread = useMemo(() => {
    if (!context?.gmtRead || !record?.gmtCreate) {
      return false;
    }
    return context?.gmtRead < record.gmtCreate;
  }, [context?.gmtRead, record?.gmtCreate]);

  return (
    <Fragment>
      <ListItem>
        <ListItemButton
          onClick={() => {
            toggleMessagesPane();
            onSelectChat?.(session.context?.chatId);
          }}
          selected={selected}
          color="neutral"
          sx={{
            flexDirection: 'column',
            alignItems: 'initial',
            gap: 1,
          }}
        >
          <Stack direction="row" spacing={1.5}>
            <AvatarWithStatus
              status={getSenderStatus(session)}
              src={sender?.avatar}
            />
            <HighlightedTypography
              highlight={highlight}
              fontWeight="lg"
              fontSize="lg"
              component="h2"
              noWrap
            >
              {nickname}
            </HighlightedTypography>
            { sender && <Typography level="body-sm">@{sender.name}</Typography> }
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
                  level="body-xs"
                  display={{ xs: 'none', md: 'block' }}
                  noWrap
                >
                  {getDateLabel(record.gmtCreate, i18n.language)}
                </Typography>
              )}
              
            </Box>
          </Stack>
          <Typography
            level="body-sm"
            sx={{
              display: '-webkit-box',
              WebkitLineClamp: '2',
              WebkitBoxOrient: 'vertical',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
            }}
          >
            {getMessageText(record?.message)}
          </Typography>
        </ListItemButton>
      </ListItem>
      <ListDivider sx={{ margin: 0 }} />
    </Fragment>
  );
}