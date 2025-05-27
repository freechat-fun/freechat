/* eslint-disable prettier/prettier */
import { useState, useEffect } from 'react';
import { ListItemButton, ListItemButtonProps } from '@mui/material';
import { DarkModeRounded, LightModeRounded } from '@mui/icons-material';
import { useColorScheme as useMaterialColorScheme } from '@mui/material';
import { useColorScheme as useJoyColorScheme } from '@mui/joy';

export default function ColorSchemeToggle(props: ListItemButtonProps) {
  const [mounted, setMounted] = useState(false);
  const { setMode: setJoyMode } = useJoyColorScheme();
  const { mode, setMode: setMaterialMode } = useMaterialColorScheme();

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return <ListItemButton dense color="inherit" {...props} disabled />;
  }

  const toggleColorMode = () => {
    if (mode === 'light') {
      setJoyMode('dark');
      setMaterialMode('dark');
    } else {
      setJoyMode('light');
      setMaterialMode('light');
    }
  };

  return (
    <ListItemButton
      dense
      id="toggle-mode"
      onClick={(event) => {
        event.preventDefault();
        toggleColorMode();
      }}
      {...props}
    >
      {mode === 'dark' ? (
        <LightModeRounded />
      ) : (
        <DarkModeRounded />
      )}
    </ListItemButton>
  );
}
