import { createBrowserRouter } from 'react-router-dom';
import Portal from './pages/Portal';
import SignIn from './pages/SignIn';

const routes = [
  {
    path: '/w',
    element: <Portal />,
  },
  {
    path: '/w/login',
    element: <SignIn />,
  },
];

const router = createBrowserRouter(routes);

export default router;
