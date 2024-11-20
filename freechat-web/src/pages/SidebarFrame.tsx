import { Box } from '@mui/joy';
import { Outlet } from 'react-router-dom';
import { ThinSidebar, FooterSidebar } from '../components';
import { useRef } from 'react';
import { useFrameScrollContext } from '../contexts';

export default function SidebarFrame() {
  const { frameScrollHandler } = useFrameScrollContext();
  const containerRef = useRef<HTMLDivElement | null>(null);

  function handleScroll(): void {
    containerRef.current && frameScrollHandler?.(containerRef.current);
  }

  return (
    <>
      <ThinSidebar />
      <Box
        ref={containerRef}
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
        onScroll={handleScroll}
      >
        <Outlet />
      </Box>
      <FooterSidebar />
    </>
  );
}
