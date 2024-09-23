import { forwardRef, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Box, IconButton, List, ListDivider, ListItem, ListItemButton, ListItemButtonProps, Tooltip, TooltipProps, useTheme } from "@mui/joy";
import { GitHub, HomeRounded, LoginRounded, LogoutRounded, SmsRounded } from "@mui/icons-material";
import { ColorSchemeToggle, LanguageToggle } from ".";
import { useMetaInfoContext } from "../contexts";


const ItemTooltip = forwardRef<HTMLDivElement, TooltipProps>((props, ref) => {
  const { sx, children, ...others } = props;
  return (
    <Tooltip
      ref={ref}
      size="sm"
      placement="top"
      sx={{
        zIndex: 9999,
        ...sx
      }}
      {...others}
    >
      <Box
        component="span"
        sx={{
          display: 'inline-block',
      }}>
        {children}
      </Box>
    </Tooltip>
  );
});

type ItemButtonProps = ListItemButtonProps & {
  href: string,
  anonymous?: boolean
};

const ItemButton = forwardRef<HTMLDivElement, ItemButtonProps>((props, ref) => {
  const { children, href, anonymous = false, ...others } = props;
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const { isAuthorized } = useMetaInfoContext();

  const isSelected = useCallback((targetPathname: string, currentPathname: string) => {
    return targetPathname.replace(/\/+$/, "") === currentPathname.replace(/\/+$/, "");
  }, []);

  return (
    <ListItemButton
      ref={ref}
      disabled={!anonymous && !isAuthorized()}
      selected={isSelected(href, pathname)}
      onClick={(event) => {
        event.preventDefault();
        navigate(href);
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
          size="sm"
          orientation="horizontal"
          sx={{
            bgcolor: 'transparent',
            borderRadius: 'md',
            boxShadow: 'lg',
            border: 1,
            borderColor: theme.palette.background.level3,
            px: 0.8,
            py: 0,
            m: 0,
            gap: 0,
            '--List-nestedInsetStart': '30px',
            '--ListItem-radius': (theme) => theme.vars.radius.sm,
          }}
        >

          <ListItem>
            <ItemTooltip title={t('Home')}>
              <ItemButton href="/" anonymous>
                <HomeRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Chat')}>
              <ItemButton href="/w/chat">
                <SmsRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListDivider orientation="vertical" sx={{
            mx: 2,
            '--Divider-lineColor': 'var(--joy-palette-background-level3)',
          }}/>

          <ListItem>
            <ItemTooltip title={t('Switch Theme')}>
              <ColorSchemeToggle sx={{ flex: 1 }} />
            </ItemTooltip>
          </ListItem>
          
          <ListItem>
            <ItemTooltip title={t('Switch Language')}>
              <LanguageToggle sx={{ flex: 1 }} />
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Code Repository')}>
              <IconButton
                component="a"
                href="https://github.com/freechat-fun/freechat"
                target="_blank"
                sx={{width: '100%'}}
              >
                <GitHub />
              </IconButton>
            </ItemTooltip>
          </ListItem>

          <ListDivider orientation="vertical" sx={{
            mx: 2,
            '--Divider-lineColor': 'var(--joy-palette-background-level3)',
          }}/>
          
          {isAuthorized() ? (
            <ListItem>
              <form method="post" action="/logout">
                <input type="hidden" name="_csrf" value={csrfToken ?? ''} />
                  <Tooltip
                    title={t('Sign Out', {ns: 'account'})}
                    size="sm"
                    placement="right"
                    sx={{zIndex: 9999}}
                  >
                    <IconButton type="submit" sx={{width: '100%'}}>
                      <LogoutRounded />
                    </IconButton>
                  </Tooltip>
              </form>
            </ListItem>
          ) : (
            <ListItem>
              <ItemTooltip title={t('Sign In', {ns: 'account'})}>
                <ItemButton href="/w/login" disabled={false}>
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