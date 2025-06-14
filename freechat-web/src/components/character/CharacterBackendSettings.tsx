/* eslint-disable prettier/prettier */
import {
  Fragment,
  createRef,
  forwardRef,
  useEffect,
  useRef,
  useState,
} from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Button,
  ButtonGroup,
  Chip,
  CircularProgress,
  FormControl,
  FormHelperText,
  FormLabel,
  IconButton,
  InputLabel,
  MenuItem,
  Radio,
  RadioGroup,
  Select,
  SelectChangeEvent,
  Slider,
  StackProps,
  Switch,
  TextField,
  Typography,
} from '@mui/material';
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
  StyledStack,
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

type CharacterBackendSettingsProps = StackProps & {
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
  const [provider, setProvider] = useState('open_ai');
  const [apiKeyName, setApiKeyName] = useState('');
  const [apiKeyValue, setApiKeyValue] = useState('');
  const [apiKeyNames, setApiKeyNames] = useState<string[]>([]);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [parameters, setParameters] = useState<{ [key: string]: any }>({});

  const [modelSetting, setModelSetting] = useState(false);
  const [openApiKeySetting, setOpenApiKeySetting] = useState(false);

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
    ttsServiceApi
      ?.listTtsBuiltinSpeakers()
      .then((resp) => {
        setTtsBuiltinSpeakers(resp);
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
      <StyledStack
        ref={ref}
        sx={{
          '&:hover, &:focus-within': { transform: 'none' },
          ...sx,
        }}
        {...others}
      >
        <OptionCard>
          <CommonContainer>
            <Typography variant="body2">
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
                  htmlInput: {
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
            <Typography variant="body2">
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
                  htmlInput: {
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
            <Typography variant="body2">
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
                  htmlInput: {
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
            <Typography variant="body2">
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
            <ButtonGroup
              disabled={!enableQuota}
              size="small"
              variant="contained"
              sx={{
                border: 0,
                boxShadow: 0,
                borderRadius: 2,
              }}
            >
              {QUOTA_TYPES.map((item) => (
                <Button
                  key={`quota-type-${item.value}`}
                  value={item.value}
                  color="info"
                  onClick={() => setQuotaType(item.value)}
                  size="small"
                  sx={{
                    fontSize: '0.6rem',
                    borderRadius: 2,
                    color:
                      quotaType === item.value ? 'text.default' : 'GrayText',
                    backgroundColor:
                    quotaType === item.value ? 'primary' : 'transparent',
                  }}
                >
                  {item.label}
                </Button>
              ))}
            </ButtonGroup>

            <CommonContainer
              sx={{ ml: 'auto', justifyContent: 'space-between' }}
            >
              <TinyInput
                disabled={!enableQuota}
                type="number"
                slotProps={{
                  htmlInput: {
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
            <Typography variant="body2">
              {t('Model')}
            </Typography>
            <FormControl sx={{ flex: 1 }} size="small">
              <Select
                value={provider}
                onChange={(event: SelectChangeEvent) => 
                  handleModelProviderSelectChange(null, event.target.value)
                }
                displayEmpty
                sx={{ flex: 1 }}
              >
                {modelProviders.map((p) => (
                  <MenuItem value={p.provider} key={`options-${p.provider}`}>
                    {p.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <Typography variant="body2">
              {' '}
            </Typography>
            <CommonContainer
              sx={{
                mt: 2,
                justifyContent: 'space-between',
              }}
            >
              <Button
                size="small"
                variant="outlined"
                color={apiKeyName || apiKeyValue ? 'inherit' : 'error'}
                disabled={!enabledApiKey(provider)}
                startIcon={<KeyRounded />}
                onClick={() => setOpenApiKeySetting(true)}
              >
                {t('API Key')}
              </Button>
              <Button
                size="small"
                variant="outlined"
                color={parameters?.modelId ? 'primary' : 'error'}
                disabled={!provider}
                startIcon={<TuneRounded />}
                onClick={() => setModelSetting(true)}
              >
                {t('button:Tune')}
              </Button>
            </CommonContainer>
          </CommonGridBox>
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography variant="body2">
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
            <CommonGridBox sx={{ gridTemplateColumns: '1fr 5fr auto' }}>
              <Radio value="idx" disabled={!enableTts} />
              <FormControl size="small">
                <InputLabel id="builtin-voice-label">
                  {t('Builtin Voice')}
                </InputLabel>
                <Select
                  label={t('Builtin Voice')}
                  value={ttsSpeakerIdx}
                  onChange={(event) => setTtsSpeakerIdx(event.target.value)}
                  disabled={!enableTts || ttsSpeakerType !== 'idx'}
                  sx={{ flex: 1, maxWidth: '12rem' }}
                >
                  {ttsBuiltinSpeaders.map((speaker) => (
                    <MenuItem value={speaker} key={`tts-speaker-${speaker}`}>
                      <Typography variant="body2">{speaker}</Typography>
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
              {
                playing && ttsSpeakerType === 'idx' ? (
                  <CommonBox>
                    <Box sx={{ ml: 'auto' }}>
                      <CircularProgress size="small" />
                    </Box>
                  </CommonBox>
                ) : (
                  <IconButton
                    disabled={
                      fileUploading || !enableTts || ttsSpeakerType !== 'idx'
                    }
                    size="small"
                    onClick={handlePlay}
                    sx={{  ml: 'auto' }}
                  >
                    <PlayCircleFilledRounded />
                  </IconButton>
                )
              }
              <audio ref={audioRefs.current[1]} />

              <Radio value="wav" disabled={!enableTts} />
              <CommonBox>
                <FormLabel sx={{ alignSelf: 'center' }}>
                  {t('Custom Voice')}
                </FormLabel>
                <OptionTooltip
                  title={t('MP3 format, about 5 seconds, size less than 1M.')}
                >
                  <HelpIcon />
                </OptionTooltip>
              </CommonBox>
              <CommonBox>
                <TextField
                  disabled={
                    fileUploading || !enableTts || ttsSpeakerType !== 'wav'
                  }
                  type="file"
                  id={fileInputId}
                  onChange={handleFileChange}
                  sx={{ display: 'none' }}
                  slotProps={{
                    htmlInput: {
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
            </CommonGridBox>
            <CommonGridBox sx={{ gridTemplateColumns: '1fr 5fr auto' }}>
              <CommonBox sx={{ flex: 1, flexWrap: 'nowrap' }} />
                {ttsSpeakerWav && (
                  <Fragment>
                    <Chip
                      disabled={!enableTts || ttsSpeakerType !== 'wav'}
                      variant="outlined"
                      color="success"
                      onDelete={handleFileDelete}
                      label={
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
                      }
                    />
                    <IconButton
                      disabled={
                        fileUploading ||
                        !enableTts ||
                        ttsSpeakerType !== 'wav'
                      }
                      size="small"
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
                      <CircularProgress size="small" />
                    </Box>
                  </Fragment>
                )}
                <audio ref={audioRefs.current[0]} />
            </CommonGridBox>
          </RadioGroup>
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography variant="body2">
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
            <Typography variant="body2">
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
            <Typography variant="body2">
              {t('Prompt Template')}
            </Typography>
            <OptionTooltip title={t('Adjust prompt words.')}>
              <IconButton onClick={() => handleSave(true, '#system')}>
                <ArrowOutwardRounded />
              </IconButton>
            </OptionTooltip>
          </CommonContainer>
          <CommonContainer>
            <Typography variant="body2">
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

        <CommonBox sx={{ justifyContent: 'flex-end' }}>
          <IconButton onClick={() => onCancel?.()}>
            <UndoRounded />
          </IconButton>
          <IconButton onClick={() => handleSave(false)}>
            <DoneRounded />
          </IconButton>
        </CommonBox>
      </StyledStack>

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
        onClose={handleModelSettings}
        defaultParameters={parameters}
      />
      <AzureOpenAiSettings
        open={modelSetting && provider === 'azure_open_ai'}
        onClose={handleModelSettings}
        defaultParameters={parameters}
      />
      <DashScopeSettings
        open={modelSetting && provider === 'dash_scope'}
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

CharacterBackendSettings.displayName = 'CharacterBackendSettings';

export default CharacterBackendSettings;
