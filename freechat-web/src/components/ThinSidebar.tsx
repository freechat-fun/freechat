import { forwardRef, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Box, IconButton, List, ListDivider, ListItem, ListItemButton, ListItemButtonProps, Tooltip, TooltipProps, useTheme } from "@mui/joy";
import { AccountTreeRounded, AndroidRounded, ArticleRounded, ExtensionRounded, GitHub, HelpRounded, HomeRounded, KeyRounded, LoginRounded, LogoutRounded, ManageAccountsRounded } from "@mui/icons-material";
import { ColorSchemeToggle, LanguageToggle } from ".";
import { useMetaInfoContext } from "../contexts";
import { ChatIcon } from "./icon";


const ItemTooltip = forwardRef<HTMLDivElement, TooltipProps>((props, ref) => {
  const { sx, children, ...others } = props;
  return (
    <Tooltip
      ref={ref}
      size="sm"
      placement="right"
      sx={{
        zIndex: 9999,
        ...sx
      }}
      {...others}
    >
      <Box
        component="span"
        sx={{
          width: '100%',
          display: 'inline-block',
      }}>
          
        {children}
      </Box>
    </Tooltip>
  );
});

type ItemButtonProps = ListItemButtonProps & {
  href: string,
};

const ItemButton = forwardRef<HTMLDivElement, ItemButtonProps>((props, ref) => {
  const { children, href, ...others } = props;
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const { isAuthorized } = useMetaInfoContext();

  const isSelected = useCallback((targetPathname: string, currentPathname: string) => {
    return targetPathname.replace(/\/+$/, "") === currentPathname.replace(/\/+$/, "");
  }, []);

  return (
    <ListItemButton
      ref={ref}
      disabled={!isAuthorized()}
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

export default function ThinSidebar() {
  const theme = useTheme();
  const { t } = useTranslation('sidebar');
  const { csrfToken, isAuthorized, isGuest } = useMetaInfoContext();
  
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
        p: 2,
        flexShrink: 0,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        gap: 2,
      }}
    >
      <div>
        <List
          size="sm"
          sx={{
            bgcolor: 'transparent',
            borderRadius: 'md',
            boxShadow: 'lg',
            borderRight: 1,
            borderBottom: 1,
            borderColor: theme.palette.background.level2,
            py: 3,
            px: 0.5,
            gap: 0.5,
            '--List-nestedInsetStart': '30px',
            '--ListItem-radius': (theme) => theme.vars.radius.sm,
          }}
        >

          <ListItem>
            <ItemTooltip title={t('Home')}>
              <ItemButton href="/w">
                <HomeRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Chat')}>
              <ItemButton href="/w/chat">
                <ChatIcon />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListDivider sx={{my: 2}}/>

          <ListItem>
            <ItemTooltip title={t('Characters')}>
              <ItemButton href="/w/characters">
                <AndroidRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Prompts')}>
              <ItemButton href="/w/prompts">
                <ArticleRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Plugins')}>
              <ItemButton disabled href="/w/plugins">
                <ExtensionRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Agents')}>
              <ItemButton disabled href="/w/agents">
                <AccountTreeRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('API Reference')}>
              <ItemButton href="/w/docs" disabled={false}>
                <HelpRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListDivider sx={{my: 2}}/>

          <ListItem>
            <ItemTooltip title={t('Profile')}>
              <ItemButton href="/w/profile" disabled={!isAuthorized() || isGuest()}>
                <ManageAccountsRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Secrets & API keys')}>
              <ItemButton href="/w/credentials" disabled={!isAuthorized() || isGuest()}>
                <KeyRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListDivider sx={{my: 2}}/>

          <ItemTooltip title={t('Switch Theme')}>
            <ColorSchemeToggle sx={{width: '100%', mr: 1}} />
          </ItemTooltip>
          
          <ItemTooltip title={t('Switch Language')}>
            <LanguageToggle sx={{width: '100%'}} />
          </ItemTooltip>

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

          <ListDivider sx={{my: 2}}/>
          
          {isAuthorized() ? (
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