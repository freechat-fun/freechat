import { useTranslation } from 'react-i18next';
import { Box, Link, Stack, Typography } from '@mui/material';

export default function ComingSoon() {
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
          gap: 2,
        }}
      >
        <Typography variant="h6">{t('Coming soon!')}</Typography>
        <Typography variant="body1" sx={{ wordWrap: 'pre-wrap' }}>
          {t('Refer to the')} <Link href="/w/docs">Open API</Link>{' '}
          {t('documentation')}
        </Typography>
      </Stack>
    </Box>
  );
}
