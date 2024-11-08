import { useTranslation } from "react-i18next";
import { Tab, TabList, TabPanel, Tabs, tabClasses } from "@mui/joy";
import { ApiTokenPanel, AiApiKeyPanel } from "../../components/account";
import { providers } from "../../configs/model-providers-config";
import { LinePlaceholder } from "../../components";

export default function Credentials() {
  const { t } = useTranslation(['account', 'button']);

  return (
    <>
      <LinePlaceholder spacing={6} />
      <Tabs
        defaultValue={0}
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
          <Tab sx={{ borderRadius: '6px 6px 0 0' }} indicatorInset value={0} key="api-token-tab">
            {t('Site')}
          </Tab>
          {providers.filter(provider => provider.enableApiKey).map((provider, index) => (
            <Tab sx={{ borderRadius: '6px 6px 0 0' }} indicatorInset value={index + 1} key={`ai-model-provider-tab-${index}`}>
              {provider.label}
            </Tab>
          ))}
        </TabList>
        <TabPanel value={0} key="ap-token-panel">
          <ApiTokenPanel />
        </TabPanel>
        {providers.filter(provider => provider.enableApiKey).map((provider, index) => (
          <TabPanel value={index + 1} key={`ai-model-provider-panel-${index}`}>
            <AiApiKeyPanel provider={provider.provider} />
          </TabPanel>
        ))}
      </Tabs>
    </>
  );
}
