import { forwardRef, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
  Box,
  List,
  ListItem,
  ListItemButton,
  ListItemButtonProps,
  Tooltip,
  TooltipProps,
} from '@mui/material';
import {
  AccountTreeRounded,
  AndroidRounded,
  ArticleRounded,
  GitHub,
  HelpRounded,
  HomeRounded,
  KeyRounded,
  LoginRounded,
  LogoutRounded,
  ManageAccountsRounded,
  SmsRounded,
} from '@mui/icons-material';
import { ColorSchemeToggle, LanguageToggle, SidebarDivider } from '.';
import { useMetaInfoContext } from '../contexts';

const ItemTooltip = forwardRef<HTMLDivElement, TooltipProps>((props, ref) => {
  const { sx, children, ...others } = props;
  return (
    <Tooltip
      ref={ref}
      placement="right"
      sx={{
        zIndex: 9999,
        ...sx,
      }}
      {...others}
    >
      <Box
        component="span"
        sx={{
          display: 'inline-block',
        }}
      >
        {children}
      </Box>
    </Tooltip>
  );
});

type ItemButtonProps = ListItemButtonProps & {
  href: string;
  anonymous?: boolean;
};

const ItemButton = forwardRef<HTMLDivElement, ItemButtonProps>((props, ref) => {
  const { children, href, anonymous = false, ...others } = props;
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const { isAuthorized } = useMetaInfoContext();

  const isSelected = useCallback(
    (targetPathname: string, currentPathname: string) => {
      return (
        targetPathname.replace(/\/+$/, '') ===
        currentPathname.replace(/\/+$/, '')
      );
    },
    []
  );

  return (
    <ListItemButton
      ref={ref}
      disabled={!anonymous && !isAuthorized()}
      selected={isSelected(href, pathname)}
      onClick={(event) => {
        event.preventDefault();
        navigate(href);
      }}
      sx={{
        borderRadius: '4px',
        px: 1,
      }}
      {...others}
    >
      {children}
    </ListItemButton>
  );
});

export default function ThinSidebar() {
  const { t } = useTranslation('sidebar');
  const { csrfToken, isAuthorized } = useMetaInfoContext();
  const borderRadius = '4px';

  return (
    <Box
      className="Sidebar"
      sx={{
        position: 'fixed',
        zIndex: 9999,
        height: '100dvh',
        width: '80px',
        bgcolor: 'transparent',
        boxShadow: 'none',
        flexShrink: 0,
        display: { xs: 'none', sm: 'flex' },
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
      }}
    >
      <div>
        <List
          dense
          sx={[
            () => ({
              bgcolor: 'transparent',
              borderRadius: borderRadius,
              boxShadow: 3,
              borderLeft: 0,
              borderTop: 1,
              borderRight: 1,
              borderBottom: 0,
              borderColor: 'divider',
              px: 0.5,
              py: 2,
              m: 0,
              gap: 1,
              display: 'flex',
              flexDirection: 'column',
            }),
            (theme) =>
              theme.applyStyles('dark', {
                bgcolor: 'background.paper',
              }),
          ]}
        >
          <ListItem disablePadding>
            <ItemTooltip title={t('Home')}>
              <ItemButton href="/" anonymous>
                <HomeRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem disablePadding>
            <ItemTooltip title={t('Chat')}>
              <ItemButton href="/w/chat">
                <SmsRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <SidebarDivider />

          <ListItem disablePadding>
            <ItemTooltip title={t('Characters')}>
              <ItemButton href="/w/characters">
                <AndroidRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem disablePadding>
            <ItemTooltip title={t('Prompts')}>
              <ItemButton href="/w/prompts">
                <ArticleRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem disablePadding>
            <ItemTooltip title={t('Agents')}>
              <ItemButton disabled href="/w/agents">
                <AccountTreeRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem disablePadding>
            <ItemTooltip title={t('API Reference')}>
              <ItemButton href="/w/docs" anonymous>
                <HelpRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <SidebarDivider />

          <ListItem disablePadding>
            <ItemTooltip title={t('Profile')}>
              <ItemButton href="/w/profile">
                <ManageAccountsRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem disablePadding>
            <ItemTooltip title={t('Secrets & API keys')}>
              <ItemButton href="/w/credentials">
                <KeyRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <SidebarDivider />

          <ListItem disablePadding>
            <ItemTooltip title={t('Switch Theme')}>
              <ColorSchemeToggle
                sx={{
                  borderRadius: borderRadius,
                  px: 1,
                }}
              />
            </ItemTooltip>
          </ListItem>

          <ListItem disablePadding>
            <ItemTooltip title={t('Switch Language')}>
              <LanguageToggle
                sx={{
                  borderRadius: borderRadius,
                  px: 1,
                }}
              />
            </ItemTooltip>
          </ListItem>

          <ListItem disablePadding>
            <ItemTooltip title={t('Code Repository')}>
              <ListItemButton
                dense
                component="a"
                href="https://github.com/freechat-fun/freechat"
                target="_blank"
                sx={{
                  borderRadius: borderRadius,
                  px: 1,
                }}
              >
                <GitHub />
              </ListItemButton>
            </ItemTooltip>
          </ListItem>

          <SidebarDivider />

          {isAuthorized() ? (
            <ListItem disablePadding>
              <form method="post" action="/logout">
                <input type="hidden" name="_csrf" value={csrfToken ?? ''} />
                <ItemTooltip title={t('Sign Out', { ns: 'account' })}>
                  <ListItemButton
                    component="button"
                    dense
                    type="submit"
                    sx={{
                      borderRadius: borderRadius,
                      px: 1,
                    }}
                  >
                    <LogoutRounded />
                  </ListItemButton>
                </ItemTooltip>
              </form>
            </ListItem>
          ) : (
            <ListItem disablePadding>
              <ItemTooltip title={t('Sign In', { ns: 'account' })}>
                <ItemButton href="/w/login" anonymous>
                  <LoginRounded />
                </ItemButton>
              </ItemTooltip>
            </ListItem>
          )}
        </List>
      </div>
    </Box>
  );
}
