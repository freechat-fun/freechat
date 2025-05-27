import { forwardRef } from 'react';
import { Drawer, DrawerProps, Paper } from '@mui/material';
import { styled } from '@mui/material/styles';

const StyledPaper = styled(Paper)(({ theme }) => ({
  borderRadius: theme.shape.borderRadius * 2,
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
      anchor="right"
      PaperProps={{
        sx: {
          width: { md: '30%' },
          backgroundColor: 'transparent',
          p: { sm: 0, md: 2 },
          boxShadow: 'none',
        },
      }}
      {...others}
    >
      <StyledPaper>{children}</StyledPaper>
    </Drawer>
  );
});

export default Sidedrawer;
