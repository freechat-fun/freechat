import { Divider, DividerProps, useColorScheme, useTheme } from '@mui/material';
import { forwardRef, useEffect, useState } from 'react';

const SidebarDivider = forwardRef<HTMLHRElement, DividerProps>((props, ref) => {
  const { sx, ...others } = props;
  const theme = useTheme();
  const { mode } = useColorScheme();
  const [dividerColor, setDividerColor] = useState<string>(
    mode === 'light' ? theme.palette.grey[300] : theme.palette.grey[700]
  );

  useEffect(() => {
    setDividerColor(
      mode === 'light' ? theme.palette.grey[300] : theme.palette.grey[700]
    );
  }, [mode, theme.palette.grey]);

  return (
    <Divider
      ref={ref}
      flexItem
      sx={{
        mx: 0.5,
        my: 0.5,
        borderColor: dividerColor,
        ...sx,
      }}
      {...others}
    />
  );
});

export default SidebarDivider;
