import { RouterProvider } from 'react-router-dom';
import { ContextsProvider } from './contexts';
import { CssVarsProvider as JoyCssVarsProvider, GlobalStyles, CssBaseline, Box } from '@mui/joy';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { UnauthorizedDialog, ErrorMessageSnackbar } from './components';
import { Header } from './components';
import router from './router';

import {
  experimental_extendTheme as materialExtendTheme,
  Experimental_CssVarsProvider as MaterialCssVarsProvider,
  THEME_ID as MATERIAL_THEME_ID,
} from '@mui/material';

const materialTheme = materialExtendTheme();

function App() {
  return (
    <ContextsProvider>
      <MaterialCssVarsProvider defaultMode="system" theme={{ [MATERIAL_THEME_ID]: materialTheme }}>
        <JoyCssVarsProvider defaultMode="system">
          <CssBaseline enableColorScheme />
          <GlobalStyles
            styles={(theme) => ({
              ':root': {
                '--Collapsed-breakpoint': '769px', // form will stretch when viewport is below `769px`
                '--Cover-width': '50vw', // must be `vw` only
                '--Form-maxWidth': '800px',
                '--Transition-duration': '0.4s', // set to `none` to disable transition
                '--Sidebar-width': '220px',
                [theme.breakpoints.up('lg')]: {
                  '--Sidebar-width': '240px',
                },
                '--ThinSidebar-width': '80px',
                '--Header-height': '40px',
              },
            })}
          />
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <UnauthorizedDialog />
            <ErrorMessageSnackbar />
            {/* <Box sx={{
              display: 'flex',
              flexDirection: 'column',
            }}>
              <Header /> */}
              <RouterProvider router={router} />
            {/* </Box> */}
          </LocalizationProvider>
        </JoyCssVarsProvider>
      </MaterialCssVarsProvider>
    </ContextsProvider>
  )
}

export default App
