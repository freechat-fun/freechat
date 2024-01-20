import { forwardRef, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Box, IconButton, List, ListDivider, ListItem, ListItemButton, ListItemButtonProps, Tooltip, TooltipProps } from "@mui/joy";
import { AccountTreeRounded, AndroidRounded, ArticleRounded, Diversity1Rounded, ExtensionRounded, GitHub, HelpRounded, HomeRounded, KeyRounded, LoginRounded, LogoutRounded, ManageAccountsRounded } from "@mui/icons-material";
import { ColorSchemeToggle, LanguageToggle } from ".";
import { useUserInfoContext } from "../contexts";


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
      <Box component="span" sx={{ display: 'inline-block' }}>
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
  const { isAuthorized } = useUserInfoContext();

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
  const { t } = useTranslation('sidebar');
  const { csrfToken, isAuthorized } = useUserInfoContext();
  
  return (
    <Box
      className="Sidebar"
      sx={{
        position: 'fixed',
        zIndex: 9999,
        height: '100vh',
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
            py: 3,
            px: 0.5,
            gap: 0.5,
            '--List-nestedInsetStart': '30px',
            '--ListItem-radius': (theme) => theme.vars.radius.sm,
          }}
        >

          <ListItem>
            <ItemTooltip title={t('Home')}>
              <ItemButton href="/w" disabled={false}>
                <HomeRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Chat')}>
              <ItemButton href="/w/chat">
                <Diversity1Rounded />
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
            <ItemTooltip title={t('Flows')}>
              <ItemButton disabled href="/w/flows">
                <AccountTreeRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('API reference')}>
              <ItemButton href="/w/docs" disabled={false}>
                <HelpRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListDivider sx={{my: 2}}/>

          <ListItem>
            <ItemTooltip title={t('Profile')}>
              <ItemButton href="/w/profile">
                <ManageAccountsRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Secrets & API keys')}>
              <ItemButton href="/w/credentials">
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
                    title={t('Sign out', {ns: 'account'})}
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
                <ItemTooltip title={t('Sign in', {ns: 'account'})}>
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