import { PropsWithChildren } from "react";
import { UserInfoProvider, ErrorMessageBusProvider, FreeChatApiProvider } from ".";

const ContextsProvider: React.FC<PropsWithChildren> = ({ children }) => {
  let apiServer = import.meta.env.VITE_API_SERVER;
  
  if (!apiServer && typeof window !== 'undefined') {
    const protocol = window.location.protocol;
    const host = window.location.hostname;
    const port = window.location.port;
    apiServer = `${protocol}//${host}${port ? `:${port}` : ''}`;
  }

  return (
    <FreeChatApiProvider server={apiServer}>
      <UserInfoProvider>
        <ErrorMessageBusProvider>
          {children}
        </ErrorMessageBusProvider>
      </UserInfoProvider>
    </FreeChatApiProvider>
  );
};

export default ContextsProvider;
