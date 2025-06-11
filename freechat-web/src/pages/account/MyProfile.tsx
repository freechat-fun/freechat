import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import {
  Button,
  FormControl,
  FormLabel,
  Stack,
  Typography,
  Radio,
  RadioGroup,
  FormControlLabel,
  Avatar,
  Divider,
  Box,
} from '@mui/material';
import { DoneRounded, SaveAltRounded } from '@mui/icons-material';
import {
  CommonGridBox,
  ContentTextarea,
  ImagePicker,
  LinePlaceholder,
  StyledStack,
  TinyInput,
} from '../../components';
import { UserDetailsDTO } from 'freechat-sdk';
import { getCompressedImage } from '../../libs/ui_utils';

export default function MyProfile() {
  const { t } = useTranslation(['account', 'button']);
  const { accountApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username, platform, resetUser } = useMetaInfoContext();

  const [currentGender, setCurrentGender] = useState('other');
  const [currentNickname, setCurrentNickname] = useState('');
  const [currentDescription, setCurrentDescription] = useState('');
  const [currentAvatar, setCurrentAvatar] = useState<string>();
  const [editEnabled, setEditEnabled] = useState(false);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    getUserInfo();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [accountApi]);

  function getUserInfo(): void {
    accountApi
      ?.getUserDetails()
      .then((userDetails) => {
        const name = userDetails.username;
        const nick = userDetails.nickname;
        const from = userDetails.platform;
        const sex = userDetails.gender;
        const profile = userDetails.profile;
        const picture = userDetails.picture;

        if (name !== username || from !== platform) {
          resetUser(name, from);
        }
        if (picture) {
          setCurrentAvatar(picture);
        }
        if (sex) {
          setCurrentGender(sex);
        }
        if (nick) {
          setCurrentNickname(nick);
        }
        if (profile) {
          setCurrentDescription(profile);
        } else {
          setCurrentDescription(getDefaultDescription(nick || name, sex));
        }

        setEditEnabled(true);
      })
      .catch(handleError);
  }

  function getDefaultDescription(
    name: string | undefined,
    gender: string | undefined
  ): string {
    if (gender === 'male') {
      return `${t('Name description', { name })}\n${t('Male description')}`;
    } else if (gender === 'female') {
      return `${t('Name description', { name })}\n${t('Female description')}`;
    } else {
      return t('Name description', { name });
    }
  }

  function handleGenderChange(
    event: React.ChangeEvent<HTMLInputElement>
  ): void {
    if (event.target.value !== currentGender) {
      setCurrentGender(event.target.value);
      setSaved(false);
    }
  }

  function handleNicknameChange(
    event: React.ChangeEvent<HTMLInputElement>
  ): void {
    if (event.target.value !== currentNickname) {
      setCurrentNickname(event.target.value);
      setSaved(false);
    }
  }

  function handleDescriptionChange(
    event: React.ChangeEvent<HTMLTextAreaElement>
  ): void {
    if (event.target.value !== currentDescription) {
      setCurrentDescription(event.target.value);
      setSaved(false);
    }
  }

  function handleImageSelect(file: Blob, name: string) {
    getCompressedImage(file, 1024 * 1024)
      .then((imageInfo) => {
        const request = new File([imageInfo.blob], name);
        accountApi
          ?.uploadUserPicture(request)
          .then((url) => setCurrentAvatar(url))
          .catch(handleError);
      })
      .catch(handleError);
  }

  function handleSubmit(event: React.FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    const userDetails = new UserDetailsDTO();

    if (currentGender) {
      userDetails.gender = currentGender;
    }

    if (currentNickname) {
      userDetails.nickname = currentNickname;
    }

    if (currentDescription) {
      userDetails.profile = currentDescription;
    }

    if (currentAvatar) {
      userDetails.picture = currentAvatar;
    }

    accountApi
      ?.updateUserInfo(userDetails)
      .then((result) => result && setSaved(true))
      .catch(handleError);
  }

  return (
    <>
      <LinePlaceholder spacing={6} />
      <Typography variant="h5" sx={{ ml: 3 }}>
        {t('My details')}
      </Typography>
      <Divider />
      <LinePlaceholder />
      <form onSubmit={handleSubmit}>
        <StyledStack
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'stretch',
            maxWidth: '800px',
            minWidth: '540px',
            mx: 'auto',
            '&:hover, &:focus-within': { transform: 'none' },
          }}
        >
          <Stack direction="row" spacing={3} sx={{ display: 'flex', my: 1 }}>
            <CommonGridBox sx={{ flexGrow: 1, gridTemplateColumns: '1fr 3fr' }}>
              <FormLabel>{t('Username')}</FormLabel>
              <Typography>{username || ''}</Typography>

              <FormLabel>{t('Coming from')}</FormLabel>
              <Typography>{platform || ''}</Typography>

              <FormLabel>{t('Gender')}</FormLabel>
              <RadioGroup
                name="genderGroup"
                row
                value={currentGender}
                onChange={handleGenderChange}
              >
                <FormControlLabel
                  value="male"
                  control={<Radio size="small" />}
                  label={t('Male')}
                  disabled={!editEnabled}
                  sx={{
                    '& .MuiFormControlLabel-label': {
                      fontSize: '0.9rem',
                    },
                  }}
                />
                <FormControlLabel
                  value="female"
                  control={<Radio size="small" />}
                  label={t('Female')}
                  disabled={!editEnabled}
                  sx={{
                    '& .MuiFormControlLabel-label': {
                      fontSize: '0.9rem',
                    },
                  }}
                />
                <FormControlLabel
                  value="other"
                  control={<Radio size="small" />}
                  label={t('Other')}
                  disabled={!editEnabled}
                  sx={{
                    '& .MuiFormControlLabel-label': {
                      fontSize: '0.9rem',
                    },
                  }}
                />
              </RadioGroup>

              <FormLabel>{t('Nickname')}</FormLabel>
              <TinyInput
                disabled={!editEnabled}
                name="nickname"
                placeholder={currentNickname}
                value={currentNickname}
                onChange={handleNicknameChange}
                fullWidth
                slotProps={{
                  input: {
                    size: 'small',
                    sx: { fontSize: '0.9rem' },
                  },
                }}
                sx={{
                  maxWidth: undefined,
                }}
              />

              <FormControl fullWidth sx={{ gridColumn: 'span 2', gap: 0.5 }}>
                <FormLabel>
                  {t(
                    'Describe yourself: (This will make characters know you better.)'
                  )}
                </FormLabel>
                <ContentTextarea
                  disabled={!editEnabled}
                  name="description"
                  value={currentDescription}
                  minRows={3}
                  onChange={handleDescriptionChange}
                  sx={{
                    fontSize: '0.9rem',
                  }}
                />
              </FormControl>
            </CommonGridBox>
            <Stack
              direction="column"
              spacing={1}
              sx={{ minWidth: 120, position: 'relative' }}
            >
              <Box
                sx={{
                  position: 'relative',
                  width: '100%',
                  paddingBottom: '100%',
                  borderRadius: '50%',
                  overflow: 'hidden',
                }}
              >
                <Avatar
                  src={currentAvatar}
                  sx={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: '100%',
                  }}
                />
              </Box>
              <ImagePicker
                key="picture-picker"
                onImageSelect={handleImageSelect}
                previewProps={{
                  maxWidth: '200px',
                  maxHeight: '200px',
                  borderRadius: '50%',
                }}
                disabled={!editEnabled}
                aria-label="upload new picture"
                size="small"
                sx={{
                  backgroundColor: 'background.paper',
                  position: 'absolute',
                  zIndex: 2,
                  borderRadius: '50%',
                  right: 5,
                  top: 85,
                  boxShadow: 1,
                  '&:hover, &:focus-within': {
                    backgroundColor: 'background.paper',
                  },
                }}
              />
            </Stack>
          </Stack>
          <Box sx={{ alignSelf: 'flex-end', pt: 2 }}>
            <Button
              size="small"
              variant="contained"
              type="submit"
              startIcon={saved ? <DoneRounded /> : <SaveAltRounded />}
              disabled={saved}
            >
              {t('button:Save')}
            </Button>
          </Box>
        </StyledStack>
      </form>
    </>
  );
}
