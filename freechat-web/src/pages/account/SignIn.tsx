import { useTranslation } from 'react-i18next';
import { Box, Button, Stack, Typography } from "@mui/joy";
import { GitHub, Google } from '@mui/icons-material';
import { AliyunIcon  } from '../../components/icon';

export default function SignIn() {
  const { t } = useTranslation('sign-in');

  const protocol = window.location.protocol;
  const host = window.location.hostname;
  const port = window.location.port;
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const guestUsername = 'guest';
  const guestPassword = 'guest';

  function toUrl(path: string) {
    return `${protocol}//${host}${port ? `:${port}` : ''}${path}`;
  }

  function handleClick(path: string) {
    return window.location.href = toUrl(path);
  }

  return (
    <Stack direction='row'>
      <Box
        sx={(theme) => ({
          width:
            'clamp(100vw - var(--Cover-width), (var(--Collapsed-breakpoint) - 100vw) * 999, 100vw)',
          transition: 'width var(--Transition-duration)',
          transitionDelay: 'calc(var(--Transition-duration) + 0.1s)',
          position: 'relative',
          zIndex: 1,
          display: 'flex',
          justifyContent: 'flex-end',
          backdropFilter: 'blur(12px)',
          backgroundColor: 'rgba(255 255 255 / 0.2)',
          [theme.getColorSchemeSelector('dark')]: {
            backgroundColor: 'rgba(19 19 24 / 0.4)',
          },
          flexBasis: '50%',
        })}
      >
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            minHeight: '100dvh',
            width:
              'clamp(var(--Form-maxWidth), (var(--Collapsed-breakpoint) - 100vw) * 999, 100%)',
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
            <Stack gap={4} sx={{ mb: 2 }}>
              <Stack gap={1}>
                <Typography level="h3">{t('Sign in')}</Typography>
                {/* <Typography level="body-sm">
                  {t('Don\'t have an account?')}
                  <Link href="#replace-with-a-link" level="title-sm">
                    {t('Sign up!')}
                  </Link>
                </Typography> */}
              </Stack>
              <Button
                variant="soft"
                color="neutral"
                fullWidth
                startDecorator={<AliyunIcon />}
                onClick={() => handleClick('/oauth2/authorization/aliyun')}
              >
                {t('Continue with Aliyun')}
              </Button>
              <Button
                variant="soft"
                color="neutral"
                fullWidth
                startDecorator={<GitHub />}
                onClick={() => handleClick('/oauth2/authorization/github')}
              >
                {t('Continue with GitHub')}
              </Button>
              <Button
                variant="soft"
                color="neutral"
                fullWidth
                startDecorator={<Google />}
                onClick={() => handleClick('/oauth2/authorization/google')}
              >
                {t('Continue with Google')}
              </Button>
              <Box sx={{ display: 'flex', justifyContent: 'center', }}>
                <form method="post" action="/login">
                  <input type="hidden" value={guestUsername} id="username" name="username" />
                  <input type="hidden" value={guestPassword} id="password" name="password" />
                  <input type="hidden" name="_csrf" value={csrfToken ?? ''} />
                  <Button
                    type="submit"
                    variant="plain"
                    color="primary"
                    sx={{
                      textDecoration: 'underline',
                      p: 0,
                      '&:hover': {
                        backgroundColor: 'Background'
                      }
                  }}>
                    {t('I\'m a guest')}
                  </Button>
                </form>
              </Box>
            </Stack>
            {/* <Divider
              sx={(theme) => ({
                [theme.getColorSchemeSelector('light')]: {
                  color: { xs: '#FFF', md: 'text.tertiary' },
                  '--Divider-lineColor': {
                    xs: '#FFF',
                    md: 'var(--joy-palette-divider)',
                  },
                },
              })}
            >
              {t('or')}
            </Divider>
            <Stack gap={4} sx={{ mt: 2 }}>
              <form method="post" action="/login">
                <FormControl required>
                  <FormLabel>{t('Username')}</FormLabel>
                  <Input type="text" name="username" />
                </FormControl>
                <FormControl required>
                  <FormLabel>{t('Password')}</FormLabel>
                  <Input type="password" name="password" />
                </FormControl>
                <Stack gap={4} sx={{ mt: 2 }}>
                  <Box
                    sx={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                    }}
                  >
                    <Checkbox size="sm" label={t('Remember me')} name="persistent" />
                    <Link level="title-sm" href="#replace-with-a-link">
                    {t('Forgot your password?')}
                    </Link>
                  </Box>
                  <Button type="submit" fullWidth>
                  {t('Sign in')}
                  </Button>
                </Stack>
                <input type="hidden" name="_csrf" value={csrfToken} />
              </form>
            </Stack> */}
          </Box>
        </Box>
      </Box>
      <Box
        id="sign-in-cover"
        sx={(theme) => ({
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
          [theme.getColorSchemeSelector('dark')]: {
            backgroundImage:
              'url(/img/sign_in_dark.jpg), linear-gradient(to right, rgba(0,0,0,1) 0%, rgba(0,0,0,0) 100%)',
          },
        })}
      />
    </Stack>
  );
}