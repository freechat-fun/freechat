import { PropsWithChildren } from "react";
import { UserInfoProvider, ErrorMessageBusProvider, FreeChatApiProvider } from ".";

const ContextsProvider: React.FC<PropsWithChildren> = ({ children }) => {
  return (
    <FreeChatApiProvider server={import.meta.env.VITE_API_SERVER}>
      <UserInfoProvider>
        <ErrorMessageBusProvider>
          {children}
        </ErrorMessageBusProvider>
      </UserInfoProvider>
    </FreeChatApiProvider>
  );
};

export default ContextsProvider;
