import { useTranslation } from 'react-i18next';
import { GitHub, Google } from "@mui/icons-material";
import { Box, Button, Stack, Typography } from "@mui/joy";
import { AliyunIcon  }from '../components';

export default function SignIn() {
  const { t } = useTranslation('sign-in');
  const protocol = window.location.protocol;
  const host = window.location.hostname;
  const port = window.location.port;
  // const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');

  function toUrl(path) {
    return `${protocol}//${host}${port ? `:${port}` : ''}${path}`;
  }

  function handleClick(path) {
    return window.location.href = toUrl(path);
  }

  return (
    <Stack direction='row' justifyContent='center'>
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
                startDecorator={<GitHub />}
                onClick={() => handleClick('/oauth2/authorization/github')}
              >
                {t('Continue with GitHub')}
              </Button>
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
                disabled
                variant="soft"
                color="neutral"
                fullWidth
                startDecorator={<Google />}
                onClick={() => handleClick('/oauth2/authorization/google')}
              >
                {t('Continue with Google')}
              </Button>
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
        sx={(theme) => ({
          flexBasis: '50%',
          right: 0,
          top: 0,
          bottom: 0,
          left: 'clamp(0px, (100vw - var(--Collapsed-breakpoint)) * 999, 100vw - var(--Cover-width))',
          transition:
            'background-image var(--Transition-duration), left var(--Transition-duration) !important',
          transitionDelay: 'calc(var(--Transition-duration) + 0.1s)',
          backgroundColor: 'background.level1',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          backgroundImage:
            'url(https://images.unsplash.com/photo-1527181152855-fc03fc7949c8?auto=format&w=1000&dpr=2)',
          [theme.getColorSchemeSelector('dark')]: {
            backgroundImage:
              'url(https://images.unsplash.com/photo-1572072393749-3ca9c8ea0831?auto=format&w=1000&dpr=2)',
          },
        })}
      />
    </Stack>
  );
}