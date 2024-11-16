import { forwardRef } from 'react';
import { Drawer, DrawerProps, Sheet } from '@mui/joy';

const Sidedrawer = forwardRef<HTMLDivElement, DrawerProps>((props, ref) => {
  const { children, ...others } = props;
  return (
    <Drawer
      ref={ref}
      size="md"
      variant="plain"
      anchor="right"
      slotProps={{
        content: {
          sx: {
            md: 50,
            bgcolor: 'transparent',
            p: { sm: 0, md: 2 },
            boxShadow: 'none',
          },
        },
      }}
      {...others}
    >
      <Sheet
        sx={{
          borderRadius: 'md',
          p: 2,
          display: 'flex',
          flexDirection: 'column',
          gap: 1,
          height: '100%',
          overflow: 'auto',
        }}
      >
        {children}
      </Sheet>
    </Drawer>
  );
});

export default Sidedrawer;
