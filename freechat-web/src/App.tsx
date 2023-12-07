import { RouterProvider } from 'react-router-dom';
import { FreeChatApiProvider } from './context';
import { CssVarsProvider, GlobalStyles, CssBaseline, Stack } from '@mui/joy';
import { ModeToggle, LanguageSelect, FreeChatLogo, AccountMenu } from './components';
import router from './router';

function App() {
  return (
    <FreeChatApiProvider server={import.meta.env.VITE_API_SERVER}>
      <CssVarsProvider>
        <CssBaseline />
        <GlobalStyles
          styles={{
            ':root': {
              '--Collapsed-breakpoint': '769px', // form will stretch when viewport is below `769px`
              '--Cover-width': '50vw', // must be `vw` only
              '--Form-maxWidth': '800px',
              '--Transition-duration': '0.4s', // set to `none` to disable transition
            },
          }}
        />
        <Stack direction="row" justifyContent="space-between" sx={{ width: '100%' }}>
          <Stack direction="row" justifyContent="flex-start">
            <FreeChatLogo />
          </Stack>
          <Stack direction="row" justifyContent="flex-end" spacing={2}>
            <LanguageSelect />
            <ModeToggle />
            <AccountMenu />
          </Stack>
        </Stack>
        <RouterProvider router={router} />
      </CssVarsProvider>
    </FreeChatApiProvider>
  )
}

export default App
