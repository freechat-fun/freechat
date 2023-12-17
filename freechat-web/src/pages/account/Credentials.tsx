import { useTranslation } from "react-i18next";
import { Tab, TabList, Tabs, Typography, tabClasses } from "@mui/joy";
import { StyledBreadcrumbs } from "../../components";

export default function Credentials() {
  const { t } = useTranslation('account');
  const breadcrumbs = {
    'Home': '/w',
    'My Credentials': undefined,
  };

  return (
    <>
      <StyledBreadcrumbs breadcrumbs={breadcrumbs} />
      <Typography level="title-lg" sx={{ mt: 1, mb: 2 }}>
        {t('My Credentials')}
      </Typography>
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
      </Tabs>
    </>
  );
}
