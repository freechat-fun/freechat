/* eslint-disable prettier/prettier */
import { useTranslation } from 'react-i18next';
import {
  Box,
  Paper,
  Stack,
  Tab,
  Tabs,
  Typography,
} from '@mui/material';
import { LinePlaceholder } from '../components';
import { PromptGallery } from '../components/prompt';
import {
  CharacterGallery,
  CharacterRecommendationViews,
} from '../components/character';
import { useColorScheme } from '@mui/material';
import { useState } from 'react';

export default function Home() {
  const { t } = useTranslation('sidebar');
  const { mode } = useColorScheme();
  const [tabValue, setTabValue] = useState(0);

  const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  return (
    <>
      <LinePlaceholder />
      <Stack sx={{ display: { xs: 'none', sm: 'inherit' } }}>
        <Stack
          sx={{
            display: 'flex',
            flexDirection: 'row',
            alignItems: 'center',
            m: 2,
            p: 2,
            boxShadow: 1,
            borderRadius: '6px',
            border: `1px solid divider`,
            gap: 2,
          }}
        >
          <Box
            sx={{
              flex: 1,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              minWidth: '160px',
            }}
          >
            <Typography variant="h4" sx={{ fontWeight: 'bold' }}>{t('Welcome to FreeChat')}</Typography>
            <LinePlaceholder spacing={1} />
            <Typography variant="subtitle1">
              {t('Create friends for yourself')}
            </Typography>
          </Box>
          <Box
            sx={{
              width: 240,
              height: 240,
              position: 'relative',
              overflow: 'hidden',
            }}
          >
            <img
              src={
                mode === 'dark'
                  ? '/img/freechat_dark.png'
                  : '/img/freechat_light.png'
              }
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'cover',
              }}
            />
          </Box>
        </Stack>

        <LinePlaceholder spacing={6} />

        <CharacterRecommendationViews sx={{ mb: 6, width: '80%' }} />

        <Box sx={{ width: '100%' }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs
              value={tabValue}
              onChange={handleTabChange}
              sx={{
                '& .MuiTabs-indicator': {
                  backgroundColor: 'primary.main',
                },
                '& .MuiTab-root': {
                  textTransform: 'none',
                  fontWeight: 600,
                  color: 'text.secondary',
                  '&.Mui-selected': {
                    color: 'text.primary',
                  },
                },
              }}
            >
              <Tab label={t('Characters')} />
              <Tab label={t('Prompts')} />
            </Tabs>
          </Box>
          <Box
            role="tabpanel"
            hidden={tabValue !== 0}
            id="character-tabpanel"
            aria-labelledby="character-tab"
          >
            {tabValue === 0 && <CharacterGallery all={false} />}
          </Box>
          <Box
            role="tabpanel"
            hidden={tabValue !== 1}
            id="prompt-tabpanel"
            aria-labelledby="prompt-tab"
          >
            {tabValue === 1 && <PromptGallery />}
          </Box>
        </Box>
      </Stack>

      <Stack sx={{ display: { xs: 'inherit', sm: 'none' } }}>
        <Stack
          sx={{
            display: 'flex',
            flexDirection: 'row',
            alignItems: 'center',
            m: 0,
            p: 2,
            gap: 2,
          }}
        >
          <Box
            sx={{
              flex: 1,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'left',
              minWidth: '160px',
            }}
          >
            <Typography variant="h4" sx={{ fontWeight: 'bold' }}>{t('Welcome to FreeChat')}</Typography>
            <LinePlaceholder spacing={1} />
            <Typography variant="subtitle1">
              {t('Create friends for yourself')}
            </Typography>
          </Box>
          <Box
            sx={{
              width: 240,
              height: 240,
              position: 'relative',
              overflow: 'hidden',
            }}
          >
            <img
              src={
                mode === 'dark'
                  ? '/img/freechat_dark.png'
                  : '/img/freechat_light.png'
              }
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'cover',
              }}
            />
          </Box>
        </Stack>

        <LinePlaceholder />
        <Paper
          sx={{
            mx: 2,
            py: 1,
            px: 2,
            bgcolor: 'background.paper',
          }}
        >
          <Typography variant="body2">
            {t(
              'If you want to create your own characters, please visit this website on PC.'
            )}
          </Typography>
        </Paper>
        <CharacterGallery all={true} />
      </Stack>
    </>
  );
}
