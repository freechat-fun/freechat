import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Button,
  Chip,
  Divider,
  FormControl,
  IconButton,
  Stack,
  Typography,
} from '@mui/material';
import { ChatSessionDTO, MemoryUsageDTO } from 'freechat-sdk';
import {
  CommonBox,
  CommonGridBox,
  ContentTextarea,
  LinePlaceholder,
  TextareaTypography,
  TinyInput,
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
    <Stack
      sx={{
        p: 2,
        height: { xs: 'calc(100dvh - var(--Footer-height))', sm: '100dvh' },
        overflowY: 'auto',
        gap: 1,
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
          aria-label="edit"
          color="default"
          size="small"
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
        <Typography variant="subtitle1" color="text.secondary">
          {t('Name')}
        </Typography>
        <Typography variant="body2">{session?.character?.name}</Typography>

        <Typography variant="subtitle1" color="text.secondary">
          {t('account:Gender')}
        </Typography>
        <Typography variant="body2">
          {getGenderLabel(session?.character?.gender)}
        </Typography>
        <Typography variant="subtitle1" color="text.secondary">
          {t('Language')}
        </Typography>
        <Typography variant="body2">{getLocaleLabel(lang)}</Typography>
      </CommonGridBox>

      <LinePlaceholder spacing={1} />

      <Typography variant="subtitle1" color="text.secondary">
        {t('Description')}
      </Typography>
      <TextareaTypography variant="subtitle2" color="text.secondary">
        {session?.character?.description}
      </TextareaTypography>

      {tags.length > 0 && (
        <CommonBox>
          {tags.map((tag, index) => (
            <Chip
              size="small"
              variant="outlined"
              color="success"
              key={`tag-${tag}-${index}`}
              label={tag}
            />
          ))}
        </CommonBox>
      )}

      <LinePlaceholder spacing={1} />

      <Divider sx={{ my: 2 }}>
        {t('Settings that affect chat feedback')}
      </Divider>

      <CommonGridBox>
        <Typography variant="subtitle1" color="text.secondary">
          {t('Your Nickname')}
        </Typography>
        <TinyInput
          name="info-user-nickname"
          value={userNickname}
          onChange={(event) => setUserNickname(event.target.value)}
          size="small"
          fullWidth
          sx={{ maxWidth: undefined }}
          slotProps={{
            input: {
              size: 'small',
            },
          }}
        />
      </CommonGridBox>

      <LinePlaceholder spacing={1} />

      <FormControl sx={{ gap: 1 }}>
        <Typography variant="subtitle1" color="text.secondary">
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
        <Typography variant="subtitle1" color="text.secondary">
          {t('Character Nickname')}
        </Typography>
        <TinyInput
          name="info-character-nickname"
          value={characterNickname}
          onChange={(event) => setCharacterNickname(event.target.value)}
          size="small"
          fullWidth
          sx={{ maxWidth: undefined }}
          slotProps={{
            input: {
              size: 'small',
            },
          }}
        />
      </CommonGridBox>

      <LinePlaceholder spacing={1} />

      <FormControl sx={{ gap: 1 }}>
        <Typography variant="subtitle1" color="text.secondary">
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
          size="small"
          variant="outlined"
          color="success"
          startIcon={saved ? <CheckRounded /> : <SaveAltRounded />}
          onClick={() =>
            onSave?.(userNickname, userProfile, characterNickname, about, () =>
              setSaved(true)
            )
          }
        >
          {t('button:Save')}
        </Button>
      </Box>
    </Stack>
  );
}
