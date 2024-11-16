declare module 'openapi-explorer/dist/es/react' {
  export function reactEventListener(
    args: {
      useEffect: typeof React.useEffect;
    },
    eventName: string,
    functionCallback: EventListenerOrEventListenerObject
  ): void;
}
