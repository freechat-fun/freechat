import { useTranslation } from 'react-i18next';
import { Tab, Tabs, Box } from '@mui/material';
import { ApiTokenPanel, AiApiKeyPanel } from '../../components/account';
import { providers } from '../../configs/model-providers-config';
import { LinePlaceholder } from '../../components';
import { useState } from 'react';

export default function Credentials() {
  const { t } = useTranslation(['account', 'button']);
  const [value, setValue] = useState(0);

  const handleChange = (_event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };

  return (
    <>
      <LinePlaceholder spacing={6} />
      <Box sx={{ bgcolor: 'transparent' }}>
        <Tabs
          value={value}
          onChange={handleChange}
          sx={{
            pl: { xs: 0, md: 4 },
            '& .MuiTabs-indicator': {
              backgroundColor: 'primary.main',
              height: '2px',
            },
          }}
        >
          <Tab
            label={t('Site')}
            sx={{
              fontWeight: 600,
              color: 'text.secondary',
              '&.Mui-selected': {
                color: 'text.primary',
                bgcolor: 'transparent',
              },
            }}
          />
          {providers
            .filter((provider) => provider.enableApiKey)
            .map((provider, index) => (
              <Tab
                key={`ai-model-provider-tab-${index}`}
                label={provider.label}
                sx={{
                  fontWeight: 600,
                  color: 'text.secondary',
                  '&.Mui-selected': {
                    color: 'text.primary',
                    bgcolor: 'transparent',
                  },
                }}
              />
            ))}
        </Tabs>

        <Box sx={{ mt: 2 }}>
          {value === 0 && <ApiTokenPanel />}
          {providers
            .filter((provider) => provider.enableApiKey)
            .map(
              (provider, index) =>
                value === index + 1 && (
                  <AiApiKeyPanel
                    key={`ai-model-provider-panel-${index}`}
                    provider={provider.provider}
                  />
                )
            )}
        </Box>
      </Box>
    </>
  );
}
