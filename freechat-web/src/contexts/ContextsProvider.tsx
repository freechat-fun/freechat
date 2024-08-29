import { PropsWithChildren } from "react";
import { MetaInfoProvider, ErrorMessageBusProvider, FreeChatApiProvider } from ".";

const ContextsProvider: React.FC<PropsWithChildren> = ({ children }) => {
  let apiServer = import.meta.env.VITE_API_SERVER;
  
  if (!apiServer && typeof window !== 'undefined') {
    const protocol = window.location.protocol;
    const host = window.location.hostname;
    const port = window.location.port;

    const location = document.querySelector('meta[name="_location"]')?.getAttribute('content');
    console.log(`location: ${location}`)
    apiServer = location === 'CN' && host !== 'localhost' ?
      'https://www.freechat.fun' :
      `${protocol}//${host}${port ? `:${port}` : ''}`;
  }

  return (
    <FreeChatApiProvider server={apiServer}>
      <MetaInfoProvider>
        <ErrorMessageBusProvider>
          {children}
        </ErrorMessageBusProvider>
      </MetaInfoProvider>
    </FreeChatApiProvider>
  );
};

export default ContextsProvider;
