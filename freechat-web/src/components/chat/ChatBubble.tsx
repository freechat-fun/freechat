import { Fragment, MouseEventHandler, forwardRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Chip,
  Divider,
  IconButton,
  Sheet,
  SheetProps,
  Stack,
  Typography,
} from '@mui/joy';
import {
  ChatContent,
  CommonBox,
  ImagePreview,
  LinePlaceholder,
  MarkdownContent,
  TextPreviewWindow,
} from '..';
import {
  ChatMessageRecordDTO,
  ChatSessionDTO,
  LlmResultDTO,
} from 'freechat-sdk';
import { getDateLabel } from '../../libs/date_utils';
import { getSenderName, getSenderReply } from '../../libs/chat_utils';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  ArticleRounded,
  ContentCopyRounded,
  ReplayRounded,
} from '@mui/icons-material';

type BubbleContainerProps = SheetProps & {
  isSent: boolean;
  onMouseEnter?: MouseEventHandler<HTMLDivElement>;
  onMouseLeave?: MouseEventHandler<HTMLDivElement>;
};

const BubbleContainer = forwardRef<HTMLDivElement, BubbleContainerProps>(
  (props, ref) => {
    const { children, isSent, onMouseEnter, onMouseLeave, ...others } = props;

    return (
      <Box
        sx={{ position: 'relative' }}
        onMouseEnter={onMouseEnter}
        onMouseLeave={onMouseLeave}
      >
        <Sheet
          ref={ref}
          color={isSent ? 'primary' : 'neutral'}
          variant={isSent ? 'solid' : 'soft'}
          sx={(theme) => ({
            p: 1.25,
            borderRadius: 'lg',
            borderTopRightRadius: isSent ? 0 : 'lg',
            borderTopLeftRadius: isSent ? 'lg' : 0,
            backgroundColor: isSent ? '#0B6BCBC0' : '#FFFFFFC0',
            [theme.getColorSchemeSelector('dark')]: {
              backgroundColor: isSent ? '#0B6BCBC0' : '#000000C0',
            },
          })}
          {...others}
        >
          {children}
        </Sheet>
      </Box>
    );
  }
);

type ChatBubbleProps = {
  session?: ChatSessionDTO;
  record: ChatMessageRecordDTO;
  variant: 'sent' | 'received';
  debugMode?: boolean;
  apiPath?: string;
  onFinish?: (result: LlmResultDTO | undefined) => void;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  onError?: (reason: any) => void;
  onReplay?: () => void;
};

export default function ChatBubble(props: ChatBubbleProps) {
  const { serverUrl } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { i18n, t } = useTranslation('chat');

  const {
    session,
    record,
    variant,
    debugMode = false,
    apiPath,
    onFinish,
    onError = handleError,
    onReplay,
  } = props;

  const [copied, setCopied] = useState(false);
  const [showSystemPrompt, setShowSystemPrompt] = useState(false);
  const [isHovered, setIsHovered] = useState(false);

  const context = session?.context;
  const sender = session?.character;
  const isSent = variant === 'sent';

  const nickname =
    (isSent ? context?.userNickname : context?.characterNickname) ??
    getSenderName(sender);
  const message = record.message;
  const ext = record.ext;

  let tokenUsage: number[];
  let systemPrompt: string;
  if (ext) {
    if (isSent) {
      try {
        systemPrompt = JSON.parse(ext)?.text;
      } catch {
        // ignore
      }
    } else {
      try {
        tokenUsage = JSON.parse(ext);
      } catch {
        // ignore
      }
    }
  }

  function getServiceUrl(): string | undefined {
    return apiPath ? serverUrl + apiPath : undefined;
  }

  return (
    <Box
      sx={{
        maxWidth: '60%',
        minWidth: 'auto',
        display: 'grid',
        gridTemplateColumns: 'auto 1fr',
        alignItems: 'center',
      }}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <Stack
        direction="row"
        justifyContent="space-between"
        spacing={2}
        sx={{ mb: 0.25, width: '100%' }}
      >
        <Typography
          level="body-xs"
          sx={{ overflowWrap: 'anywhere', minWidth: '35%' }}
        >
          {nickname}
        </Typography>
        <Typography level="body-xs" sx={{ overflowWrap: 'anywhere' }}>
          {getDateLabel(record.gmtCreate || new Date(), i18n.language, true)}
        </Typography>
      </Stack>

      <LinePlaceholder />

      {apiPath ? (
        <BubbleContainer isSent={isSent}>
          <ChatContent
            debugMode={debugMode}
            url={getServiceUrl()}
            body={JSON.stringify(record.message)}
            onFinish={onFinish}
            onError={onError}
            sx={(theme) => ({
              color: isSent
                ? theme.palette.common.white
                : theme.palette.text.primary,
            })}
          />
        </BubbleContainer>
      ) : (
        <Fragment>
          {message?.contents?.map((content, index) => (
            <BubbleContainer key={`message-${index}`} isSent={isSent}>
              {content.type === 'image' ? (
                <ImagePreview src={content.content} />
              ) : (
                <MarkdownContent
                  sx={(theme) => ({
                    whiteSpace: 'pre-wrap',
                    color: isSent
                      ? theme.palette.common.white
                      : theme.palette.text.primary,
                  })}
                >
                  {getSenderReply(content.content, debugMode)}
                </MarkdownContent>
              )}
              {debugMode && ext && (
                <Fragment>
                  {tokenUsage && (
                    <Fragment>
                      <LinePlaceholder spacing={2} />
                      <Box
                        sx={{
                          display: 'flex',
                          justifyContent: 'flex-end',
                          alignItems: 'center',
                        }}
                      >
                        {content.type === 'text' && copied ? (
                          <Chip variant="outlined">{t('Copied!')}</Chip>
                        ) : (
                          <IconButton
                            onClick={() => {
                              if (content.content) {
                                navigator?.clipboard
                                  ?.writeText(content.content)
                                  .then(() => setCopied(true))
                                  .catch(handleError);
                              }
                            }}
                          >
                            <ContentCopyRounded fontSize="small" />
                          </IconButton>
                        )}
                      </Box>
                      <Divider sx={{ mt: 1, mb: 1 }}>
                        {t('Token Usage')}
                      </Divider>
                      <Box
                        sx={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center',
                        }}
                      >
                        <Typography level="body-sm">{`${t('Input')}: ${tokenUsage[0]}`}</Typography>
                        <Typography level="body-sm">{`${t('Output')}: ${tokenUsage[1]}`}</Typography>
                        <Typography level="body-sm">{`${t('Total')}: ${tokenUsage[2]}`}</Typography>
                      </Box>
                    </Fragment>
                  )}
                  {systemPrompt && (
                    <Fragment>
                      <LinePlaceholder spacing={3} />
                      <CommonBox sx={{ justifyContent: 'flex-end' }}>
                        <Typography level="body-sm" sx={{ color: 'white' }}>
                          {t('System Prompt')}
                        </Typography>
                        <IconButton
                          aria-label="expand"
                          onClick={() => {
                            setShowSystemPrompt(true);
                          }}
                          size="sm"
                          variant="solid"
                          color="neutral"
                          sx={{ bgcolor: 'rgba(0 0 0 / 0)' }}
                        >
                          <ArticleRounded fill="e0e0e0" />
                        </IconButton>
                      </CommonBox>
                      <TextPreviewWindow
                        title={t('System Prompt')}
                        open={showSystemPrompt}
                        setOpen={setShowSystemPrompt}
                      >
                        {systemPrompt}
                      </TextPreviewWindow>
                    </Fragment>
                  )}
                </Fragment>
              )}
            </BubbleContainer>
          ))}
        </Fragment>
      )}
      {!isSent && !apiPath && (
        <IconButton
          size="sm"
          onClick={() => onReplay?.()}
          sx={{ visibility: isHovered ? 'visible' : 'hidden' }}
        >
          <ReplayRounded fontSize="small" />
        </IconButton>
      )}
    </Box>
  );
}
