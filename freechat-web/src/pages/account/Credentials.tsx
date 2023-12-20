import { useTranslation } from "react-i18next";
import { Tab, TabList, TabPanel, Tabs, tabClasses } from "@mui/joy";
import { Breadcrumbsbar } from "../../components";
import { ApiTokenPanel } from "../../components/account";
import AiApiKeyPanel from "../../components/account/AiApiKeyPanel";

export default function Credentials() {
  const { t } = useTranslation(['account', 'button']);

  const breadcrumbs = {
    'Home': '/w',
    'My Credentials': undefined,
  };

  return (
    <>
      <Breadcrumbsbar breadcrumbs={breadcrumbs} />
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
          <Tab sx={{ borderRadius: '6px 6px 0 0' }} indicatorInset value={0}>
            {t('Site')}
          </Tab>
          <Tab sx={{ borderRadius: '6px 6px 0 0' }} indicatorInset value={1}>
            OpenAI
          </Tab>
          <Tab sx={{ borderRadius: '6px 6px 0 0' }} indicatorInset value={2}>
            DashScope
          </Tab>
        </TabList>
        <TabPanel value={0}>
          <ApiTokenPanel />
        </TabPanel>
        <TabPanel value={1}>
          <AiApiKeyPanel provider="open_ai" />
        </TabPanel>
        <TabPanel value={2}>
          <AiApiKeyPanel provider="dash_scope" />
        </TabPanel>
      </Tabs>
    </>
  );
}
