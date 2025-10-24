import {
  Fragment,
  MouseEventHandler,
  forwardRef,
  useRef,
  useState,
} from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Chip,
  CircularProgress,
  Divider,
  IconButton,
  Stack,
  StackProps,
  Typography,
  styled,
} from '@mui/material';
import {
  ChatContent,
  FlexBox,
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
  VolumeOffRounded,
  VolumeUpRounded,
} from '@mui/icons-material';

type BubbleContainerProps = StackProps & {
  isSent: boolean;
  onMouseEnter?: MouseEventHandler<HTMLDivElement>;
  onMouseLeave?: MouseEventHandler<HTMLDivElement>;
};

const BubbleContainer = styled(
  // eslint-disable-next-line react/display-name
  forwardRef<HTMLDivElement, BubbleContainerProps>((props, ref) => {
    const { children, isSent, onMouseEnter, onMouseLeave, ...others } = props;

    return (
      <Box
        sx={{ position: 'relative' }}
        onMouseEnter={onMouseEnter}
        onMouseLeave={onMouseLeave}
      >
        <Stack
          ref={ref}
          sx={[
            {
              p: 1.25,
              borderRadius: '12px',
              borderTopRightRadius: isSent ? 0 : '12px',
              borderTopLeftRadius: isSent ? '12px' : 0,
              backgroundColor: isSent ? '#0B6BCBC0' : '#FFFFFFC0',
            },
            (theme) =>
              theme.applyStyles('dark', {
                backgroundColor: isSent ? '#0B6BCBC0' : '#000000C0',
              }),
          ]}
          {...others}
        >
          {children}
        </Stack>
      </Box>
    );
  })
)();

BubbleContainer.displayName = 'ChatBubble.BubbleContainer';

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
  const [speaking, setSpeaking] = useState(false);
  const [loading, setLoading] = useState(false);

  const audioRef = useRef<HTMLAudioElement | null>(null);

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

  function getServiceUrl(path: string): string {
    return path ? serverUrl + path : path;
  }

  function handlePlay() {
    if (session?.isTtsEnabled) {
      const audioUrl = `/api/v2/tts/speak/${record.message?.messageId}`;

      if (audioRef.current) {
        // avoid icon flickering
        const handler = setTimeout(() => setLoading(true), 200);

        audioRef.current.src = getServiceUrl(audioUrl);
        audioRef.current.oncanplay = () => {
          clearTimeout(handler);
          setLoading(false);
          setSpeaking(true);
        };
        audioRef.current.onerror = () => {
          clearTimeout(handler);
          setLoading(false);
          setSpeaking(false);
        };
        audioRef.current.onended = () => {
          setLoading(false);
          setSpeaking(false);
        };
        audioRef.current.onpause = () => {
          setLoading(false);
          setSpeaking(false);
        };
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        audioRef.current.play().catch((error: any) => {
          clearTimeout(handler);
          setLoading(false);
          setSpeaking(false);
          handleError(error);
        });
      }
    }
  }

  function handleStop() {
    if (session?.isTtsEnabled) {
      if (audioRef.current) {
        audioRef.current.pause();
      }
    }
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
    >
      <Stack
        direction="row"
        justifyContent="space-between"
        spacing={2}
        sx={{ mb: 0.25, width: '100%' }}
      >
        <Typography
          variant="body2"
          sx={{ overflowWrap: 'anywhere', minWidth: '35%' }}
        >
          {nickname}
        </Typography>
        <Typography variant="body2" sx={{ overflowWrap: 'anywhere' }}>
          {getDateLabel(record.gmtCreate || new Date(), i18n.language, true)}
        </Typography>
      </Stack>

      <LinePlaceholder />

      {apiPath ? (
        <BubbleContainer isSent={isSent}>
          <ChatContent
            debugMode={debugMode}
            url={getServiceUrl(apiPath)}
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
                  {getSenderReply(content.content, debugMode, false, message?.thinking)}
                </MarkdownContent>
              )}
              {!isSent && !apiPath && (
                <Fragment>
                  <Divider sx={{ mt: 1, mb: 1 }} />
                  <Box
                    sx={{
                      display: 'flex',
                      justifyContent: 'flex-end',
                      alignItems: 'center',
                    }}
                  >
                    {content.type === 'text' && copied ? (
                      <Chip variant="outlined" label={t('Copied!')} />
                    ) : (
                      <IconButton
                        size="small"
                        onClick={() => {
                          if (content.content) {
                            navigator?.clipboard
                              ?.writeText(
                                getSenderReply(content.content, debugMode, false, message?.thinking)
                              )
                              .then(() => {
                                setCopied(true);
                                setTimeout(() => setCopied(false), 2000);
                              })
                              .catch(handleError);
                          }
                        }}
                      >
                        <ContentCopyRounded fontSize="small" />
                      </IconButton>
                    )}
                    {session?.isTtsEnabled && (
                      <Fragment>
                        {loading ? (
                          <CircularProgress size={20} />
                        ) : (
                          <IconButton
                            size="small"
                            onClick={() =>
                              speaking ? handleStop() : handlePlay()
                            }
                          >
                            {speaking ? (
                              <VolumeOffRounded fontSize="small" />
                            ) : (
                              <VolumeUpRounded fontSize="small" />
                            )}
                          </IconButton>
                        )}
                        <audio ref={audioRef} />
                      </Fragment>
                    )}
                    <IconButton
                      size="small"
                      disabled={loading}
                      onClick={() => onReplay?.()}
                    >
                      <ReplayRounded fontSize="small" />
                    </IconButton>
                  </Box>
                </Fragment>
              )}
              {debugMode && ext && (
                <Fragment>
                  {tokenUsage && (
                    <Fragment>
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
                        <Typography variant="body2">{`${t('Input')}: ${tokenUsage[0]}`}</Typography>
                        <Typography variant="body2">{`${t('Output')}: ${tokenUsage[1]}`}</Typography>
                        <Typography variant="body2">{`${t('Total')}: ${tokenUsage[2]}`}</Typography>
                      </Box>
                    </Fragment>
                  )}
                  {systemPrompt && (
                    <Fragment>
                      <LinePlaceholder spacing={3} />
                      <FlexBox sx={{ justifyContent: 'flex-end' }}>
                        <Typography variant="body2" sx={{ color: 'white' }}>
                          {t('System Prompt')}
                        </Typography>
                        <IconButton
                          aria-label="expand"
                          onClick={() => {
                            setShowSystemPrompt(true);
                          }}
                          size="small"
                          sx={{ bgcolor: 'rgba(0 0 0 / 0)' }}
                        >
                          <ArticleRounded sx={{ color: '#e0e0e0' }} />
                        </IconButton>
                      </FlexBox>
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
    </Box>
  );
}
