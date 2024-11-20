import { Dispatch, PropsWithChildren, SetStateAction, createContext, useContext, useState } from 'react';

type FrameScrollContextValue = {
  frameScrollHandler: ((frameRef: HTMLDivElement) => void) | undefined;
  setFrameScrollHandler: Dispatch<SetStateAction<((frameRef: HTMLDivElement) => void) | undefined>>;
};

const doNothing: FrameScrollContextValue = {
  frameScrollHandler: undefined,
  setFrameScrollHandler: () => () => {},
};

const FrameScrollContext = createContext<FrameScrollContextValue>(doNothing);

const FrameScrollProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const [frameScrollHandler, setFrameScrollHandler] = useState<(frameRef: HTMLDivElement) => void>();

  return (
    <FrameScrollContext.Provider
      value={{
        frameScrollHandler,
        setFrameScrollHandler,
      }}
    >
      {children}
    </FrameScrollContext.Provider>
  );
};

export default FrameScrollProvider;
export const useFrameScrollContext = () => useContext(FrameScrollContext);