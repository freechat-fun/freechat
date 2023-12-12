import { createBrowserRouter } from 'react-router-dom';
import Portal from './pages/Portal';
import SignIn from './pages/account/SignIn';
import MyAccount from './pages/account/MyAccount';

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
    path: '/w/account/my',
    element: <MyAccount />,
  },
];

const router = createBrowserRouter(routes);

export default router;
