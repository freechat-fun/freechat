import { useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { useUserInfoContext } from '../contexts';
import { Dropdown, MenuButton, Menu, MenuItem, IconButton, ListItemDecorator, Button } from '@mui/joy';
import { LoginRounded, LogoutRounded, ManageAccountsRounded, PermIdentityRounded } from '@mui/icons-material';

export default function AccountMenu() {
  const { t } = useTranslation('account');
  const { csrfToken, isAuthorized } = useUserInfoContext();

  const submitRef = useRef<HTMLButtonElement>(null);

  const menuButton = (
    <MenuButton 
      slots={{ root: IconButton }}
      slotProps={{ root: { variant: 'plain', color: 'neutral' } }}
    >
      <ManageAccountsRounded />
    </MenuButton>
  );

  return (
    <Dropdown>
      {isAuthorized() ? (
        <>
          {menuButton}
          <Menu>
            <MenuItem>
              <ListItemDecorator>
                <PermIdentityRounded />
              </ListItemDecorator>
              {t('My account')}
            </MenuItem>
            <form method="post" action="/logout">
              <input type="hidden" name="_csrf" value={csrfToken ?? ''} />
              <Button
                ref={submitRef}
                type="submit"
                sx={{ display: 'none' }}
              />
              <MenuItem onClick={() => submitRef.current?.click()}>
                <ListItemDecorator>
                  <LogoutRounded />
                </ListItemDecorator>
                {t('Sign out')}
              </MenuItem>
            </form>
          </Menu>
        </>
      ) : (
        <>
          {menuButton}
          <Menu>
            <MenuItem onClick={() => window.location.href = '/w/login'}>
              <ListItemDecorator>
                <LoginRounded />
              </ListItemDecorator>
              {t('Sign in')}
            </MenuItem>
          </Menu>
        </>
      )}
    </Dropdown>
  );
}