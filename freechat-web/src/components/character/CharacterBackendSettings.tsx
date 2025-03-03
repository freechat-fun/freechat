import {
  Fragment,
  createRef,
  forwardRef,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Button,
  Card,
  CardProps,
  Chip,
  ChipDelete,
  CircularProgress,
  FormHelperText,
  FormLabel,
  IconButton,
  Input,
  Option,
  Radio,
  RadioGroup,
  Select,
  Slider,
  Stack,
  Switch,
  Typography,
} from '@mui/joy';
import {
  ArrowOutwardRounded,
  AudioFileRounded,
  DoneRounded,
  KeyRounded,
  PlayCircleFilledRounded,
  TuneRounded,
  UndoRounded,
} from '@mui/icons-material';
import {
  AiModelInfoDTO,
  CharacterBackendDetailsDTO,
  PromptTaskDTO,
  PromptTaskDetailsDTO,
} from 'freechat-sdk';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  CommonBox,
  CommonContainer,
  CommonGridBox,
  LinePlaceholder,
  OptionCard,
  OptionTooltip,
  TinyInput,
} from '..';
import { HelpIcon } from '../icon';
import { enabledApiKey, extractModelProvider } from '../../libs/template_utils';
import { providers as modelProviders } from '../../configs/model-providers-config';
import {
  AiApiKeySettings,
  AzureOpenAiSettings,
  DashScopeSettings,
  OllamaSettings,
  OpenAiSettings,
} from '../prompt';

type CharacterBackendSettingsProps = CardProps & {
  backend?: CharacterBackendDetailsDTO;
  onSave?: (
    backend: CharacterBackendDetailsDTO,
    promptTask?: PromptTaskDTO,
    redirectToChatPrompt?: boolean,
    redirectHash?: string
  ) => void;
  onCancel?: () => void;
};

const MAX_FILE_SIZE = 1 * 1024 * 1024;
let idCounter = 0;

const CharacterBackendSettings = forwardRef<
  HTMLDivElement,
  CharacterBackendSettingsProps
>((props, ref) => {
  const { t } = useTranslation('character');
  const {
    aiServiceApi,
    characterApi,
    promptTaskApi,
    ttsServiceApi,
    serverUrl,
  } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const { backend, onSave, onCancel, sx, ...others } = props;

  const [messageWindowSize, setMessageWindowSize] = useState(
    backend?.messageWindowSize ?? 50
  );
  const [longTermMemoryWindowSize, setLongTermMemoryWindowSize] = useState(
    backend?.longTermMemoryWindowSize ?? 5
  );
  const [proactiveChatWaitingTime, setProactiveChatWaitingTime] = useState(
    backend?.proactiveChatWaitingTime ?? 2
  );
  const [initQuota, setInitQuota] = useState(backend?.initQuota ?? 0);
  const [quotaType, setQuotaType] = useState(
    backend?.quotaType === 'tokens' ? backend?.quotaType : 'messages'
  );
  const [enableQuota, setEnableQuota] = useState(
    backend?.quotaType === 'messages' || backend?.quotaType === 'tokens'
  );
  const [enableAlbumTool, setEnableAlbumTool] = useState(
    backend?.enableAlbumTool ?? false
  );
  const [enableTts, setEnableTts] = useState(backend?.enableTts ?? false);
  const [ttsBuiltinSpeaders, setTtsBuiltinSpeakers] = useState<Array<string>>(
    []
  );
  const [ttsSpeakerIdx, setTtsSpeakerIdx] = useState(backend?.ttsSpeakerIdx);
  const [ttsSpeakerWav, setTtsSpeakerWav] = useState(backend?.ttsSpeakerWav);
  const [ttsSpeakerType, setTtsSpeakerType] = useState(
    backend?.ttsSpeakerType ?? 'idx'
  );
  const [fileUploading, setFileUploading] = useState(false);
  const [fileInvalid, setFileInvalid] = useState(false);

  const fileInputId = useRef(`voice-file-upload-input-${idCounter}`).current;
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [playing, setPlaying] = useState(false);
  const audioRefs = useRef(Array(2).fill(createRef<HTMLAudioElement | null>()));

  const [promptTask, setPromptTask] = useState<PromptTaskDetailsDTO>();
  const [models, setModels] = useState<(AiModelInfoDTO | undefined)[]>([]);
  const [provider, setProvider] = useState('open_ai');
  const [apiKeyName, setApiKeyName] = useState('');
  const [apiKeyValue, setApiKeyValue] = useState('');
  const [apiKeyNames, setApiKeyNames] = useState<string[]>([]);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [parameters, setParameters] = useState<{ [key: string]: any }>({});

  const [modelSetting, setModelSetting] = useState(false);
  const [openApiKeySetting, setOpenApiKeySetting] = useState(false);

  const inputRefs = useRef(Array(4).fill(createRef<HTMLInputElement | null>()));

  const matchingModels = useMemo(() => {
    return provider && models
      ? models.filter(
          (model) =>
            model && model.provider === provider && model.type === 'text2chat'
        )
      : [];
  }, [models, provider]);

  const QUOTA_TYPES = [
    {
      label: 'Messages',
      value: 'messages',
    },
    {
      label: 'Tokens',
      value: 'tokens',
    },
  ];

  useEffect(() => {
    idCounter++;
  }, []);

  useEffect(() => {
    aiServiceApi?.listAiModelInfo().then(setModels).catch(handleError);
  }, [aiServiceApi, handleError]);

  useEffect(() => {
    ttsServiceApi
      ?.listTtsBuiltinSpeakers()
      .then((resp) => {
        setTtsBuiltinSpeakers(resp);
        setTtsSpeakerIdx((defaultSpeaker) => {
          if (resp.length > 0 && !defaultSpeaker) {
            return resp[0];
          }
          return defaultSpeaker;
        });
      })
      .catch(handleError);
  }, [ttsServiceApi, handleError]);

  useEffect(() => {
    if (backend?.chatPromptTaskId) {
      promptTaskApi
        ?.getPromptTask(backend?.chatPromptTaskId)
        .then(setPromptTask)
        .catch(handleError);
    }

    setInitQuota(backend?.initQuota ?? 0);
    setQuotaType(
      backend?.quotaType === 'tokens' ? backend?.quotaType : 'messages'
    );
    setEnableQuota(
      backend?.quotaType === 'messages' || backend?.quotaType === 'tokens'
    );
    setEnableAlbumTool(backend?.enableAlbumTool ?? false);
    setEnableTts(backend?.enableTts ?? false);
    setTtsSpeakerIdx(backend?.ttsSpeakerIdx);
    setTtsSpeakerWav(backend?.ttsSpeakerWav);
    setTtsSpeakerType(backend?.ttsSpeakerType ?? 'idx');
  }, [backend, handleError, promptTaskApi]);

  useEffect(() => {
    aiServiceApi
      ?.listAiApiKeys(provider)
      .then((resp) =>
        setApiKeyNames(
          resp
            .filter((key) => !!key.name && key.enabled)
            .map((key) => key.name as string)
        )
      )
      .catch(handleError);
  }, [aiServiceApi, handleError, provider]);

  useEffect(() => {
    setProvider(extractModelProvider(promptTask?.modelId) ?? 'open_ai');
    setApiKeyName(promptTask?.apiKeyName ?? '');
    setApiKeyValue(promptTask?.apiKeyValue ?? '');
    setParameters({ ...promptTask?.params, modelId: promptTask?.modelId });
  }, [promptTask]);

  function handleModelProviderSelectChange(
    _event: React.SyntheticEvent | null,
    newValue: string | null
  ): void {
    if (newValue && newValue !== provider) {
      setProvider(newValue);
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  function handleModelSettings(parameters: { [key: string]: any }) {
    setParameters(parameters);
    setModelSetting(false);
  }

  function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    if (!backend?.backendId || !characterApi) {
      return;
    }

    const file = event.target.files && event.target.files[0];
    if (file) {
      if (file.size > MAX_FILE_SIZE) {
        setFileInvalid(true);
      } else {
        setFileInvalid(false);
        setFileUploading(true);
        characterApi
          .uploadCharacterVoice(backend?.backendId, file)
          .then(setTtsSpeakerWav)
          .catch(handleError)
          .finally(() => setFileUploading(false));
      }
    } else {
      setFileInvalid(false);
    }
  }

  function handleFileDelete() {
    setTtsSpeakerWav((exists) => {
      if (exists && backend?.backendId && characterApi) {
        setFileUploading(true);
        characterApi
          .deleteCharacterVoice(backend.backendId, exists)
          .finally(() => setFileUploading(false));
      }
      return undefined;
    });
  }

  function handleFileModify(): void {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  }

  function getServiceUrl(apiPath: string): string {
    return apiPath ? serverUrl + apiPath : apiPath;
  }

  function handlePlay() {
    if (enableTts) {
      const speaker = ttsSpeakerType === 'idx' ? ttsSpeakerIdx : ttsSpeakerWav;
      const player = ttsSpeakerType === 'idx' ? 0 : 1;
      const audioUrl = `/api/v2/public/tts/play/sample/${ttsSpeakerType}/${encodeURIComponent(speaker ?? '')}`;

      if (audioRefs.current[player].current) {
        setPlaying(true);
        audioRefs.current[player].current.src = getServiceUrl(audioUrl);
        audioRefs.current[player].current.onerror = () => setPlaying(false);
        audioRefs.current[player].current.onended = () => setPlaying(false);
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        audioRefs.current[player].current.play().catch((error: any) => {
          setPlaying(false);
          handleError(error);
        });
      }
    }
  }

  function handleSave(
    redirectToChatPrompt: boolean = false,
    redirectHash?: string
  ) {
    let editBackend;
    if (backend) {
      editBackend = {
        ...backend,
        messageWindowSize,
        longTermMemoryWindowSize,
        proactiveChatWaitingTime,
        enableAlbumTool,
        enableTts,
        ttsSpeakerIdx,
        ttsSpeakerWav,
        ttsSpeakerType,
      };
    } else {
      editBackend = new CharacterBackendDetailsDTO();
      editBackend.messageWindowSize = messageWindowSize;
      editBackend.longTermMemoryWindowSize = longTermMemoryWindowSize;
      editBackend.proactiveChatWaitingTime = proactiveChatWaitingTime;
      editBackend.enableAlbumTool = enableAlbumTool;
      editBackend.enableTts = enableTts;
      editBackend.ttsSpeakerIdx = ttsSpeakerIdx;
      editBackend.ttsSpeakerWav = ttsSpeakerWav;
      editBackend.ttsSpeakerType = ttsSpeakerType;
    }

    if (enableQuota) {
      editBackend.quotaType = quotaType;
      editBackend.initQuota = initQuota * (quotaType === 'tokens' ? 1000 : 1);
    }

    const { modelId, ...modelParameters } = parameters;

    const editPromptTask = new PromptTaskDTO();
    editPromptTask.apiKeyName = apiKeyName;
    editPromptTask.apiKeyValue = apiKeyValue;
    editPromptTask.modelId = modelId;
    editPromptTask.params = modelParameters;

    onSave?.(editBackend, editPromptTask, redirectToChatPrompt, redirectHash);
  }

  return (
    <Fragment>
      <Card
        ref={ref}
        sx={{
          my: 2,
          mx: { xs: 0, sm: 2 },
          p: 2,
          boxShadow: 'sm',
          ...sx,
        }}
        {...others}
      >
        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Message Window')}
            </Typography>
            <OptionTooltip
              title={t(
                'Set the maximum number of historical messages sent to the model.'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
              <TinyInput
                type="number"
                slotProps={{
                  input: {
                    ref: inputRefs.current[0],
                    min: 10,
                    max: 500,
                    step: 10,
                  },
                }}
                value={messageWindowSize}
                onChange={(event) => setMessageWindowSize(+event.target.value)}
              />
            </CommonContainer>
          </CommonContainer>
          <Slider
            value={messageWindowSize}
            min={10}
            max={500}
            step={10}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) =>
              setMessageWindowSize(newValue as number)
            }
          />
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Long Term Memory Window')}
            </Typography>
            <OptionTooltip
              title={t(
                'Set the maximum number of long term memory rounds (a round includes a user message and a character reply) sent to the model, 0 to disable. Recalled long-term memory messages will be placed at the top of the historical message window.'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
              <TinyInput
                type="number"
                slotProps={{
                  input: {
                    ref: inputRefs.current[1],
                    min: 0,
                    max: 30,
                    step: 5,
                  },
                }}
                value={longTermMemoryWindowSize}
                onChange={(event) =>
                  setLongTermMemoryWindowSize(+event.target.value)
                }
              />
            </CommonContainer>
          </CommonContainer>
          <Slider
            value={longTermMemoryWindowSize}
            step={5}
            min={0}
            max={30}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) =>
              setLongTermMemoryWindowSize(newValue as number)
            }
          />
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Proactive Chat Waiting Time')}
            </Typography>
            <OptionTooltip
              title={t(
                'Set the number of minutes to wait before evoking a proactive message, 0 to disable.'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
              <TinyInput
                type="number"
                slotProps={{
                  input: {
                    ref: inputRefs.current[2],
                    min: 0,
                    max: 60,
                    step: 1,
                  },
                }}
                value={proactiveChatWaitingTime}
                onChange={(event) =>
                  setProactiveChatWaitingTime(+event.target.value)
                }
              />
            </CommonContainer>
          </CommonContainer>
          <Slider
            value={proactiveChatWaitingTime}
            step={1}
            min={0}
            max={60}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) =>
              setProactiveChatWaitingTime(newValue as number)
            }
          />
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Quota Limit')}
            </Typography>
            <OptionTooltip
              title={t(
                'After reaching the quota limit, users need to use their own API-Key to continue chatting.'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <Switch
              checked={enableQuota}
              sx={{ ml: 'auto' }}
              onChange={() => setEnableQuota(!enableQuota)}
            />
          </CommonContainer>

          <CommonContainer>
            <RadioGroup
              orientation="horizontal"
              name="format"
              size="sm"
              value={quotaType}
              onChange={(event) => setQuotaType(event.target.value)}
              sx={{
                my: 2,
                p: 0.5,
                borderRadius: '12px',
                bgcolor: 'neutral.softBg',
                '--RadioGroup-gap': '4px',
                '--Radio-actionRadius': '8px',
              }}
            >
              {QUOTA_TYPES.map((item) => (
                <Radio
                  disabled={!enableQuota}
                  key={`format-${item.value}`}
                  color="neutral"
                  size="sm"
                  value={item.value}
                  disableIcon
                  label={
                    <Typography noWrap level="body-xs">
                      {item.label}
                    </Typography>
                  }
                  variant="plain"
                  sx={{
                    p: 0.5,
                    alignItems: 'center',
                  }}
                  slotProps={{
                    action: ({ checked }) => ({
                      sx: (theme) => ({
                        ...(checked && {
                          backgroundColor: theme.palette.primary.softHoverBg,
                          boxShadow: 'sm',
                          '&:hover': {
                            bgcolor: theme.palette.primary.softActiveBg,
                          },
                        }),
                      }),
                    }),
                  }}
                />
              ))}
            </RadioGroup>

            <CommonContainer
              sx={{ ml: 'auto', justifyContent: 'space-between' }}
            >
              <TinyInput
                disabled={!enableQuota}
                type="number"
                slotProps={{
                  input: {
                    ref: inputRefs.current[3],
                    min: 0,
                    max: 10000,
                    step: 10,
                  },
                }}
                value={initQuota}
                onChange={(event) => setInitQuota(+event.target.value)}
              />
              <Typography
                sx={{
                  display: quotaType === 'tokens' ? 'block' : 'none',
                }}
              >
                K
              </Typography>
            </CommonContainer>
          </CommonContainer>
        </OptionCard>

        <OptionCard>
          <CommonGridBox>
            <Typography level="title-sm" textColor="neutral">
              {t('Model')}
            </Typography>
            <Select
              placeholder={
                <Typography textColor="gray">
                  {t('Select model providers')}
                </Typography>
              }
              value={provider}
              onChange={handleModelProviderSelectChange}
              sx={{
                flex: 1,
              }}
            >
              {modelProviders.map((p) => (
                <Option value={p.provider} key={`options-${p.provider}`}>
                  {p.label}
                </Option>
              ))}
            </Select>

            <Typography level="title-sm" textColor="neutral">
              {' '}
            </Typography>
            <CommonContainer
              sx={{
                mt: 2,
                justifyContent: 'space-between',
              }}
            >
              <Button
                size="sm"
                variant="soft"
                color={apiKeyName || apiKeyValue ? 'neutral' : 'danger'}
                disabled={!enabledApiKey(provider)}
                startDecorator={<KeyRounded />}
                onClick={() => setOpenApiKeySetting(true)}
              >
                {t('API Key')}
              </Button>
              <Button
                size="sm"
                variant="soft"
                color={parameters?.modelId ? 'primary' : 'danger'}
                disabled={!provider}
                startDecorator={<TuneRounded />}
                onClick={() => setModelSetting(true)}
              >
                {t('button:Tune')}
              </Button>
            </CommonContainer>
          </CommonGridBox>
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Enable TTS')}
            </Typography>
            <OptionTooltip title={t('Enable characters to read replies.')}>
              <HelpIcon />
            </OptionTooltip>
            <Switch
              checked={enableTts}
              sx={{ ml: 'auto' }}
              onChange={() => setEnableTts(!enableTts)}
            />
          </CommonContainer>
          <RadioGroup
            name="ttsSpeakerType"
            value={ttsSpeakerType}
            onChange={(event) => setTtsSpeakerType(event.target.value)}
            sx={{ mt: 2 }}
          >
            <Stack direction="row" sx={{ p: 2, gap: 2 }}>
              <Radio
                key="tts-speaker-type-idx"
                value="idx"
                disabled={!enableTts}
              />
              <Stack sx={{ flex: 1, gap: 2 }}>
                <FormLabel>{t('Builtin Voice')}</FormLabel>
                <CommonBox sx={{ flex: 1 }}>
                  <Select
                    placeholder={
                      <Typography fontSize="small" textColor="gray">
                        {t('Select speaker')}
                      </Typography>
                    }
                    renderValue={(selected) => (
                      <Typography fontSize="small">
                        {selected?.value}
                      </Typography>
                    )}
                    sx={{ flex: 1, maxWidth: '12rem' }}
                    value={ttsSpeakerIdx}
                    onChange={(_, newValue) => setTtsSpeakerIdx(newValue ?? '')}
                    disabled={!enableTts || ttsSpeakerType !== 'idx'}
                  >
                    {ttsBuiltinSpeaders.map((speaker) => (
                      <Option value={speaker} key={`tts-speaker-${speaker}`}>
                        <Typography fontSize="small">{speaker}</Typography>
                      </Option>
                    ))}
                  </Select>
                  <IconButton
                    disabled={
                      fileUploading || !enableTts || ttsSpeakerType !== 'idx'
                    }
                    size="sm"
                    onClick={handlePlay}
                    sx={{
                      display:
                        playing && ttsSpeakerType === 'idx' ? 'none' : 'flex',
                      ml: 'auto',
                    }}
                  >
                    <PlayCircleFilledRounded />
                  </IconButton>
                  <Box
                    sx={{
                      display:
                        playing && ttsSpeakerType === 'idx' ? 'flex' : 'none',
                      ml: 'auto',
                    }}
                  >
                    <CircularProgress size="sm" />
                  </Box>
                  <audio ref={audioRefs.current[1]} />
                </CommonBox>
              </Stack>
            </Stack>
            <Stack direction="row" sx={{ p: 2, gap: 2 }}>
              <Radio
                key="tts-speaker-type-wav"
                value="wav"
                disabled={!enableTts}
                sx={{ mt: 1 }}
              />
              <Stack sx={{ flex: 1, gap: 2 }}>
                <CommonBox>
                  <FormLabel sx={{ alignSelf: 'center' }}>
                    {t('Custom Voice')}
                  </FormLabel>
                  <OptionTooltip
                    title={t('MP3 format, about 5 seconds, size less than 1M.')}
                  >
                    <HelpIcon />
                  </OptionTooltip>
                  <Input
                    disabled={
                      fileUploading || !enableTts || ttsSpeakerType !== 'wav'
                    }
                    type="file"
                    id={fileInputId}
                    onChange={handleFileChange}
                    sx={{ display: 'none' }}
                    slotProps={{
                      input: {
                        ref: fileInputRef,
                        accept: 'audio/mp3, .mp3',
                      },
                    }}
                  />
                  <label htmlFor={fileInputId}>
                    <IconButton
                      onClick={handleFileModify}
                      disabled={
                        fileUploading || !enableTts || ttsSpeakerType !== 'wav'
                      }
                    >
                      <AudioFileRounded />
                    </IconButton>
                  </label>
                  <FormHelperText
                    sx={{ display: fileInvalid ? 'unset' : 'none' }}
                  >
                    {t('File too large!')}
                  </FormHelperText>
                </CommonBox>
                <CommonBox sx={{ flex: 1, flexWrap: 'nowrap' }}>
                  {ttsSpeakerWav && (
                    <Fragment>
                      <Chip
                        disabled={!enableTts || ttsSpeakerType !== 'wav'}
                        variant="outlined"
                        color="success"
                        endDecorator={
                          <ChipDelete onDelete={handleFileDelete} />
                        }
                      >
                        <Typography
                          sx={{
                            whiteSpace: 'nowrap',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            maxWidth: '10rem',
                          }}
                        >
                          {ttsSpeakerWav}
                        </Typography>
                      </Chip>
                      <IconButton
                        disabled={
                          fileUploading ||
                          !enableTts ||
                          ttsSpeakerType !== 'wav'
                        }
                        size="sm"
                        onClick={handlePlay}
                        sx={{
                          display:
                            playing && ttsSpeakerType === 'wav'
                              ? 'none'
                              : 'flex',
                          ml: 'auto',
                        }}
                      >
                        <PlayCircleFilledRounded />
                      </IconButton>
                      <Box
                        sx={{
                          display:
                            playing && ttsSpeakerType === 'wav'
                              ? 'flex'
                              : 'none',
                          ml: 'auto',
                        }}
                      >
                        <CircularProgress size="sm" />
                      </Box>
                      <audio ref={audioRefs.current[0]} />
                    </Fragment>
                  )}
                </CommonBox>
              </Stack>
            </Stack>
          </RadioGroup>
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Enable Album Tool')}
            </Typography>
            <OptionTooltip
              title={t(
                'If the user wants to see photos during a chat, select one from the album to display.'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <Switch
              checked={enableAlbumTool}
              sx={{ ml: 'auto' }}
              onChange={() => setEnableAlbumTool(!enableAlbumTool)}
            />
          </CommonContainer>
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Moderation Model')}
            </Typography>
            <OptionTooltip title={t('Set up the moderation model.')}>
              <IconButton disabled>
                <TuneRounded />
              </IconButton>
            </OptionTooltip>
            <Switch checked={false} disabled sx={{ ml: 'auto' }} />
          </CommonContainer>
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Prompt Template')}
            </Typography>
            <OptionTooltip title={t('Adjust prompt words.')}>
              <IconButton onClick={() => handleSave(true, '#system')}>
                <ArrowOutwardRounded />
              </IconButton>
            </OptionTooltip>
          </CommonContainer>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Preset Memory')}
            </Typography>
            <OptionTooltip title={t('Adjust preset memory.')}>
              <IconButton onClick={() => handleSave(true, '#messages')}>
                <ArrowOutwardRounded />
              </IconButton>
            </OptionTooltip>
          </CommonContainer>
        </OptionCard>

        <LinePlaceholder />

        <CommonBox sx={{ justifyContent: 'flex-end ' }}>
          <IconButton onClick={() => onCancel?.()}>
            <UndoRounded />
          </IconButton>
          <IconButton onClick={() => handleSave(false)}>
            <DoneRounded />
          </IconButton>
        </CommonBox>
      </Card>

      <AiApiKeySettings
        defaultKeyName={apiKeyName}
        defaultKeyValue={apiKeyValue}
        keyNames={apiKeyNames}
        open={openApiKeySetting}
        onClose={() => setOpenApiKeySetting(false)}
        onConfirm={(keyName, keyValue) => {
          setApiKeyName(keyName ?? '');
          setApiKeyValue(keyValue ?? '');
          setOpenApiKeySetting(false);
        }}
      />
      <OpenAiSettings
        open={modelSetting && provider === 'open_ai'}
        models={matchingModels}
        onClose={handleModelSettings}
        defaultParameters={parameters}
      />
      <AzureOpenAiSettings
        open={modelSetting && provider === 'azure_open_ai'}
        models={matchingModels}
        onClose={handleModelSettings}
        defaultParameters={parameters}
      />
      <DashScopeSettings
        open={modelSetting && provider === 'dash_scope'}
        models={matchingModels}
        onClose={handleModelSettings}
        defaultParameters={parameters}
      />
      <OllamaSettings
        open={modelSetting && provider === 'ollama'}
        onClose={handleModelSettings}
        defaultParameters={parameters}
      />
    </Fragment>
  );
});

export default CharacterBackendSettings;
