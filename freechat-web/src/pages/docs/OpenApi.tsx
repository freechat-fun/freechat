import 'openapi-explorer';
import { reactEventListener } from 'openapi-explorer/dist/es/react';
import { useEffect, useRef, useState } from 'react';
import { useFreeChatApiContext } from '../../contexts';
import { DocumentSkeleton, LinePlaceholder } from '../../components';
import { useTheme } from '@mui/material';

export default function OpenApi() {
  const theme = useTheme();
  const explorerRef = useRef<HTMLElement>(null);
  const { serverUrl } = useFreeChatApiContext();
  const [specLoaded, setSpecLoaded] = useState(false);
  const [loading, setLoading] = useState(true);

  const specUrl = `${serverUrl?.replace('http://localhost:3000', 'http://127.0.0.1:8080') ?? 'http://127.0.0.1:8080'}/public/openapi/v3/api-docs/g-all`;

  // Necessary because react by default does not know how to listen to HTML5 events
  reactEventListener({ useEffect }, 'spec-loaded', () => setSpecLoaded(true));

  useEffect(() => {
    if (specLoaded) {
      const apiExplorer = explorerRef.current;
      const style = document.createElement('style');
      style.innerHTML = `
      :host {
        --border-radius: 4px;
        --input-bg: ${theme.palette.background.default};
        --nav-hover-bg-color: ${theme.palette.background.paper};
        --nav-hover-text-color:${theme.palette.background.default};
        --primary-color: ${theme.palette.primary};
        --bg: ${theme.palette.background.default};
        --bg1: ${theme.palette.background.paper};
        --bg2: ${theme.palette.background.paper};
        --bg3: ${theme.palette.background.paper};
      }
      `;
      apiExplorer?.shadowRoot?.appendChild(style);
      setLoading(false);
    }
  }, [
    specLoaded,
    theme.palette.background.default,
    theme.palette.background.paper,
    theme.palette.primary,
  ]);

  return (
    <main>
      <style>
        {`
        openapi-explorer::part(section-navbar) {
          background-color: ${theme.palette.background.paper};
          border-radius: 6px;
          color: ${theme.palette.text.primary};
        }
        openapi-explorer::part(section-main-content) {
          background-color: ${theme.palette.background.default};
          border-radius: 6px;
          color: ${theme.palette.text.primary};
        }
        openapi-explorer::part(navbar-operations-header) {
          background: ${theme.palette.background.paper};
          border-radius: 6px;
          color: ${theme.palette.primary.main};
        }
        openapi-explorer::part(btn-fill) {
          background-color: ${theme.palette.background.paper};
          color: ${theme.palette.text.primary};
        }
        openapi-explorer::part(btn-outline) {
          color: ${theme.palette.text.primary};
        }
        openapi-explorer::part(btn-search) {
          background-color: ${theme.palette.background.default};
          color: ${theme.palette.text.primary};
        }
        openapi-explorer::part(label-operation-path) {
          color: ${theme.palette.text.primary};
        }
        `}
      </style>
      <LinePlaceholder />
      <openapi-explorer
        ref={explorerRef}
        spec-url={specUrl}
        hide-components
        style={{
          display: loading ? 'none' : 'unset',
        }}
      />
      <DocumentSkeleton lines={3} loading={loading} sx={{ width: '100%' }} />
      <LinePlaceholder spacing={1} />
    </main>
  );
}
