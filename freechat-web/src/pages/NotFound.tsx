import { useTranslation } from 'react-i18next';
import { Box, Stack, Typography } from '@mui/material';
import { RouterLink } from '../components';

export default function NotFound() {
  const { t } = useTranslation();

  return (
    <Box
      sx={{
        height: '100dvh',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        minWidth: 0,
        gap: 1,
        overflow: 'auto',
      }}
    >
      <Stack
        sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-evenly',
          alignItems: 'center',
        }}
      >
        <Typography variant="h5" gutterBottom>
          {t('Page not found')}
        </Typography>
        <Typography variant="body1">
          {t('Sorry, the page you are looking for does not exist.')}
        </Typography>
        <Typography variant="body1">
          {t('You can always go back to the')}{' '}
          <RouterLink href="/w" sx={{ cursor: 'pointer' }}>
            {t('homepage')}
          </RouterLink>
          {t('.')}
        </Typography>
      </Stack>
    </Box>
  );
}
