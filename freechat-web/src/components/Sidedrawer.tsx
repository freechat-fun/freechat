import { forwardRef } from 'react';
import { Drawer, DrawerProps, Paper } from '@mui/material';
import { styled } from '@mui/material/styles';

const StyledPaper = styled(Paper)(({ theme }) => ({
  borderRadius: '8px',
  padding: theme.spacing(2),
  display: 'flex',
  flexDirection: 'column',
  gap: theme.spacing(1),
  height: '100%',
  overflow: 'auto',
}));

const Sidedrawer = forwardRef<HTMLDivElement, DrawerProps>((props, ref) => {
  const { children, ...others } = props;

  return (
    <Drawer
      ref={ref}
      elevation={0}
      anchor="right"
      slotProps={{
        paper: {
          sx: {
            width: { md: '35%' },
            backgroundColor: 'transparent',
            p: { sm: 0, md: 2 },
            boxShadow: 'none',
          },
        },
      }}
      {...others}
    >
      <StyledPaper>{children}</StyledPaper>
    </Drawer>
  );
});

Sidedrawer.displayName = 'Sidedrawer';

export default Sidedrawer;
