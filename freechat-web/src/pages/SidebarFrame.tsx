import { Box } from '@mui/joy';
import { Outlet } from 'react-router-dom';
import { ThinSidebar, FooterSidebar } from '../components';

export default function SidebarFrame() {
  return (
    <>
      <ThinSidebar />
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          gap: 1,
          overflow: 'auto',
          pl: { xs: 'none', sm: '120px' },
          pr: { xs: 'none', sm: '120px' },
          maxHeight: {
            xs: 'calc(100dvh - var(--Footer-height))',
            sm: '100dvh',
          },
        }}
      >
        <Outlet />
      </Box>
      <FooterSidebar />
    </>
  );
}
