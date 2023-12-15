import { RouterProvider } from 'react-router-dom';
import { ContextsProvider } from './context';
import { CssVarsProvider, GlobalStyles, CssBaseline, Box } from '@mui/joy';
import { UnauthorizedDialog, ErrorMessageSnackbar } from './components';
import { Header } from './components';
import router from './router';

function App() {
  return (
    <ContextsProvider>
      <CssVarsProvider>
        <CssBaseline />
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
              '--Header-height': '40px',
            },
          })}
        />
        <UnauthorizedDialog />
        <ErrorMessageSnackbar />
        <Box sx={{
          display: 'flex',
          flexDirection: 'column',
        }}>
          <Header />
          <RouterProvider router={router} />
        </Box>
      </CssVarsProvider>
    </ContextsProvider>
  )
}

export default App
