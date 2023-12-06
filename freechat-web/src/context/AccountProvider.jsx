import { createContext, useReducer, useContext } from 'react';
import { PropTypes } from 'prop-types';

AccountProvider.propTypes = {
  children: PropTypes.node.isRequired,
};

const AccountContext = createContext(null);

export default function AccountProvider({ children }) {

  const [account, dispatch] = useReducer(accountReducer, undefined);

  return (
    <AccountContext.Provider value={{ account, dispatch }}>
      {children}
    </AccountContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export const useAccountContext = () => useContext(AccountContext);

function accountReducer(state, action) {
  switch (action.type) {
    case 'reset': {
      return action.account;
    }
  }
}
