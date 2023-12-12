import { Dropdown, MenuButton, Menu, MenuItem, IconButton, ListItemDecorator, Tooltip } from '@mui/joy';
import { Login, Logout, ManageAccounts, PermIdentity } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useUserInfoContext } from '../context';

export default function AccountMenu() {
  const { t } = useTranslation('account');
  const { isAuthorized, getDisplayName } = useUserInfoContext();

  const menuButton = (
    <MenuButton 
      slots={{ root: IconButton }}
      slotProps={{ root: { variant: 'plain', color: 'neutral' } }}
    >
      <ManageAccounts />
    </MenuButton>
  );

  return (
    <Dropdown>
      {isAuthorized() ? (
        <>
          <Tooltip title={getDisplayName()} disableInteractive>
            {menuButton}
          </Tooltip>
          <Menu>
            <MenuItem>
              <ListItemDecorator>
                <PermIdentity />
              </ListItemDecorator>
              {t('My account')}
            </MenuItem>
            <MenuItem>
              <ListItemDecorator>
                <Logout />
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
                <Login />
              </ListItemDecorator>
              {t('Sign in')}
            </MenuItem>
          </Menu>
        </>
      )}
    </Dropdown>
  );
}