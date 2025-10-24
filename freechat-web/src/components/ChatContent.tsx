import { Fragment, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { useErrorMessageBusContext } from '../contexts';
import { LlmResultDTO, TokenUsageDTO } from 'freechat-sdk';
import { SxProps, Theme } from '@mui/material/styles';
import { LinePlaceholder, MarkdownContent } from '.';
import { Box, Chip, Divider, IconButton, Typography } from '@mui/material';
import { ContentCopyRounded } from '@mui/icons-material';
import { getSenderReply } from '../libs/chat_utils';
import { getMessageText } from '../libs/template_utils';

type ChatContentProps = {
  disabled?: boolean;
  debugMode?: boolean;
  url?: string;
  body?: string;
  initialData?: string;
  sx?: SxProps<Theme>;
  onMessage?: (partialResult: LlmResultDTO) => boolean;
  onFinish?: (result?: LlmResultDTO) => void;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  onError?: (reason: any) => void;
};

export default function ChatContent({
  disabled = false,
  debugMode = true,
  url,
  body,
  initialData,
  sx,
  onMessage,
  onFinish,
  onError,
}: ChatContentProps) {
  const { t } = useTranslation('chat');
  const { handleError } = useErrorMessageBusContext();

  const [data, setData] = useState(initialData || '');
  const [usage, setUsage] = useState<TokenUsageDTO>();
  const [copied, setCopied] = useState(false);

  const acRef = useRef<AbortController | null>(null);

  useEffect(() => {
    if (disabled || !url || acRef.current) {
      return;
    }
    const hasBody = body !== undefined;
    let fullThinking = '';
    let fullText = '';
    acRef.current = new AbortController();

    const startListening = async () => {
      try {
        await fetchEventSource(url, {
          method: hasBody ? 'POST' : 'GET',
          headers: {
            'Content-Type': hasBody ? 'application/json' : 'text/event-stream',
          },
          body: body,
          signal: acRef.current?.signal,
          async onopen(response) {
            setUsage(undefined);
            setCopied(false);
            if (response.ok) {
              setData('');
            } else if (response.status === 429) {
              const message = `[${t('Quota has been used up, you can use your API key to continue chatting.')}]`;
              const error = { code: response.status, message: message };
              setData(message);
              onError?.(error);
            } else {
              const message = `[${response.statusText}]`;
              const error = { code: response.status, message: message };
              setData(message);
              onError?.(error);
            }
            return Promise.resolve();
          },
          onmessage(event) {
            const result = JSON.parse(event.data) as LlmResultDTO;
              
            if (result.finishReason) {
              setUsage(result.tokenUsage);
              onFinish?.(result);
            } else {
              const thinkingChunk = result.message?.thinking;
              const textChunk = getMessageText(result.message) ?? '';

              if (thinkingChunk) {
                fullThinking = fullThinking + thinkingChunk;
              }

              if (textChunk) {
                fullText = fullText + textChunk;
              }

              const fullMessage = getSenderReply(fullText, debugMode, false, fullThinking);
              if (thinkingChunk || textChunk) {
                if (onMessage === undefined || onMessage(result)) {
                  setData(fullMessage);
                }
              } else if (result.text) {
                setData(result.text);
              }
            }
          },
          onerror(error) {
            console.error('The connection has an error...');
            if (onError) {
              onError(error);
            } else {
              handleError(error);
            }
          },
          onclose() {
            console.error('The connection has been closed...');
            onFinish?.();
          },
        });
      } catch (error) {
        console.error('The connection has an error...');
        if (onError) {
          onError(error);
        } else {
          handleError(error);
        }
      }
    };

    startListening();
    return () => {
      acRef.current?.abort();
      acRef.current = null;
    };
  }, [url, body, onFinish, onMessage, onError, handleError, disabled, t]);

  return (
    <Fragment>
      <MarkdownContent sx={sx}>
        {data}
      </MarkdownContent>
      {debugMode && usage && (
        <Fragment>
          <LinePlaceholder spacing={2} />
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'flex-end',
              alignItems: 'center',
            }}
          >
            {copied ? (
              <Chip variant="outlined" label={t('Copied!')} />
            ) : (
              <IconButton
                onClick={() => {
                  if (data) {
                    navigator?.clipboard
                      ?.writeText(data)
                      .then(() => setCopied(true))
                      .catch(handleError);
                  }
                }}
                size="small"
              >
                <ContentCopyRounded fontSize="small" />
              </IconButton>
            )}
          </Box>
          <Divider sx={{ my: 1 }}>{t('Token Usage')}</Divider>
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
            }}
          >
            <Typography variant="body2">{`${t('Input')}: ${usage.inputTokenCount}`}</Typography>
            <Typography variant="body2">{`${t('Output')}: ${usage.outputTokenCount}`}</Typography>
            <Typography variant="body2">{`${t('Total')}: ${usage.totalTokenCount}`}</Typography>
          </Box>
        </Fragment>
      )}
    </Fragment>
  );
}
