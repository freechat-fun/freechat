import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Box, List, ListItem, ListItemButton, ListItemContent, Sheet, Tooltip, Typography } from "@mui/joy";
import { ArticleRounded } from "@mui/icons-material";
import { RouterLink, FoxIcon } from ".";

export default function ThinSidebar() {
  const { t } = useTranslation('sidebar');
  const { pathname } = useLocation();
  
  const normalizePathname = pathname.replace(/\/+$/, "");
  const relativePathname = normalizePathname.slice('/w/console/'.length);

  const isSelected = (path: string): boolean => {
    return relativePathname === path;
  };
  
  return (
    <Box
      className="Sidebar"
      sx={{
        position: 'fixed',
        zIndex: 9999,
        height: '100vh',
        width: 'var(--ThinSidebar-width)',
        bgcolor: 'transparent',
        boxShadow: 'none',
        p: 2,
        flexShrink: 0,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        gap: 2,
      }}
    >
      <List
        size="sm"
        sx={{
          gap: 1,
          '--List-nestedInsetStart': '30px',
          '--ListItem-radius': (theme) => theme.vars.radius.sm,
        }}
      >
        <ListItem>
          <Tooltip size="sm" title={t('Characters')}>
            <ListItemButton
              component={RouterLink}
              href="/w/console/characters"
              underline="none"
              selected={isSelected('characters')}
              sx={{
                pl: 1.8
              }}
            >
              <FoxIcon />
            </ListItemButton>
          </Tooltip>
        </ListItem>

        <ListItem>
          <Tooltip size="sm" title={t('Prompts')}>
            <ListItemButton
              component={RouterLink}
              href="/w/console/prompts"
              underline="none"
              selected={isSelected('prompts')}
            >
              <ArticleRounded />
            </ListItemButton>
          </Tooltip>
        </ListItem>
      </List>
    </Box>
  );
}