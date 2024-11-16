import { MenuRounded } from '@mui/icons-material';
import { IconButton } from '@mui/joy';
import { toggleSidebar } from '../libs/sidebar_utils';

export default function SidebarToggle() {
  return (
    <IconButton
      onClick={() => toggleSidebar()}
      variant="outlined"
      color="neutral"
      size="sm"
      sx={{
        display: { md: 'none' },
      }}
    >
      <MenuRounded />
    </IconButton>
  );
}
