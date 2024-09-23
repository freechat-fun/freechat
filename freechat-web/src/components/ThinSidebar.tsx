import { forwardRef, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Box, IconButton, List, ListDivider, ListItem, ListItemButton, ListItemButtonProps, Tooltip, TooltipProps, useTheme } from "@mui/joy";
import { AccountTreeRounded, AndroidRounded, ArticleRounded, GitHub, HelpRounded, HomeRounded, KeyRounded, LoginRounded, LogoutRounded, ManageAccountsRounded, SmsRounded } from "@mui/icons-material";
import { ColorSchemeToggle, LanguageToggle } from ".";
import { useMetaInfoContext } from "../contexts";


const ItemTooltip = forwardRef<HTMLDivElement, TooltipProps>((props, ref) => {
  const { sx, children, ...others } = props;
  return (
    <Tooltip
      ref={ref}
      placement="right"
      size="sm"
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

export default function ThinSidebar() {
  const theme = useTheme();
  const { t } = useTranslation('sidebar');
  const { csrfToken, isAuthorized } = useMetaInfoContext();
  
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
          size="sm"
          sx={{
            bgcolor: 'transparent',
            borderRadius: 'md',
            boxShadow: 'lg',
            borderRight: 1,
            borderBottom: 1,
            borderColor: theme.palette.background.level3,
            py: 3,
            px: 0.5,
            gap: 0.5,
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

          <ListDivider sx={{
            my: 2,
            '--Divider-lineColor': 'var(--joy-palette-background-level3)',
          }}/>

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

          <ListDivider sx={{
            my: 2,
            '--Divider-lineColor': 'var(--joy-palette-background-level3)',
          }}/>

          <ListItem>
            <ItemTooltip title={t('Profile')}>
              <ItemButton href="/w/profile" disabled={!isAuthorized()}>
                <ManageAccountsRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListItem>
            <ItemTooltip title={t('Secrets & API keys')}>
              <ItemButton href="/w/credentials" disabled={!isAuthorized()}>
                <KeyRounded />
              </ItemButton>
            </ItemTooltip>
          </ListItem>

          <ListDivider sx={{
            my: 2,
            '--Divider-lineColor': 'var(--joy-palette-background-level3)',
          }}/>

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

          <ListDivider sx={{
            my: 2,
            '--Divider-lineColor': 'var(--joy-palette-background-level3)',
          }}/>
          
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