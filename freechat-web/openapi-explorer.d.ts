export {};

// https://github.com/Authress-Engineering/openapi-explorer/blob/release/2.1/docs/troubleshooting.md#typescript
interface OpenApiExplorerProps {
  collapse?: boolean;
  table?: boolean;
}

declare global {
  namespace React.JSX {
    interface IntrinsicElements {
      'openapi-explorer': React.DetailedHTMLProps<React.HTMLAttributes<HTMLElement>, HTMLElement> & OpenApiExplorerProps;
    }
  }
}
