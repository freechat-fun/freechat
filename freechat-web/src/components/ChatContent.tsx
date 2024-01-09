import { useEffect, useState } from 'react';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { useErrorMessageBusContext } from "../contexts";
import { LlmResultDTO } from 'freechat-sdk';
import { SxProps } from '@mui/joy/styles/types';
import { MarkdownContent } from '.';

interface ChatContentProps {
  disabled?: boolean;
  url?: string;
  body?: string;
  initialData?: string;
  sx?: SxProps;
  onMessage?: (partialResult: LlmResultDTO) => boolean;
  onFinish?: (result: LlmResultDTO) => void;
  onClose?: () => void;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  onError?: (reason: any) => void;
}

export default function ChatContent({
  disabled = false,
  url,
  body,
  initialData,
  sx,
  onMessage,
  onFinish,
  onClose,
  onError
}: ChatContentProps) {
  const { handleError } = useErrorMessageBusContext();

  const [data, setData] = useState(initialData || '');

  useEffect(() => {
    const hasBody = body !== undefined;
    const controller = new AbortController();

    if (disabled || !url) {
      return;
    }

    const startListening = async () => {
      try {
        await fetchEventSource(url, {
          method: hasBody ? 'POST' : 'GET',
          headers: {
            'Content-Type': hasBody ? 'application/json' : 'text/event-stream',
          },
          body: body,
          signal: controller.signal,
          onopen() {
            setData('');
            return Promise.resolve();
          },
          onmessage(event) {
            const result = JSON.parse(event.data) as LlmResultDTO;
            if (result.finishReason) {
              onFinish?.(result);
            } else {
              if (onMessage === undefined || onMessage(result)) {
                setData((currentData) => currentData + result.text);
              }
            }
          },
          onerror(error) {
            onError ? onError(error) : handleError(error);
          },
          onclose() {
            onClose?.();
          }
        });
      } catch (error) {
        onError ? onError(error) : handleError(error);
      }
    };

    startListening();
    return () => controller.abort();
    
  }, [url, body, onFinish, onMessage, onError, onClose, handleError, disabled]);

  return (
    <MarkdownContent sx={sx}>
      {data}
    </MarkdownContent>
  );

}