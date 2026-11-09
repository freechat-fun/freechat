import { useEffect, useRef } from 'react';
import { listenChatRun } from './chat_run';
import type { ChatRunOutcome, ChatRunSnapshot } from './chat_run';

type Options = {
  enabled: boolean;
  url: string;
  body?: string;
  sessionId?: string;
  onUpdate: (snapshot: ChatRunSnapshot) => void;
  onFinish: (outcome: ChatRunOutcome) => void;
};

export function useChatRun(options: Options): void {
  const { enabled, url, body, sessionId } = options;
  const latest = useRef(options);
  latest.current = options;
  const generation = useRef(0);

  useEffect(() => {
    if (!enabled || !url || !sessionId) return;
    const controller = new AbortController();
    const id = ++generation.current;
    const isCurrent = () => {
      const current = latest.current;
      return (
        !controller.signal.aborted &&
        id === generation.current &&
        current.enabled &&
        current.sessionId === sessionId &&
        current.url === url &&
        current.body === body
      );
    };
    // Defer the request so StrictMode's discarded effect cannot send a POST.
    void Promise.resolve().then(() =>
      listenChatRun({
        url,
        body,
        sessionId,
        signal: controller.signal,
        isCurrent,
        onUpdate: (snapshot) => latest.current.onUpdate(snapshot),
        onFinish: (outcome) => latest.current.onFinish(outcome),
      })
    );
    return () => {
      controller.abort();
    };
  }, [enabled, url, body, sessionId]);
}
