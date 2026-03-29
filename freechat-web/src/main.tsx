import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.tsx';
import './i18n/i18n';

function detectIosPwaMode(): boolean {
  const ua = window.navigator.userAgent;
  const isIosDevice =
    /iPad|iPhone|iPod/.test(ua) ||
    (window.navigator.platform === 'MacIntel' &&
      window.navigator.maxTouchPoints > 1);
  const isStandalone =
    window.matchMedia('(display-mode: standalone)').matches ||
    // Legacy iOS Safari flag.
    Boolean((window.navigator as Navigator & { standalone?: boolean }).standalone);

  return isIosDevice && isStandalone;
}

if (detectIosPwaMode()) {
  document.documentElement.setAttribute('data-ios-pwa', 'true');
}

// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
