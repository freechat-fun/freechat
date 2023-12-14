/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable react-refresh/only-export-components */
import { PropsWithChildren, createContext, useContext, useReducer } from "react";

type ErrorMessage = {
  code: number | undefined;
  message: string | undefined;
}

type ErrorMessageAction = {
  type: 'put';
  message: ErrorMessage;
} | {
  type: 'remove';
  message: ErrorMessage;
}

interface ErrorMessageBusContextValue {
  messages: ErrorMessage[];
  putMessage: (message: ErrorMessage) => void;
  removeMessage: (message: ErrorMessage) => void;
  handleError: (reason: any) => void;
}

const emptyMessageList = {
  messages: [],
  putMessage: () => {},
  removeMessage: () => {},
  handleError: () => {},
};

const ErrorMessageBusContext = createContext<ErrorMessageBusContextValue>(emptyMessageList);

const ErrorMessageBusProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const [messages, dispatch] = useReducer(messagesReducer, []);

  const putMessage = (message: ErrorMessage) => {
      dispatch({ type: 'put', message: message });
  }

  const removeMessage = (message: ErrorMessage) => {
      dispatch({ type: 'remove', message: message });
  }

  const handleError = (reason: any) => {
    const code = reason?.code || -1;
    const message = reason?.message || 'Unknown Error';
    putMessage({code: code, message: message});
  }

  return (
    <ErrorMessageBusContext.Provider value={{
      messages,
      putMessage,
      removeMessage,
      handleError,
    }}>
      {children}
    </ErrorMessageBusContext.Provider>
  );
};

function messagesReducer(messages: ErrorMessage[], action: ErrorMessageAction) {
  switch(action.type) {
    case 'put': {
      return [...messages, action.message];
    }
    case 'remove': {
      return messages.filter(m => m !== action.message)
    }
    default: {
      return messages;
    }
  }
}

export default ErrorMessageBusProvider;
export const useErrorMessageBusContext = () => useContext(ErrorMessageBusContext);
