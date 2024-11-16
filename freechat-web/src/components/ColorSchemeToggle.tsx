import { useState, useEffect } from 'react';
import {
  IconButton,
  IconButtonProps,
  useColorScheme as useJoyColorScheme,
} from '@mui/joy';
import { DarkModeRounded, LightModeRounded } from '@mui/icons-material';

import { useColorScheme as useMaterialColorScheme } from '@mui/material';

export default function ColorSchemeToggle(props: IconButtonProps) {
  const { onClick, sx, ...other } = props;
  const { mode, setMode: setJoyMode } = useJoyColorScheme();
  const { setMode: setMaterialMode } = useMaterialColorScheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);
  if (!mounted) {
    return (
      <IconButton
        size="sm"
        variant="plain"
        color="neutral"
        {...other}
        sx={sx}
        disabled
      />
    );
  }
  return (
    <IconButton
      id="toggle-mode"
      size="sm"
      variant="plain"
      color="neutral"
      {...props}
      onClick={(event) => {
        if (mode === 'light') {
          setJoyMode('dark');
          setMaterialMode('dark');
        } else {
          setJoyMode('light');
          setMaterialMode('light');
        }
        onClick?.(event);
      }}
      sx={[
        {
          '& > *:first-of-type': {
            display: mode === 'dark' ? 'none' : 'initial',
          },
          '& > *:last-of-type': {
            display: mode === 'light' ? 'none' : 'initial',
          },
        },
        ...(Array.isArray(sx) ? sx : [sx]),
      ]}
    >
      <DarkModeRounded />
      <LightModeRounded />
    </IconButton>
  );
}
