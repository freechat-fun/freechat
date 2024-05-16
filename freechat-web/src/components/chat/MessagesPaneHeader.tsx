import { useTranslation } from "react-i18next";
import { Avatar, Chip, Dropdown, IconButton, ListItemDecorator, Menu, MenuButton, MenuItem, Stack, Switch, Typography } from "@mui/joy";
import { ArrowBackIosNewRounded, CheckRounded, CircleRounded, DeleteForeverRounded, InfoRounded, MoreVertRounded } from "@mui/icons-material";
import { ChatSessionDTO } from "freechat-sdk";
import { getSenderName, getSenderStatus, getSenderStatusColor, toggleChatInfoPane, toggleMessagesPane } from "../../libs/chat_utils";
import { CommonBox } from "..";

type MessagesPaneHeaderProps = {
  session?: ChatSessionDTO;
  debugMode?: boolean,
  disabled?: boolean;
  enableBackground?: boolean;
  setEnableBackground?: (enabled: boolean) => void,
  setDebugMode?: (debugMode: boolean) => void,
  onClearHistory?: (chatId: string) => void;
};

export default function MessagesPaneHeader(props: MessagesPaneHeaderProps) {
  const { session, debugMode, disabled = false, enableBackground, setEnableBackground, setDebugMode, onClearHistory } = props;
  const { t } = useTranslation('chat');

  const sender = session?.character;
  const nickname = session?.context?.characterNickname ?? getSenderName(sender);
  const status = getSenderStatus(session);
  const iconColorMapper: {[key: string]: 'success' | 'warning' | 'disabled'} = {
    'success': 'success',
    'warning': 'warning',
    'neutral': 'disabled',
  }

  function handleClearHistory(): void {
    if (onClearHistory && session?.context?.chatId) {
      onClearHistory(session.context.chatId);
    }
  }

  return (
    <Stack
      direction="row"
      justifyContent="space-between"
      sx={{
        borderBottom: '1px solid',
        borderColor: 'divider',
      }}
      py={{ xs: 2, md: 2 }}
      px={{ xs: 1, md: 2 }}
    >
      <Stack direction="row" spacing={{ xs: 1, md: 2 }} alignItems="center">
        <IconButton
          variant="plain"
          color="neutral"
          size="sm"
          sx={{
            display: { xs: 'inline-flex', sm: 'none' },
          }}
          onClick={toggleMessagesPane}
        >
          <ArrowBackIosNewRounded />
        </IconButton>
        <Avatar size="lg" src={sender?.avatar} />
        <div>
          <Typography
            fontWeight="lg"
            fontSize="lg"
            component="h2"
            noWrap
            endDecorator={
              <Chip
                variant="outlined"
                size="sm"
                color="neutral"
                sx={{
                  borderRadius: 'sm',
                }}
                startDecorator={
                  <CircleRounded sx={{ fontSize: 8 }} color={iconColorMapper[getSenderStatusColor(status)]} />
                }
                slotProps={{ root: { component: 'span' } }}
              >
                {t(status)}
              </Chip>
            }
          >
            {nickname}
          </Typography>
          { sender && <Typography level="body-sm">@{sender.name}</Typography> }
        </div>
      </Stack>
      <Stack spacing={1} direction="row" alignItems="center">
        {session?.isDebugEnabled && (
          <CommonBox sx={{gap: 2}}>
            <Typography level="body-sm">{t('Debug')}</Typography>
            <Switch disabled={disabled} checked={debugMode} onChange={() => setDebugMode?.(!debugMode)} />
          </CommonBox>
        )}
        <Dropdown>
          <MenuButton
            disabled={disabled}
            slots={{ root: IconButton }}
            slotProps={{ root: { variant: 'plain', color: 'neutral' } }}
          >
            <MoreVertRounded />
          </MenuButton>
          <Menu>
            <MenuItem onClick={toggleChatInfoPane} sx={{
              display: { xs: 'inherit', sm: 'none' },
            }}>
              <ListItemDecorator>
                <InfoRounded />
              </ListItemDecorator>
              {t('Chat Information')}
            </MenuItem>
            <MenuItem
              disabled={enableBackground === undefined}
              onClick={() => setEnableBackground?.(!enableBackground)}
            >
              <ListItemDecorator>
                {enableBackground && <CheckRounded />}
              </ListItemDecorator>
              {t('Enable Background')}
            </MenuItem>
            <MenuItem onClick={handleClearHistory}>
              <ListItemDecorator>
                <DeleteForeverRounded />
              </ListItemDecorator>
              {t('Clear History')}
            </MenuItem>
          </Menu>
        </Dropdown>
      </Stack>
    </Stack>
  );
}
