/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useReducer } from 'react';
import ApiClient from 'freechat-sdk';

interface FreeChatClientProps {
  baseUrl: string | undefined;
}

type FreeChatClientAction =
  | { type: 'reset'; baseUrl: string }
  | { type: 'auth'; token: string };


const FreeChatClientContext = createContext<ApiClient | null>(null);

const FreeChatClientProvider: React.FC<React.PropsWithChildren<FreeChatClientProps>> = ({baseUrl, children }) => {
  const [client, dispatch] = useReducer(clientReducer, new ApiClient(baseUrl));

  return (
    <FreeChatClientContext.Provider value={{ client, dispatch }}>
      {children}
    </FreeChatClientContext.Provider>
  );
}

function clientReducer(client: ApiClient, action: FreeChatClientAction) {
  switch (action.type) {
    case 'reset': {
      return new ApiClient(action.baseUrl);
    }
    case 'auth': {
      client.authentications['bearerAuth'] = action.token;
      return client;
    }
  }
}

export default FreeChatClientProvider;
export const useFreeChatClientContext = () => useContext(FreeChatClientContext);