import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import {
  AspectRatio,
  Button,
  Card,
  CardActions,
  CardOverflow,
  FormControl,
  FormLabel,
  Input,
  Stack,
  Textarea,
  Typography,
  Radio,
  RadioGroup,
  Grid,
  Avatar,
  Divider,
} from '@mui/joy';
import { DoneRounded, SaveAltRounded } from '@mui/icons-material';
import { ImagePicker, LinePlaceholder } from '../../components';
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

  const labelGridUnits = 3;
  const valueGridUnits = 12 - labelGridUnits;

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
      <Typography level="title-lg" sx={{ ml: 3 }}>
        {t('My details')}
      </Typography>
      <Divider />
      <LinePlaceholder />
      <form onSubmit={handleSubmit}>
        <Card
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'stretch',
            maxWidth: '800px',
            minWidth: '540px',
            mx: 'auto',
            gap: 2,
          }}
        >
          <Stack direction="row" spacing={3} sx={{ display: 'flex', my: 1 }}>
            <Grid
              container
              spacing={2}
              alignItems="center"
              sx={{ flexGrow: 1 }}
            >
              <Grid xs={labelGridUnits}>
                <FormLabel>{t('Username')}</FormLabel>
              </Grid>
              <Grid xs={valueGridUnits}>
                <Typography>{username || ''}</Typography>
              </Grid>

              <Grid xs={labelGridUnits}>
                <FormLabel>{t('Coming from')}</FormLabel>
              </Grid>
              <Grid xs={valueGridUnits}>
                <Typography>{platform || ''}</Typography>
              </Grid>

              <Grid xs={labelGridUnits}>
                <FormLabel>{t('Gender')}</FormLabel>
              </Grid>
              <Grid xs={valueGridUnits}>
                <RadioGroup
                  name="genderGroup"
                  orientation="horizontal"
                  value={currentGender}
                  onChange={handleGenderChange}
                  sx={{ my: 1 }}
                >
                  <Radio
                    value="male"
                    label={t('Male')}
                    disabled={!editEnabled}
                  />
                  <Radio
                    value="female"
                    label={t('Female')}
                    disabled={!editEnabled}
                  />
                  <Radio
                    value="other"
                    label={t('Other')}
                    disabled={!editEnabled}
                  />
                </RadioGroup>
              </Grid>

              <Grid xs={labelGridUnits}>
                <FormLabel>{t('Nickname')}</FormLabel>
              </Grid>
              <Grid xs={valueGridUnits}>
                <Input
                  disabled={!editEnabled}
                  name="nickname"
                  placeholder={currentNickname}
                  value={currentNickname}
                  onChange={handleNicknameChange}
                />
              </Grid>
              <Grid xs={12}>
                <FormControl>
                  <FormLabel>
                    {t(
                      'Describe yourself: (This will make characters know you better.)'
                    )}
                  </FormLabel>
                  <Textarea
                    disabled={!editEnabled}
                    name="description"
                    value={currentDescription}
                    minRows={3}
                    onChange={handleDescriptionChange}
                  />
                </FormControl>
              </Grid>
            </Grid>
            <Stack direction="column" spacing={1} sx={{ minWidth: 120 }}>
              <AspectRatio
                ratio="1"
                maxHeight={200}
                sx={{ flex: 1, borderRadius: '50%' }}
              >
                <Avatar variant="soft" src={currentAvatar} />
              </AspectRatio>
              <ImagePicker
                onImageSelect={handleImageSelect}
                previewProps={{
                  maxWidth: '200px',
                  maxHeight: '200px',
                  borderRadius: '50%',
                }}
                disabled={!editEnabled}
                aria-label="upload new picture"
                size="sm"
                variant="outlined"
                color="neutral"
                sx={{
                  bgcolor: 'background.body',
                  position: 'absolute',
                  zIndex: 2,
                  borderRadius: '50%',
                  right: 20,
                  top: 110,
                  boxShadow: 'sm',
                }}
              />
            </Stack>
          </Stack>
          <CardOverflow>
            <CardActions sx={{ alignSelf: 'flex-end', pt: 2 }}>
              <Button
                size="sm"
                variant="solid"
                type="submit"
                startDecorator={saved ? <DoneRounded /> : <SaveAltRounded />}
                disabled={saved}
              >
                {t('button:Save')}
              </Button>
            </CardActions>
          </CardOverflow>
        </Card>
      </form>
    </>
  );
}
