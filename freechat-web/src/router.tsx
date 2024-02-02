import { createBrowserRouter } from 'react-router-dom';
import Home from './pages/Home';
import SignIn from './pages/account/SignIn';
import MyProfile from './pages/account/MyProfile';
import Credentials from './pages/account/Credentials';
import SidebarFrame from './pages/SidebarFrame';
import ComingSoon from './pages/ComingSoon';
import NotFound from './pages/NotFound';
import Prompts from './pages/prompt/Prompts';
import PromptInfo from './pages/prompt/PromptInfo';
import PromptEditor from './pages/prompt/PromptEditor';
import OpenApi from './pages/docs/OpenApi';
import CharacterEditor from './pages/character/CharacterEditor';
import Characters from './pages/character/Characters';

const sidebarRoutes = [
  {
    index: true,
    element: <Home />,
  },
  {
    path: 'login',
    element: <SignIn />,
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
    path: 'prompt/edit/:id',
    element: <PromptEditor />,
  },
  {
    path: 'characters',
    element: <Characters />,
  },
  {
    path: 'character/edit/:id',
    element: <CharacterEditor />,
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
    path: 'docs',
    element: <OpenApi />,
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
    path: '/w/*',
    element: <SidebarFrame />,
    children: sidebarRoutes,
  },
  {
    path: '*',
    element: <NotFound />,
  },
];

const router = createBrowserRouter(routes);

export default router;
