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
import PromptEdit from './pages/prompt/PromptEdit';
import OpenApi from './pages/docs/OpenApi';
import Characters from './pages/character/Characters';
import CharacterEdit from './pages/character/CharacterEdit';
import PromptTaskEdit from './pages/prompt/PromptTaskEdit';
import PromptTaskInfo from './pages/prompt/PromptTaskInfo';
import Chats from './pages/chat/Chats';

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
    path: 'chat/:id?/:mode?',
    element: <Chats />,
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
    element: <PromptEdit />,
  },
  {
    path: 'prompt/task/:id',
    element: <PromptTaskInfo />,
  },
  {
    path: 'prompt/task/edit/:id',
    element: <PromptTaskEdit />,
  },
  {
    path: 'characters',
    element: <Characters />,
  },
  {
    path: 'character/edit/:id',
    element: <CharacterEdit />,
  },
  {
    path: 'plugins',
    element: <ComingSoon />,
  },
  {
    path: 'agents',
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
