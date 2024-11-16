import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Button,
  Card,
  Chip,
  Divider,
  FormControl,
  IconButton,
  Input,
  Stack,
  Typography,
} from '@mui/joy';
import { ChatSessionDTO, MemoryUsageDTO } from 'freechat-sdk';
import {
  CommonBox,
  CommonGridBox,
  ContentTextarea,
  LinePlaceholder,
  TextareaTypography,
} from '..';
import {
  CheckRounded,
  CloseRounded,
  SaveAltRounded,
} from '@mui/icons-material';
import { getLocaleLabel } from '../../configs/i18n-config';
import { ChatInfoUsage } from '.';
import { toggleChatInfoPane } from '../../libs/chat_utils';

type ChatInfoPaneProps = {
  session?: ChatSessionDTO;
  memoryUsage?: MemoryUsageDTO;
  apiKeyValue?: string;
  onApiKeyChanged?: (key: string) => void;
  onSave?: (
    userNickname: string,
    userProfile: string,
    characterNickname: string,
    about: string,
    onSaved: () => void
  ) => void;
};

export default function ChatInfoPane(props: ChatInfoPaneProps) {
  const { t } = useTranslation(['chat', 'character']);

  const { session, memoryUsage, apiKeyValue, onApiKeyChanged, onSave } = props;

  const [userNickname, setUserNickname] = useState(
    session?.context?.userNickname ?? ''
  );
  const [userProfile, setUserProfile] = useState(
    session?.context?.userProfile ?? ''
  );
  const [characterNickname, setCharacterNickname] = useState(
    session?.context?.characterNickname ?? ''
  );
  const [about, setAbout] = useState(session?.context?.about ?? '');
  const [saved, setSaved] = useState(false);

  const lang =
    session?.character?.lang?.split('_')?.[0] ??
    session?.character?.lang ??
    'en';
  const tags = session?.character?.tags ?? [];

  useEffect(() => {
    setUserNickname(session?.context?.userNickname ?? '');
    setUserProfile(session?.context?.userProfile ?? '');
    setCharacterNickname(session?.context?.characterNickname ?? '');
    setAbout(session?.context?.about ?? '');
  }, [session]);

  useEffect(() => {
    setSaved(false);
  }, [userNickname, userProfile, characterNickname, about]);

  function getGenderLabel(gender: string | undefined): string {
    switch (gender) {
      case 'male':
        return t('account:Male');
      case 'female':
        return t('account:Female');
      default:
        return t('account:Other');
    }
  }

  return (
    <Card
      sx={{
        p: 2,
        borderRadius: 0,
        borderColor: 'divider',
        height: { xs: 'calc(100dvh - var(--Footer-height))', sm: '100dvh' },
        overflowY: 'auto',
      }}
    >
      <Stack
        direction="row"
        sx={{
          display: { xs: 'inherit', sm: 'none' },
          gap: 1,
        }}
      >
        <IconButton
          variant="plain"
          aria-label="edit"
          color="neutral"
          size="sm"
          onClick={() => {
            toggleChatInfoPane();
          }}
          sx={{ display: { sm: 'none' } }}
        >
          <CloseRounded />
        </IconButton>
        <Divider sx={{ my: 2 }} />
      </Stack>
      <ChatInfoUsage
        session={session}
        memoryUsage={memoryUsage}
        apiKeyValue={apiKeyValue}
        setApiKeyValue={onApiKeyChanged}
      />

      <Divider sx={{ my: 2 }}>
        {t('Character information', { ns: 'character' })}
      </Divider>

      <CommonGridBox>
        <Typography level="title-sm" textColor="neutral">
          {t('Name')}
        </Typography>
        <Typography level="body-sm">{session?.character?.name}</Typography>

        <Typography level="title-sm" textColor="neutral">
          {t('account:Gender')}
        </Typography>
        <Typography level="body-sm">
          {getGenderLabel(session?.character?.gender)}
        </Typography>
        <Typography level="title-sm" textColor="neutral">
          {t('Language')}
        </Typography>
        <Typography level="body-sm">{getLocaleLabel(lang)}</Typography>
      </CommonGridBox>

      <LinePlaceholder spacing={1} />

      <Typography level="title-sm" textColor="neutral">
        {t('Description')}
      </Typography>
      <TextareaTypography level="title-sm" textColor="neutral">
        {session?.character?.description}
      </TextareaTypography>

      {tags.length > 0 && (
        <CommonBox>
          {tags.map((tag, index) => (
            <Chip
              variant="outlined"
              color="success"
              key={`tag-${tag}-${index}`}
            >
              {tag}
            </Chip>
          ))}
        </CommonBox>
      )}

      <LinePlaceholder spacing={1} />

      <Divider sx={{ my: 2 }}>
        {t('Settings that affect chat feedback')}
      </Divider>

      <CommonGridBox>
        <Typography level="title-sm" textColor="neutral">
          {t('Your Nickname')}
        </Typography>
        <Input
          name="info-user-nickname"
          value={userNickname}
          onChange={(event) => setUserNickname(event.target.value)}
        />
      </CommonGridBox>

      <LinePlaceholder spacing={1} />

      <FormControl sx={{ gap: 1 }}>
        <Typography level="title-sm" textColor="neutral">
          {t('Your Profile')}
        </Typography>
        <ContentTextarea
          name="info-user-profile"
          minRows={3}
          maxRows={10}
          value={userProfile}
          onChange={(event) => setUserProfile(event.target.value)}
        />
      </FormControl>

      <LinePlaceholder spacing={1} />

      <CommonGridBox>
        <Typography level="title-sm" textColor="neutral">
          {t('Character Nickname')}
        </Typography>
        <Input
          name="info-character-nickname"
          value={characterNickname}
          onChange={(event) => setCharacterNickname(event.target.value)}
        />
      </CommonGridBox>

      <LinePlaceholder spacing={1} />

      <FormControl sx={{ gap: 1 }}>
        <Typography level="title-sm" textColor="neutral">
          {t('Anything about this chat')}
        </Typography>
        <ContentTextarea
          name="info-about"
          minRows={3}
          maxRows={10}
          value={about}
          onChange={(event) => setAbout(event.target.value)}
        />
      </FormControl>

      <Divider sx={{ my: 2 }}>
        {t('The settings only apply to this chat')}
      </Divider>

      <Box
        sx={{
          display: 'flex',
          justifyContent: 'flex-end',
        }}
      >
        <Button
          disabled={saved || !session?.context?.chatId}
          size="sm"
          variant="outlined"
          color="success"
          startDecorator={saved ? <CheckRounded /> : <SaveAltRounded />}
          onClick={() =>
            onSave?.(userNickname, userProfile, characterNickname, about, () =>
              setSaved(true)
            )
          }
        >
          {t('button:Save')}
        </Button>
      </Box>
    </Card>
  );
}
