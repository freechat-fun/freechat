import { useTranslation } from 'react-i18next';
import { Box, Link } from '@mui/joy';

export default function FreeChatLogo() {
  const { t } = useTranslation();

  return (
    <Link
      href="/w"
      color="neutral"
      underline="none"
      sx={{
        display: { xs: 'none', md: 'flex' },
        alignItems: 'center',
        fontStyle: 'italic',
        textDecoration: 'none',
      }}
    >
      <Box
        sx={{
          height: '60%',
          display: 'flex',
          alignItems: 'center',
          pb: 0.3,
        }}
      >
        <img
          alt={t('FreeChat')}
          src="/w/freechat_small.png"
          style={{ height: '100%' }}
        />
      </Box>
      <Box sx={{ pl: 1 }}>{t('FreeChat')}</Box>
    </Link>
  );
}
