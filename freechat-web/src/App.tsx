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

import {
  CssVarsProvider as JoyCssVarsProvider,
  CssBaseline as JoyCssBaseline,
  extendTheme as joyExtendTheme,
  GlobalStyles as JoyGlobalStyles,
  THEME_ID as JOY_THEME_ID,
} from '@mui/joy';

const joyTheme = joyExtendTheme({
  components: {
    MuiSvgIcon: {
      defaultProps: {
        // @ts-expect-error: temporary for MUI icons usage
        fontSize: 'medium',
      },
    },
  },
});

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
          paper: '#171A1C',
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
              '--Footer-height': '80px',
              '--Footer-width': '80vw',
            },
          })}
        />
        <JoyCssVarsProvider
          defaultMode="dark"
          theme={{ [JOY_THEME_ID]: joyTheme }}
        >
          <JoyCssBaseline />
          <JoyGlobalStyles
            styles={() => ({
              ':root': {
                '--Collapsed-breakpoint': '769px', // form will stretch when viewport is below `769px`
                '--Cover-width': '50dvw',
                '--Form-maxWidth': '800px',
                '--Transition-duration': '0.4s', // set to `none` to disable transition
                '--Sidebar-width': '240px',
                '--Header-height': '40px',
                '--Footer-height': '80px',
                '--Footer-width': '80vw',
              },
            })}
          />
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <UnauthorizedDialog />
            <ErrorMessageSnackbar />
            <RouterProvider router={router} />
          </LocalizationProvider>
        </JoyCssVarsProvider>
      </ThemeProvider>
    </ContextsProvider>
  );
}

export default App;
