import { useTranslation } from "react-i18next";
import { AspectRatio, Box, Card, Stack, Tab, TabList, TabPanel, Tabs, Typography, tabClasses, useColorScheme } from "@mui/joy";
import { LinePlaceholder } from "../components";
import { PromptGallery } from "../components/prompt";

export default function Home() {
  const { t } = useTranslation(['sidebar']);
  const { mode } = useColorScheme();

  return (
    <>
      <LinePlaceholder />
      <Stack>
        <Card sx={{
          display: 'flex',
          flexDirection: 'row',
          alignItems: 'center',
          boxShadow: 'lg',
        }}>
          <Box sx={{
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}>
            <Typography level="h1">{t('Welcome to FreeChat')}</Typography>
            <LinePlaceholder spacing={1} />
            <Typography level="title-md">{t('Create your friends here!')}</Typography>
          </Box>
          <AspectRatio
            variant="plain"
            ratio={1}
            sx={{
              width: 240,
            }}
          >
            <img src={mode === 'dark' ? '/img/freechat_dark.png' : '/img/freechat_light.png'} />
          </AspectRatio>
        </Card>
        <LinePlaceholder spacing={6} />
        <Tabs
          defaultValue={1}
          sx={{
            bgcolor: 'transparent',
          }}
        >
          <TabList
            tabFlex={1}
            size="sm"
            sx={{
              pl: { xs: 0, md: 4 },
              justifyContent: 'left',
              [`&& .${tabClasses.root}`]: {
                fontWeight: '600',
                flex: 'initial',
                color: 'text.tertiary',
                [`&.${tabClasses.selected}`]: {
                  bgcolor: 'transparent',
                  color: 'text.primary',
                  '&::after': {
                    height: '2px',
                    bgcolor: 'primary.500',
                  },
                },
              },
            }}
          >
            <Tab disabled sx={{borderRadius: '6px 6px 0 0'}} value={0}>{t('Characters')}</Tab>
            <Tab sx={{borderRadius: '6px 6px 0 0'}} value={1}>{t('Prompts')}</Tab>
          </TabList>
          <TabPanel value={1} key="prompt-gallery-panel">
            <PromptGallery />
          </TabPanel>
        </Tabs>
      </Stack>
    </>
  );
}