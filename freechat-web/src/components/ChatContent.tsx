import { Fragment, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { useErrorMessageBusContext } from "../contexts";
import { LlmResultDTO, LlmTokenUsageDTO } from 'freechat-sdk';
import { SxProps } from '@mui/joy/styles/types';
import { LinePlaceholder, MarkdownContent } from '.';
import { Box, Chip, Divider, IconButton, Typography } from '@mui/joy';
import { ContentCopyRounded } from '@mui/icons-material';
import { getSenderReply } from '../libs/chat_utils';

type ChatContentProps = {
  disabled?: boolean;
  debugMode?: boolean;
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
  debugMode = true,
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
  const [copied, setCopied] = useState(false);

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
            setCopied(false);
            return Promise.resolve();
          },
          onmessage(event) {
            const result = JSON.parse(event.data) as LlmResultDTO;
            if (result.text) {
              if (onMessage === undefined || onMessage(result)) {
                setData((currentData) => currentData + result.text);
              }
            }
            if (result.finishReason) {
              setUsage(result.tokenUsage);
              onFinish?.(result);
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
    return () => {
      console.log('ChatContent was closed!');
      controller.abort();
    };
    
  }, [url, body, onFinish, onMessage, onError, onClose, handleError, disabled]);

  return (
    <Fragment>
      <MarkdownContent sx={sx}>
        {getSenderReply(data, debugMode)}
      </MarkdownContent>
      {debugMode && usage && (
        <Fragment>
          <LinePlaceholder spacing={2} />
          <Box sx={{
            display: 'flex',
            justifyContent: 'flex-end',
            alignItems: 'center',
          }}>
            {copied ? (<Chip variant='outlined'>{t('Copied!')}</Chip>) : (
              <IconButton onClick={() => {
                data && navigator?.clipboard?.writeText(data)
                  .then(() => setCopied(true))
                  .catch(handleError);
              }}>
                <ContentCopyRounded fontSize='small'/>
              </IconButton>
            )}
          </Box>
          <Divider sx={{ mt: 1, mb: 1 }}>{t('Token Usage')}</Divider>
          <Box sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}>
            <Typography level="body-sm">{`${t('Input')}: ${usage.inputTokenCount}`}</Typography>
            <Typography level="body-sm">{`${t('Output')}: ${usage.outputTokenCount}`}</Typography>
            <Typography level="body-sm">{`${t('Total')}: ${usage.totalTokenCount}`}</Typography>
          </Box>
        </Fragment>
      )}
    </Fragment>
  );

}