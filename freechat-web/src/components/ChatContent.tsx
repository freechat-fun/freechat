import { Fragment, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { useErrorMessageBusContext } from "../contexts";
import { LlmResultDTO, LlmTokenUsageDTO } from 'freechat-sdk';
import { SxProps } from '@mui/joy/styles/types';
import { MarkdownContent } from '.';
import { Box, Divider, Typography } from '@mui/joy';

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
  const { t } = useTranslation();
  const { handleError } = useErrorMessageBusContext();

  const [data, setData] = useState(initialData || '');
  const [usage, setUsage] = useState<LlmTokenUsageDTO>();

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
            setUsage(undefined);
            return Promise.resolve();
          },
          onmessage(event) {
            const result = JSON.parse(event.data) as LlmResultDTO;
            if (result.finishReason) {
              setUsage(result.tokenUsage);
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
    <Fragment>
      <MarkdownContent sx={sx}>
        {data}
      </MarkdownContent>
      {usage && (
        <Fragment>
          <Divider sx={{ mt: 3, mb: 1 }}>{t('Token Usage')}</Divider>
          <Box sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}>
            <Typography level='body-sm'>{`${t('Input')}: ${usage.inputTokenCount}`}</Typography>
            <Typography level='body-sm'>{`${t('Output')}: ${usage.outputTokenCount}`}</Typography>
            <Typography level='body-sm'>{`${t('Total')}: ${usage.totalTokenCount}`}</Typography>
          </Box>
        </Fragment>
      )}
    </Fragment>
  );

}