import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Button,
  Divider,
  FormControl,
  FormLabel,
  Stack,
  Typography,
  useTheme,
} from '@mui/material';
import { GitHub, Google } from '@mui/icons-material';
import { AliyunIcon } from '../../components/icon';
import { useErrorMessageBusContext, useMetaInfoContext } from '../../contexts';
import { UserFullDetailsDTO } from 'freechat-sdk';
import { TinyInput } from '../../components';

export default function SignIn() {
  const { t } = useTranslation('sign-in');
  const { csrfToken, csrfHeaderName, registrations } = useMetaInfoContext();
  const { handleError } = useErrorMessageBusContext();
  const theme = useTheme();

  const [guestFormState, setGuestFormState] = useState({
    guestUsername: '',
    guestPassword: '',
    isFormReadyToSubmit: false,
  });

  const guestFormRef = useRef<HTMLFormElement | null>(null);

  const protocol = window.location.protocol;
  const host = window.location.hostname;
  const port = window.location.port;

  const GUEST_USERNAME_KEY = 'SignIn.guestUsername';
  const GUEST_PASSWORD_KEY = 'SignIn.guestPassword';

  useEffect(() => {
    const guestUsername = localStorage.getItem(GUEST_USERNAME_KEY) ?? '';
    const guestPassword = localStorage.getItem(GUEST_PASSWORD_KEY) ?? '';

    setGuestFormState((prevState) => ({
      ...prevState,
      guestUsername,
      guestPassword,
    }));
  }, []);

  useEffect(() => {
    if (guestFormState.isFormReadyToSubmit) {
      guestFormRef.current?.submit();
      setGuestFormState((prevState) => ({
        ...prevState,
        isFormReadyToSubmit: false,
      }));
    }
  }, [guestFormState]);

  function toUrl(path: string): string {
    return `${protocol}//${host}${port ? `:${port}` : ''}${path}`;
  }

  function handleOAuth2Click(path: string): void {
    window.location.href = toUrl(path);
  }

  function handleGuestClick(): void {
    if (!guestFormState.guestUsername || !guestFormState.guestPassword) {
      fetch('/public/register/guest', {
        method: 'POST',
        credentials: host === 'localhost' ? 'include' : 'same-origin',
        headers: {
          Accept: 'application/json',
          [csrfHeaderName]: csrfToken ?? '',
        },
      })
        .then((resp) => {
          if (!resp.ok) {
            handleError({ code: resp.status, message: resp.statusText });
            return null;
          }
          return resp.json();
        })
        .then((userInfo: UserFullDetailsDTO | null) => {
          if (userInfo?.username && userInfo.password) {
            localStorage.setItem(GUEST_USERNAME_KEY, userInfo.username);
            localStorage.setItem(GUEST_PASSWORD_KEY, userInfo.password);
            setGuestFormState({
              guestUsername: userInfo.username,
              guestPassword: userInfo.password,
              isFormReadyToSubmit: true,
            });
          }
        })
        .catch(handleError);
    } else {
      setGuestFormState((prevState) => ({
        ...prevState,
        isFormReadyToSubmit: true,
      }));
    }
  }

  return (
    <Stack direction="row">
      <Box
        sx={{
          transition: 'width var(--Transition-duration)',
          transitionDelay: 'calc(var(--Transition-duration) + 0.1s)',
          zIndex: 1,
          display: 'flex',
          width: '100%',
          justifyContent: 'flex-end',
          backdropFilter: 'blur(12px)',
          backgroundColor: 'rgba(255 255 255 / 0.2)',
          ...(theme.palette.mode === 'dark' && {
            backgroundColor: 'rgba(19 19 24 / 0.4)',
          }),
          flexBasis: '50%',
        }}
      >
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            minHeight: {
              xs: 'calc(100dvh - var(--Footer-height))',
              sm: '100dvh',
            },
            maxWidth: '100%',
            px: 2,
          }}
        >
          <Box
            component="main"
            sx={{
              my: 'auto',
              py: 2,
              pb: 5,
              display: 'flex',
              flexDirection: 'column',
              gap: 2,
              width: 400,
              maxWidth: '100%',
              mx: 'auto',
              borderRadius: 'sm',
              '& form': {
                display: 'flex',
                flexDirection: 'column',
                gap: 2,
              },
            }}
          >
            <Stack spacing={4} sx={{ mb: 2 }}>
              <Stack spacing={1}>
                <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                  {t('Sign in')}
                </Typography>
              </Stack>

              {registrations?.includes('github') && (
                <Button
                  variant="contained"
                  color="inherit"
                  fullWidth
                  startIcon={<GitHub />}
                  onClick={() =>
                    handleOAuth2Click('/oauth2/authorization/github')
                  }
                  sx={{
                    backgroundColor: 'background.paper',
                  }}
                >
                  {t('Continue with GitHub')}
                </Button>
              )}

              {registrations?.includes('google') && (
                <Button
                  variant="contained"
                  color="inherit"
                  fullWidth
                  startIcon={<Google />}
                  onClick={() =>
                    handleOAuth2Click('/oauth2/authorization/google')
                  }
                  sx={{
                    backgroundColor: 'background.paper',
                  }}
                >
                  {t('Continue with Google')}
                </Button>
              )}

              {registrations?.includes('aliyun') && (
                <Button
                  variant="contained"
                  color="inherit"
                  fullWidth
                  startIcon={<AliyunIcon />}
                  onClick={() =>
                    handleOAuth2Click('/oauth2/authorization/aliyun')
                  }
                  sx={{
                    backgroundColor: 'background.paper',
                  }}
                >
                  {t('Continue with Aliyun')}
                </Button>
              )}

              {registrations.length > 0 && (
                <Divider sx={{ color: 'text.secondary' }}>{t('or')}</Divider>
              )}

              <form method="post" action="/login">
                <FormControl required fullWidth>
                  <FormLabel>{t('Username')}</FormLabel>
                  <TinyInput
                    type="text"
                    name="username"
                    fullWidth
                    slotProps={{
                      input: {
                        sx: {
                          size: 'small',
                        },
                      },
                    }}
                    sx={{
                      maxWidth: undefined,
                      backgroundColor: 'background.default',
                    }}
                  />
                </FormControl>
                <FormControl required fullWidth>
                  <FormLabel>{t('Password')}</FormLabel>
                  <TinyInput
                    type="password"
                    name="password"
                    fullWidth
                    slotProps={{
                      input: {
                        sx: {
                          size: 'small',
                        },
                      },
                    }}
                    sx={{
                      maxWidth: undefined,
                      backgroundColor: 'background.default',
                    }}
                  />
                </FormControl>
                <Stack spacing={4} sx={{ mt: 2 }}>
                  <Button type="submit" variant="contained" fullWidth>
                    {t('Sign in')}
                  </Button>
                </Stack>
                <input type="hidden" name="_csrf" value={csrfToken ?? ''} />
              </form>

              <form method="post" action="/login" ref={guestFormRef}>
                <input
                  type="hidden"
                  value={guestFormState.guestUsername}
                  id="username"
                  name="username"
                />
                <input
                  type="hidden"
                  value={guestFormState.guestPassword}
                  id="password"
                  name="password"
                />
                <input type="hidden" name="_csrf" value={csrfToken ?? ''} />
                <Button
                  variant="text"
                  color="primary"
                  onClick={handleGuestClick}
                  sx={{
                    textDecoration: 'underline',
                    p: 0,
                    '&:hover': {
                      backgroundColor: 'transparent',
                    },
                  }}
                >
                  {t("I'm a guest")}
                </Button>
              </form>
            </Stack>
          </Box>
        </Box>
      </Box>
      <Box
        id="sign-in-cover"
        sx={{
          height: '100dvh',
          position: 'fixed',
          right: 0,
          top: 0,
          bottom: 0,
          left: 'clamp(0px, (100vw - var(--Collapsed-breakpoint)) * 999, 100vw - var(--Cover-width))',
          transition:
            'background-image var(--Transition-duration), left var(--Transition-duration) !important',
          transitionDelay: 'calc(var(--Transition-duration) + 0.1s)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          backgroundBlendMode: 'overlay',
          backgroundImage:
            'url(/img/sign_in_light.jpg), linear-gradient(to right, rgba(255,255,255,1) 0%, rgba(255,255,255,0) 100%)',
          ...(theme.palette.mode === 'dark' && {
            backgroundImage:
              'url(/img/sign_in_dark.jpg), linear-gradient(to right, rgba(0,0,0,1) 0%, rgba(0,0,0,0) 100%)',
          }),
        }}
      />
    </Stack>
  );
}
