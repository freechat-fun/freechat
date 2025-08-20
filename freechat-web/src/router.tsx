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
import PrivacyPolicy from './pages/PrivacyPolicy';

const sidebarRoutes = [
  {
    index: true,
    element: <Home />,
  },
  {
    path: 'w',
    element: <Home />,
  },
  {
    path: 'w/login',
    element: <SignIn />,
  },
  {
    path: 'w/chat/:id?/:mode?',
    element: <Chats />,
  },
  {
    path: 'w/prompts',
    element: <Prompts />,
  },
  {
    path: 'w/prompt/:id',
    element: <PromptInfo />,
  },
  {
    path: 'w/prompt/edit/:id',
    element: <PromptEdit />,
  },
  {
    path: 'w/prompt/task/:id',
    element: <PromptTaskInfo />,
  },
  {
    path: 'w/prompt/task/edit/:id',
    element: <PromptTaskEdit />,
  },
  {
    path: 'w/characters',
    element: <Characters />,
  },
  {
    path: 'w/character/edit/:id',
    element: <CharacterEdit />,
  },
  {
    path: 'w/plugins',
    element: <ComingSoon />,
  },
  {
    path: 'w/agents',
    element: <ComingSoon />,
  },
  {
    path: 'w/docs',
    element: <OpenApi />,
  },
  {
    path: 'w/profile',
    element: <MyProfile />,
  },
  {
    path: 'w/credentials',
    element: <Credentials />,
  },
  {
    path: '*',
    element: <NotFound />,
  },
];

const routes = [
  {
    path: '/privacy-policy',
    element: <PrivacyPolicy />,
  },
  {
    path: '/*',
    element: <SidebarFrame />,
    children: sidebarRoutes,
  },
];

const router = createBrowserRouter(routes);

export default router;
