/* eslint-disable prettier/prettier */
import { useState, useEffect } from 'react';
import { ListItemButton, ListItemButtonProps } from '@mui/material';
import { DarkModeRounded, LightModeRounded } from '@mui/icons-material';
import { useColorScheme } from '@mui/material';

export default function ColorSchemeToggle(props: ListItemButtonProps) {
  const [mounted, setMounted] = useState(false);
  const { mode, setMode } = useColorScheme();

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return <ListItemButton dense color="inherit" {...props} disabled />;
  }

  const toggleColorMode = () => {
    if (mode === 'light') {
      setMode('dark');
    } else {
      setMode('light');
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
      {mode === 'light' ? (
        <DarkModeRounded />
      ) : (
        <LightModeRounded />
      )}
    </ListItemButton>
  );
}
