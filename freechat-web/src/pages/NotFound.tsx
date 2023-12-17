import { useTranslation } from "react-i18next";
import { Box, Card, Typography } from "@mui/joy";
import { RouterLink } from "../components";

export default function NotFound() {
  const { t } = useTranslation();

  return (
    <Box
      component="main"
      className="MainContent"
      sx={{
        height: '100vh',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        minWidth: 0,
        gap: 1,
        overflow: 'auto',
      }}
    >
      <Card variant="plain" sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-evenly',
        alignItems: 'center',
        mb: 'calc(10=0px + var(--Header-height))',
      }}>
          <Typography level="title-lg" gutterBottom>
            {t('Page not found')}
          </Typography>
          <Typography level="body-md">
            {t('Sorry, the page you are looking for does not exist.')}
          </Typography>
          <Typography level="body-md">
            {t('You can always go back to the')}{' '}
            <RouterLink
              href = "/w"
              sx={{ cursor: 'pointer' }}
            >
              {t('homepage')}
            </RouterLink>{t('.')}
          </Typography>
      </Card>
    </Box>
  );
}