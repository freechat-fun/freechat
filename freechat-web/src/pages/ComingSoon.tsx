import { useTranslation } from "react-i18next";
import { Box, Card, Link, Typography } from "@mui/joy";

export default function ComingSoon() {
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
          <Typography level="title-lg">
            {t('Coming soon!')}
          </Typography>
          <Typography level="body-md" sx={{ wordWrap: 'pre-wrap' }}>
            {t('Refer to the')} <Link href="/public/docs/api">Open API</Link> {t('documentation')}
          </Typography>
      </Card>
      </Box>
  );
}