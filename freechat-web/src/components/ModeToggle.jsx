import { useState, useEffect } from "react";
import { IconButton, useColorScheme } from "@mui/joy";
import { LightMode, DarkMode } from "@mui/icons-material";
import { Stack } from "@mui/system";

export default function ModeToggle() {
  const { mode, setMode } = useColorScheme();
  const [mounted, setMounted] = useState(false);

  // necessary for server-side rendering
  // because mode is undefined on the server
  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return null;
  }

  return (
    <Stack direction='row' justifyContent='flex-end'>
      <IconButton
        variant="plain"
        onClick={() => {
          setMode(mode === 'light' ? 'dark' : 'light')
        }}
      >
        { mode === 'light' ? <LightMode /> : <DarkMode /> }
      </IconButton>
    </Stack>
  );
}