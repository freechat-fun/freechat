import React from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Box, List, ListItem, ListItemButton, ListItemContent, Sheet, Typography, listItemButtonClasses } from "@mui/joy";
import { closeSidebar } from "../libs/sidebar_utils";
import { AccountTreeRounded, ArticleRounded, ExtensionRounded, KeyboardArrowDownRounded, SettingsRounded } from "@mui/icons-material";
import  { FoxIcon } from ".";

interface MenuToggleProps {
  defaultExpanded?: boolean;
  children: React.ReactNode;
  renderToggle: (params: {
    open: boolean;
    setOpen: React.Dispatch<React.SetStateAction<boolean>>;
  }) => React.ReactNode;
}

function MenuToggler({
  defaultExpanded = false,
  renderToggle,
  children,
}: MenuToggleProps) {
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
        height: '100vh',
        width: 'var(--Sidebar-width)',
        top: 0,
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
          height: '100vh',
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
            <ListItemButton>
              <FoxIcon />
              <ListItemContent>
                <Typography level="title-md">{t('Characters')}</Typography>
              </ListItemContent>
            </ListItemButton>
          </ListItem>

          <ListItem>
            <ListItemButton>
              <ArticleRounded />
              <ListItemContent>
                <Typography level="title-md">{t('Prompts')}</Typography>
              </ListItemContent>
            </ListItemButton>
          </ListItem>

          <ListItem>
            <ListItemButton>
              <ExtensionRounded />
              <ListItemContent>
                <Typography level="title-md">{t('Plugins')}</Typography>
              </ListItemContent>
            </ListItemButton>
          </ListItem>

          <ListItem>
            <ListItemButton>
              <AccountTreeRounded />
              <ListItemContent>
                <Typography level="title-md">{t('Flows')}</Typography>
              </ListItemContent>
            </ListItemButton>
          </ListItem>

          <ListItem nested>
            <MenuToggler
              defaultExpanded
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
                  <ListItemButton selected>{t('My Profile')}</ListItemButton>
                </ListItem>

                <ListItem>
                  <ListItemButton>{t('My Credentials')}</ListItemButton>
                </ListItem>
              </List>
            </MenuToggler>
          </ListItem>
        </List>
      </Box>
    </Sheet>
  );
}
