import { Dropdown, MenuButton, Menu, MenuItem, IconButton, ListItemDecorator } from '@mui/joy';
import { LoginRounded, LogoutRounded, ManageAccountsRounded, PermIdentityRounded } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useUserInfoContext } from '../contexts';

export default function AccountMenu() {
  const { t } = useTranslation('account');
  const { isAuthorized } = useUserInfoContext();

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
            <MenuItem>
              <ListItemDecorator>
                <LogoutRounded />
              </ListItemDecorator>
              {t('Sign out')}
            </MenuItem>
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