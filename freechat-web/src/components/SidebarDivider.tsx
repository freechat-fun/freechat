import { Divider, DividerProps } from '@mui/material';
import { forwardRef } from 'react';

const SidebarDivider = forwardRef<HTMLHRElement, DividerProps>((props, ref) => {
  const { sx, ...others } = props;

  return (
    <Divider
      ref={ref}
      flexItem
      sx={{
        mx: 0.5,
        my: 0.5,
        borderColor: 'diveder',
        ...sx,
      }}
      {...others}
    />
  );
});

export default SidebarDivider;
