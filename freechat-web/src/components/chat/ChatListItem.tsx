import { Fragment, useEffect, useMemo, useState } from "react";
import { Box, IconButton, ListDivider, ListItem, ListItemButton, ListItemButtonProps, Stack, Typography } from "@mui/joy";
import { ChatSessionDTO } from "freechat-sdk";
import { getSenderName, getSenderStatus, toggleMessagesPane } from "../../libs/chat_utils";
import { AvatarWithStatus } from ".";
import { CommonGridBox, HighlightedTypography } from "..";
import { CircleRounded, RemoveCircleRounded } from "@mui/icons-material";
import { getDateLabel } from "../../libs/date_utils";
import { useTranslation } from "react-i18next";
import { getMessageText } from "../../libs/template_utils";

type ChatListItemProps = ListItemButtonProps & {
  session: ChatSessionDTO;
  selectedChatId?: string;
  highlight?: string;
  editMode?: boolean;
  onSelectChat?: () => void;
  onRemoveChat?: () => void;
};

export default function ChatListItem(props: ChatListItemProps) {
  const { i18n } = useTranslation();
  
  const { session, selectedChatId, highlight, editMode = false, onSelectChat, onRemoveChat } = props;

  const [selected, setSelected] = useState(selectedChatId === session.context?.chatId);
  const [context, setContext] = useState(session.context);
  const [sender, setSender] = useState(session.character);
  const [nickname, setNickname] = useState(session.context?.characterNickname ?? getSenderName(sender));
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
    setNickname(session.context?.characterNickname ?? getSenderName(session.character));
    setRecord(session.latestMessageRecord);
  }, [selectedChatId, session.character, session.context, session.latestMessageRecord]);

  return (
    <Fragment>
      <ListItem>
        <ListItemButton
          onClick={() => {
            toggleMessagesPane();
            onSelectChat?.();
          }}
          selected={selected}
          color="neutral"
          sx={{
            flexDirection: 'column',
            alignItems: 'initial',
            gap: 1,
          }}
        >
          <CommonGridBox sx={{
            alignItems: 'center',
            gridTemplateColumns: editMode ? '80% 20%' : 'auto 1fr',
          }}>
            <Stack>
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
            </Stack>
          {editMode && (
            <IconButton size="sm" sx={{px: 1}} onClick={() => onRemoveChat?.()}>
              <RemoveCircleRounded fontSize="small" />
            </IconButton>
          )}
          </CommonGridBox>
        </ListItemButton>
      </ListItem>
      <ListDivider sx={{ margin: 0 }} />
    </Fragment>
  );
}