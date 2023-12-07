import { useTranslation } from 'react-i18next';
import { Box, Typography } from "@mui/joy";
import logoUrl from '../resources/freechat_s.png';

export default function FreeChatLogo() {
  const { t } = useTranslation();

  return (
    <Box sx={{ gap: 1, display: 'flex', alignItems: "center" }}>
      <img alt="FreeChat" src={logoUrl} height="70%" />
      <Typography level="title-lg" color="neutral" sx={{
        fontStyle: 'italic',
        mt: 1,
      }}>
        {t('FreeChat')}
      </Typography>
    </Box>
  );
}