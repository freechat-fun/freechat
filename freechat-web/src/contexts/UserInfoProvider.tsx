/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable react-refresh/only-export-components */
import { PropsWithChildren, createContext, useContext, useState } from "react";

interface UserInfoContextValue {
  username: string | null | undefined,
  platform: string | null | undefined,
  csrfToken: string | null | undefined,
  resetUser: (
    name: string | null | undefined,
    from: string | null | undefined,
  ) => void;
  isAuthorized: () => boolean;
}

const anonymous: UserInfoContextValue = {
  username: undefined,
  platform: undefined,
  csrfToken: undefined,
  resetUser: () => {},
  isAuthorized: () => false,
};

const UserInfoContext = createContext<UserInfoContextValue>(anonymous);

const UserInfoProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const metaUsername = document.querySelector('meta[name="_username"]')?.getAttribute('content');
  const metaPlatform = document.querySelector('meta[name="_platform"]')?.getAttribute('content');
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const [username, setUsername] = useState(metaUsername);
  const [platform, setPlatform] = useState(metaPlatform);

  const resetUser = (
    name: string | null | undefined,
    from: string | null | undefined,
  ) => {
    setUsername(name);
    setPlatform(from);
  };

  const isAuthorized = () => {
    return !!username;
  }

  return (
    <UserInfoContext.Provider value={{
        username,
        platform,
        csrfToken,
        resetUser,
        isAuthorized,
    }}> 
      {children}
    </UserInfoContext.Provider>
  );

};

export default UserInfoProvider;
export const useUserInfoContext = () => useContext(UserInfoContext);
