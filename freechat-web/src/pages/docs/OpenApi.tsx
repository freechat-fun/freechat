import 'openapi-explorer';
import { reactEventListener } from 'openapi-explorer/dist/es/react';
import { useEffect, useRef, useState } from 'react';
import { useFreeChatApiContext } from '../../contexts';
import { useTheme } from '@mui/joy';
import { LinePlaceholder } from '../../components';

export default function OpenApi() {
  const theme = useTheme();
  const explorerRef = useRef<HTMLElement>(null);
  const { serverUrl } = useFreeChatApiContext();
  const [specLoaded, setSpecLoaded] = useState(false);

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
        --input-bg: ${theme.palette.background.surface};
        --nav-hover-bg-color: ${theme.palette.neutral.softHoverBg};
        --nav-hover-text-color:${theme.palette.neutral.softHoverColor};
        --primary-color: ${theme.palette.primary[500]};
        --bg: ${theme.palette.background.body};
        --bg1: ${theme.palette.background.surface};
        --bg2: ${theme.palette.background.surface};
        --bg3: ${theme.palette.background.surface};
      }
      `
      apiExplorer?.shadowRoot?.appendChild(style);
    }
  }, [specLoaded, theme.palette.background.body, theme.palette.background.surface, theme.palette.neutral.softHoverBg, theme.palette.neutral.softHoverColor, theme.palette.primary]);

  return (
    <>
    <style>
      {`
      openapi-explorer::part(section-navbar) {
        background-color: ${theme.palette.neutral.softBg};
        border-radius: 6px;
        color: ${theme.palette.text.primary};
      }
      openapi-explorer::part(section-main-content) {
        background-color: ${theme.palette.background.body};
        border-radius: 6px;
        color: ${theme.palette.text.primary};
      }
      openapi-explorer::part(navbar-operations-header) {
        background: ${theme.palette.neutral.softBg};
        border-radius: 6px;
        color: ${theme.palette.primary.softColor};
      }
      openapi-explorer::part(btn-fill) {
        background-color: ${theme.palette.background.level3};
        color: ${theme.palette.text.primary};
      }
      openapi-explorer::part(btn-outline) {
        color: ${theme.palette.text.primary};
      }
      openapi-explorer::part(btn-search) {
        background-color: ${theme.palette.background.body};
        color: ${theme.palette.text.primary};
      }
      openapi-explorer::part(label-operation-path) {
        color: ${theme.palette.text.tertiary};
      }
      `}
    </style>
    <LinePlaceholder />
    <openapi-explorer
      ref={explorerRef}
      spec-url={specUrl}
      hide-components
    />
    <LinePlaceholder spacing={1} />
  </>
  );
}