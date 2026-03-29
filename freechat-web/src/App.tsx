import { RouterProvider } from 'react-router-dom';
import { ContextsProvider } from './contexts';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { UnauthorizedDialog, ErrorMessageSnackbar } from './components';
import router from './router';

import {
  createTheme,
  CssBaseline,
  GlobalStyles,
  ThemeProvider,
} from '@mui/material';

const theme = createTheme({
  colorSchemes: {
    light: {
      palette: {
        background: {
          default: '#FBFCFE',
          paper: '#F0F4F8',
        },
      },
    },
    dark: {
      palette: {
        background: {
          default: '#0B0D0E',
          paper: '#272A2C',
        },
        divider: '#424242',
      },
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
        },
      },
    },
  },
});

function App() {
  return (
    <ContextsProvider>
      <ThemeProvider defaultMode="dark" theme={theme} noSsr>
        <CssBaseline />
        <GlobalStyles
          styles={() => ({
            ':root': {
              '--Collapsed-breakpoint': '769px', // form will stretch when viewport is below `769px`
              '--Cover-width': '50dvw',
              '--Form-maxWidth': '800px',
              '--Transition-duration': '0.4s', // set to `none` to disable transition
              '--Sidebar-width': '240px',
              '--Header-height': '40px',
              '--Footer-height-base': '42px',
              '--Footer-safe-area': '0px',
              '--Footer-height':
                'calc(var(--Footer-height-base) + var(--Footer-safe-area))',
              '--Footer-width': '80vw',
              '--App-height-xs': 'calc(100dvh - var(--Footer-height))',
            },
            'html[data-ios-pwa="true"]': {
              '--Footer-safe-area': 'env(safe-area-inset-bottom, 0px)',
            },
          })}
        />
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <UnauthorizedDialog />
          <ErrorMessageSnackbar />
          <RouterProvider router={router} />
        </LocalizationProvider>
      </ThemeProvider>
    </ContextsProvider>
  );
}

export default App;
