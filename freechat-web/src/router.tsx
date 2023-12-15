import { createBrowserRouter } from 'react-router-dom';
import Portal from './pages/Portal';
import SignIn from './pages/account/SignIn';
import MyAccount from './pages/account/MyAccount';
import Console from './pages/Console';

const consoleRoutes = [
  {
    index: true,
    element: <MyAccount />,
  },
  {
    path: 'account',
    element: <MyAccount />,
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
];

const router = createBrowserRouter(routes);

export default router;
