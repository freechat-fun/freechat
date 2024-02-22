import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { Box, List, ListItem, ListItemButton, ListItemContent, Sheet, Typography, listItemButtonClasses } from "@mui/joy";
import { AccountTreeRounded, AndroidRounded, ArticleRounded, ExtensionRounded, KeyboardArrowDownRounded, SettingsRounded } from "@mui/icons-material";
import { RouterLink } from ".";
import { closeSidebar } from "../libs/sidebar_utils";

type NestedListToggleProps = {
  defaultExpanded?: boolean;
  children: React.ReactNode;
  renderToggle: (params: {
    open: boolean;
    setOpen: React.Dispatch<React.SetStateAction<boolean>>;
  }) => React.ReactNode;
}

function NestedListToggle({
  defaultExpanded = false,
  renderToggle,
  children,
}: NestedListToggleProps) {
  const [open, setOpen] = useState(defaultExpanded);
  return (
    <React.Fragment>
      {renderToggle({ open, setOpen })}
      <Box
        sx={{
          display: 'grid',
          gridTemplateRows: open ? '1fr' : '0fr',
          transition: '0.2s ease',
          '& > *': {
            overflow: 'hidden',
          },
        }}
      >
        {children}
      </Box>
    </React.Fragment>
  );
}

export default function Sidebar() {
  const { t } = useTranslation('sidebar');
  const { pathname } = useLocation();
  
  const normalizePathname = pathname.replace(/\/+$/, "");
  const relativePathname = normalizePathname.slice('/w/'.length);

  const isSelected = (path: string): boolean => {
    return relativePathname === path;
  };

  const shouldExpand = (subPaths: string[]): boolean => {
    return subPaths.includes(relativePathname);
  };

  return (
    <Sheet
      className="Sidebar"
      sx={{
        position: { xs: 'fixed', md: 'sticky' },
        transform: {
          xs: 'translateX(calc(100% * (var(--SideNavigation-slideIn, 0) - 1)))',
          md: 'none',
        },
        transition: 'transform 0.4s, width 0.4s',
        zIndex: 9999,
        height: '100dvh',
        width: 'var(--Sidebar-width)',
        top: 'var(--Header-height)',
        p: 2,
        flexShrink: 0,
        display: 'flex',
        flexDirection: 'column',
        gap: 2,
        borderRight: '1px solid',
        borderColor: 'divider',
      }}
    >
      <Box
        className="Sidebar-overlay"
        sx={{
          position: 'fixed',
          zIndex: 9998,
          top: 0,
          left: 0,
          width: '100vw',
          height: '100dvh',
          opacity: 'var(--SideNavigation-slideIn)',
          backgroundColor: 'var(--joy-palette-background-backdrop)',
          transition: 'opacity 0.4s',
          transform: {
            xs: 'translateX(calc(100% * (var(--SideNavigation-slideIn, 0) - 1) + var(--SideNavigation-slideIn, 0) * var(--Sidebar-width, 0px)))',
            lg: 'translateX(-100%)',
          },
        }}
        onClick={() => closeSidebar()}
      />
      <Box
        sx={{
          minHeight: 0,
          overflow: 'hidden auto',
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          [`& .${listItemButtonClasses.root}`]: {
            gap: 1.5,
          },
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
            <ListItemButton
              component={RouterLink}
              href="/w/characters"
              underline="none"
              selected={isSelected('characters')}
              sx={{
                pl: 1.5
              }}
            >
              <AndroidRounded />
              <ListItemContent>
                <Typography level="title-md">{t('Characters')}</Typography>
              </ListItemContent>
            </ListItemButton>
          </ListItem>

          <ListItem>
            <ListItemButton
              component={RouterLink}
              href="/w/prompts"
              underline="none"
              selected={isSelected('prompts')}
            >
              <ArticleRounded />
              <ListItemContent>
                <Typography level="title-md">{t('Prompts')}</Typography>
              </ListItemContent>
            </ListItemButton>
          </ListItem>

          <ListItem>
            <ListItemButton
              component={RouterLink}
              href="/w/plugins"
              underline="none"
              selected={isSelected('plugins')}
            >
              <ExtensionRounded />
              <ListItemContent>
                <Typography level="title-md">{t('Plugins')}</Typography>
              </ListItemContent>
            </ListItemButton>
          </ListItem>

          <ListItem>
            <ListItemButton
              component={RouterLink}
              href="/w/flows"
              underline="none"
              selected={isSelected('flows')}
            >
              <AccountTreeRounded />
              <ListItemContent>
                <Typography level="title-md">{t('Flows')}</Typography>
              </ListItemContent>
            </ListItemButton>
          </ListItem>

          <ListItem nested>
            <NestedListToggle
              defaultExpanded = {shouldExpand(['profile', 'credentials'])}
              renderToggle={({ open, setOpen }) => (
                <ListItemButton onClick={() => setOpen(!open)}>
                  <SettingsRounded />
                  <ListItemContent>
                    <Typography level="title-md">{t('Settings')}</Typography>
                  </ListItemContent>
                  <KeyboardArrowDownRounded
                    sx={{ transform: open ? 'rotate(180deg)' : 'none' }}
                  />
                </ListItemButton>
              )}
            >
              <List sx={{ gap: 0.5 }}>
                <ListItem sx={{ mt: 0.5 }}>
                  <ListItemButton
                    component={RouterLink}
                    href="/w/profile"
                    underline="none"
                    selected={isSelected('profile')}
                  >
                    {t('My Profile')}
                  </ListItemButton>
                </ListItem>

                <ListItem>
                  <ListItemButton
                    selected={isSelected('credentials')}
                    component={RouterLink}
                    href="/w/credentials"
                    underline="none"
                  >
                    {t('My Credentials')}
                  </ListItemButton>
                </ListItem>
              </List>
            </NestedListToggle>
          </ListItem>
        </List>
      </Box>
    </Sheet>
  );
}
