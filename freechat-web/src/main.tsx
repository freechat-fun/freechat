import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './i18n/i18n'

// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
