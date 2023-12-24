import { createBrowserRouter } from 'react-router-dom';
import Portal from './pages/Portal';
import SignIn from './pages/account/SignIn';
import MyProfile from './pages/account/MyProfile';
import Credentials from './pages/account/Credentials';
import Console from './pages/Console';
import ComingSoon from './pages/ComingSoon';
import NotFound from './pages/NotFound';
import Prompts from './pages/prompt/Prompts';
import PromptInfo from './pages/prompt/PromptInfo';

const consoleRoutes = [
  {
    index: true,
    element: <MyProfile />,
  },
  {
    path: 'prompts',
    element: <Prompts />,
  },
  {
    path: 'prompt/:id',
    element: <PromptInfo />,
  },
  {
    path: 'plugins',
    element: <ComingSoon />,
  },
  {
    path: 'flows',
    element: <ComingSoon />,
  },
  {
    path: 'profile',
    element: <MyProfile />,
  },
  {
    path: 'credentials',
    element: <Credentials />,
  },
  {
    path: '*',
    element: <NotFound />,
  },
];

const routes = [
  {
    path: '/w',
    element: <Portal />,
  },
  {
    path: '/w/login',
    element: <SignIn />,
  },
  {
    path: '/w/console/*',
    element: <Console />,
    children: consoleRoutes,
  },
  {
    path: '*',
    element: <NotFound />,
  },
];

const router = createBrowserRouter(routes);

export default router;
