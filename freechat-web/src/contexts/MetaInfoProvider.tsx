/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable react-refresh/only-export-components */
import { PropsWithChildren, createContext, useContext, useState } from 'react';

type MetaInfoContextValue = {
  username: string | null | undefined;
  platform: string | null | undefined;
  csrfToken: string | null | undefined;
  csrfHeaderName: string;
  registrations: string[];
  location: string | null | undefined;
  icpCode: string | null | undefined;
  resetUser: (
    name: string | null | undefined,
    from: string | null | undefined
  ) => void;
  isAuthorized: () => boolean;
  isGuest: () => boolean;
};

const anonymous: MetaInfoContextValue = {
  username: undefined,
  platform: undefined,
  csrfToken: undefined,
  csrfHeaderName: 'X-CSRF-TOKEN',
  registrations: [],
  location: undefined,
  icpCode: undefined,
  resetUser: () => {},
  isAuthorized: () => false,
  isGuest: () => false,
};

const MetaInfoContext = createContext<MetaInfoContextValue>(anonymous);

const MetaInfoProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const metaUsername = document
    .querySelector('meta[name="_username"]')
    ?.getAttribute('content');
  const metaPlatform = document
    .querySelector('meta[name="_platform"]')
    ?.getAttribute('content');
  const csrfToken = document
    .querySelector('meta[name="_csrf"]')
    ?.getAttribute('content');
  const csrfHeaderName =
    document
      .querySelector('meta[name="_csrf_header"]')
      ?.getAttribute('content') ?? 'X-CSRF-TOKEN';
  const metaRegistrations = document
    .querySelector('meta[name="_registrations"]')
    ?.getAttribute('content');
  const registrations = metaRegistrations ? metaRegistrations.split(',') : [];
  const location = document
    .querySelector('meta[name="_location"]')
    ?.getAttribute('content');
  const icpCode = document
    .querySelector('meta[name="_icp_code"]')
    ?.getAttribute('content');
  const [username, setUsername] = useState(metaUsername);
  const [platform, setPlatform] = useState(metaPlatform);

  const resetUser = (
    name: string | null | undefined,
    from: string | null | undefined
  ) => {
    setUsername(name);
    setPlatform(from);
  };

  const isAuthorized = () => {
    return !!username;
  };

  const isGuest = () => {
    return platform === 'guest';
  };

  return (
    <MetaInfoContext.Provider
      value={{
        username,
        platform,
        csrfToken,
        csrfHeaderName,
        registrations,
        location,
        icpCode,
        resetUser,
        isAuthorized,
        isGuest,
      }}
    >
      {children}
    </MetaInfoContext.Provider>
  );
};

export default MetaInfoProvider;
export const useMetaInfoContext = () => useContext(MetaInfoContext);
