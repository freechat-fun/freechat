import { RouterProvider } from 'react-router-dom';
import { ContextsProvider } from './contexts';
import {
  CssVarsProvider as JoyCssVarsProvider,
  GlobalStyles,
  CssBaseline,
  extendTheme as joyExtendTheme,
} from '@mui/joy';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { UnauthorizedDialog, ErrorMessageSnackbar } from './components';
import router from './router';

import {
  experimental_extendTheme as materialExtendTheme,
  Experimental_CssVarsProvider as MaterialCssVarsProvider,
  THEME_ID as MATERIAL_THEME_ID,
} from '@mui/material';

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
const materialTheme = materialExtendTheme();

function App() {
  return (
    <ContextsProvider>
      <MaterialCssVarsProvider
        defaultMode="dark"
        theme={{ [MATERIAL_THEME_ID]: materialTheme }}
      >
        <JoyCssVarsProvider defaultMode="dark" theme={joyTheme}>
          <CssBaseline enableColorScheme />
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
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <UnauthorizedDialog />
            <ErrorMessageSnackbar />
            <RouterProvider router={router} />
          </LocalizationProvider>
        </JoyCssVarsProvider>
      </MaterialCssVarsProvider>
    </ContextsProvider>
  );
}

export default App;
