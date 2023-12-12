/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable react-refresh/only-export-components */
import { PropsWithChildren, createContext, useContext, useState } from "react";

interface UserInfoContextValue {
  username: string | null | undefined,
  nickname: string | null | undefined,
  reset: (name: string | null | undefined, nick: string | null | undefined) => void;
  getDisplayName: () => string | null | undefined;
  isAuthorized: () => boolean;
}

const anonymous: UserInfoContextValue = {
  username: undefined,
  nickname: undefined,
  reset: () => {},
  getDisplayName: () => undefined,
  isAuthorized: () => false,
};

const UserInfoContext = createContext<UserInfoContextValue>(anonymous);

const UserInfoProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const metaUsername = document.querySelector('meta[name="_username"]')?.getAttribute('content');
  const metaNickname = document.querySelector('meta[name="_nickname"]')?.getAttribute('content');
  const [username, setUsername] = useState(metaUsername);
  const [nickname, setNickname] = useState(metaNickname);

  const reset = (name: string | null | undefined, nick: string | null | undefined) => {
    setUsername(name);
    setNickname(nick);
  };

  const getDisplayName = () => {
    return nickname ? nickname : username;
  }

  const isAuthorized = () => {
    return !!username;
  }

  return (
    <UserInfoContext.Provider value={{
        username,
        nickname,
        reset,
        getDisplayName,
        isAuthorized,
    }}> 
      {children}
    </UserInfoContext.Provider>
  );

};

export default UserInfoProvider;
export const useUserInfoContext = () => useContext(UserInfoContext);
