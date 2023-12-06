import { Dropdown, MenuButton, Menu, MenuItem, IconButton } from '@mui/joy';
import { ManageAccounts } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';

export default function AccountMenu() {
  const { t } = useTranslation('sign-in');

  return (
    <Dropdown>
      <MenuButton 
        slots={{ root: IconButton }}
        slotProps={{ root: { variant: 'plain', color: 'neutral' } }}
      >
        <ManageAccounts />
      </MenuButton>
      <Menu>
        <MenuItem>{t('Sign in')}</MenuItem>
      </Menu>
    </Dropdown>
  );
}