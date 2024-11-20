import { Box, IconButton } from '@mui/joy';
import { Outlet } from 'react-router-dom';
import { ThinSidebar, FooterSidebar } from '../components';
import { useEffect, useRef, useState } from 'react';
import { useFrameScrollContext } from '../contexts';
import { KeyboardDoubleArrowUpRounded } from '@mui/icons-material';

export default function SidebarFrame() {
  const { frameScrollHandler } = useFrameScrollContext();
  const containerRef = useRef<HTMLDivElement | null>(null);
  const [showScrollToTop, setShowScrollToTop] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      if (containerRef.current) {
        frameScrollHandler?.(containerRef.current);
        const threshold = 800;
        const scrollTop = containerRef.current.scrollTop;
        setShowScrollToTop(scrollTop > threshold);
      }
    };

    const container = containerRef.current;
    container?.addEventListener('scroll', handleScroll);

    return () => {
      container?.removeEventListener('scroll', handleScroll);
    };
  }, [frameScrollHandler]);

  const scrollToTop = () => {
    if (containerRef.current) {
      containerRef.current.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

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
      >
        <Outlet />
      </Box>
      <IconButton
        variant="solid"
        size="lg"
        sx={{
          display: showScrollToTop ? 'block' : 'none',
          position: 'fixed',
          bottom: {
            xs: 'calc(10px + var(--Footer-height))',
            sm: '40px',
          },
          right: '40px',
          borderRadius: '50%',
          zIndex: 1000,
        }}
        onClick={scrollToTop}
      >
        <KeyboardDoubleArrowUpRounded />
      </IconButton>
      <FooterSidebar />
    </>
  );
}
