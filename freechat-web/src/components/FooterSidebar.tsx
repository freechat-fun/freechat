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
  useTheme,
} from '@mui/material';
import {
  GitHub,
  HomeRounded,
  LoginRounded,
  LogoutRounded,
  SmsRounded,
} from '@mui/icons-material';
import { ColorSchemeToggle, LanguageToggle, SidebarDivider } from '.';
import { useMetaInfoContext } from '../contexts';

const ItemTooltip = forwardRef<HTMLDivElement, TooltipProps>((props, ref) => {
  const { sx, children, ...others } = props;
  return (
    <Tooltip
      ref={ref}
      placement="top"
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
  const theme = useTheme();
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
        borderRadius: theme.shape.borderRadius / 2,
        px: 1,
      }}
      {...others}
    >
      {children}
    </ListItemButton>
  );
});

export default function FooterSidebar() {
  const theme = useTheme();
  const { t } = useTranslation('sidebar');
  const { csrfToken, isAuthorized } = useMetaInfoContext();
  const borderRadius = theme.shape.borderRadius / 2;

  return (
    <Box
      className="Sidebar"
      sx={{
        zIndex: 9999,
        position: 'fixed',
        bottom: 0,
        left: 'calc((100dvw - var(--Footer-width)) / 2)',
        right: 'calc((100dvw - var(--Footer-width)) / 2)',
        height: 'var(--Footer-height)',
        width: 'var(--Footer-width)',
        bgcolor: 'transparent',
        boxShadow: 'none',
        display: { xs: 'flex', sm: 'none' },
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
      }}
    >
      <div>
        <List
          dense
          sx={{
            bgcolor: 'transparent',
            borderRadius: borderRadius,
            boxShadow: theme.shadows[3],
            borderLeft: 1,
            borderTop: 1,
            borderRight: 1,
            borderBottom: 0,
            borderColor: theme.palette.background.default,
            px: 1,
            py: 0.5,
            m: 0,
            gap: 0.5,
            display: 'flex',
            flexDirection: 'row',
          }}
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

          <SidebarDivider orientation="vertical" />

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

          <SidebarDivider orientation="vertical" />

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
