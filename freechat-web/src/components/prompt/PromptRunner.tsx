/* eslint-disable @typescript-eslint/no-explicit-any */
import { Fragment, useCallback, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  Box,
  FormLabel,
  Select,
  MenuItem,
  Typography,
  Button,
  IconButton,
  Chip,
  SelectChangeEvent,
  FormControl,
  InputLabel,
} from '@mui/material';
import {
  AttachmentRounded,
  IosShareRounded,
  KeyRounded,
  PlayCircleFilledRounded,
  ReplayCircleFilledRounded,
  TuneRounded,
} from '@mui/icons-material';
import {
  DynamicFlexBox,
  LinePlaceholder,
  TextareaTypography,
  ChatContent,
  ImagePicker,
  FlexBox,
  TinyInput,
  OptionTooltip,
  StyledStack,
} from '..';
import {
  AiApiKeySettings,
  AzureOpenAiSettings,
  DashScopeSettings,
  OllamaSettings,
  OpenAiSettings,
} from '.';
import {
  defaultBaseURLs,
  defaultModels,
  providers as modelProviders,
} from '../../configs/model-providers-config';
import {
  PromptAiParamDTO,
  PromptDetailsDTO,
  PromptTemplateDTO,
  LlmResultDTO,
} from 'freechat-sdk';
import {
  enabledApiKey,
  extractModelProvider,
  extractVariables,
} from '../../libs/template_utils';
import { HelpIcon } from '../icon';
import { getCompressedImage } from '../../libs/ui_utils';

type PromptRunnerProps = {
  apiPath: string;
  record: PromptDetailsDTO | undefined;
  defaultVariables?: { [key: string]: any };
  defaultParameters?: { [key: string]: any };
  defaultOutputText?: string;
  minWidth?: string;
  maxWidth?: string;
  onPlayFailure?: (request: PromptAiParamDTO | undefined, error: any) => void;
  onPlaySuccess?: (
    request: PromptAiParamDTO | undefined,
    response: LlmResultDTO | undefined
  ) => void;
  onExampleSave?: (
    request: PromptAiParamDTO | undefined,
    response: LlmResultDTO | undefined
  ) => void;
};

export default function PromptRunner(props: PromptRunnerProps) {
  const {
    apiPath,
    record,
    defaultVariables,
    defaultParameters,
    defaultOutputText,
    minWidth,
    maxWidth,
    onPlayFailure,
    onPlaySuccess,
    onExampleSave,
  } = props;
  const { t } = useTranslation();
  const { aiServiceApi, serverUrl } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [provider, setProvider] = useState<string>(
    extractModelProvider(defaultParameters?.modelId) ?? 'open_ai'
  );
  const [inputs, setInputs] = useState(
    defaultVariables ?? extractVariables(record)
  );
  const [attachment, setAttachment] = useState<string>();
  const [openApiKeySetting, setOpenApiKeySetting] = useState(false);
  const [apiKeyName, setApiKeyName] = useState<string | undefined>(
    defaultParameters?.apiKeyName
  );
  const [apiKeyValue, setApiKeyValue] = useState<string | undefined>(
    defaultParameters?.apiKey
  );
  const [apiKeyNames, setApiKeyNames] = useState<(string | undefined)[]>([]);
  const [modelSetting, setModelSetting] = useState(false);
  const [aiRequest, setAiRequest] = useState<PromptAiParamDTO>();
  const [playing, setPlaying] = useState(false);
  const [parameters, setParameters] = useState(defaultParameters);

  const output = useRef<LlmResultDTO>(undefined);

  const [width, setWidth] = useState(minWidth || '30%');

  const initParameters = useCallback(
    (
      initialParameters: { [key: string]: any } | undefined,
      initialProvider: string
    ): { [key: string]: any } | undefined => {
      if (
        extractModelProvider(initialParameters?.modelId) === initialProvider
      ) {
        return { ...initialParameters };
      } else {
        const initialParameters: { [key: string]: any } = {};
        initialParameters['modelId'] =
          defaultModels?.[
            (initialProvider ?? 'open_ai') as keyof typeof defaultModels
          ] ?? defaultModels.open_ai;
        initialParameters['baseUrl'] =
          defaultBaseURLs?.[
            (initialProvider ?? 'open_ai') as keyof typeof defaultBaseURLs
          ] ?? defaultBaseURLs.open_ai;
        return initialParameters;
      }
    },
    []
  );

  useEffect(() => {
    aiServiceApi
      ?.listAiApiKeys(provider)
      .then((resp) =>
        setApiKeyNames(
          resp.filter((key) => !!key.name && key.enabled).map((key) => key.name)
        )
      )
      .catch(handleError);
  }, [aiServiceApi, handleError, provider]);

  useEffect(() => {
    setProvider(extractModelProvider(defaultParameters?.modelId) ?? 'open_ai');
    setApiKeyName(defaultParameters?.apiKeyName);
    setApiKeyValue(defaultParameters?.apiKey);

    const initialProvider =
      extractModelProvider(defaultParameters?.modelId) ?? 'open_ai';

    const initialParameters = initParameters(
      defaultParameters,
      initialProvider
    );

    if (initialParameters) {
      setParameters(initialParameters);
    }
  }, [defaultParameters, initParameters]);

  useEffect(() => {
    setInputs({ ...defaultVariables });
  }, [defaultVariables]);

  useEffect(() => {
    setWidth(maxWidth || '50%');
  }, [maxWidth]);

  function handleInputChange(key: string, value: string | undefined): void {
    if (inputs && value !== inputs[key]) {
      const newInputs: { [key: string]: any } = {};
      Object.entries(inputs)
        .filter(([k]) => k !== key)
        .forEach(([k, v]) => (newInputs[k] = v));
      newInputs[key] = value;
      setInputs(newInputs);
    }
  }

  function handleSelectChange(
    _event: React.SyntheticEvent | null,
    newValue: string | null
  ): void {
    if (newValue && newValue !== provider) {
      setProvider(newValue);
    }
  }

  function handleModelSettings(parameters: { [key: string]: any }) {
    setParameters(parameters);
    setModelSetting(false);
  }

  function handleImageSelect(file: Blob, name: string) {
    getCompressedImage(file)
      .then((imageInfo) => {
        handleInputChange('attachment', imageInfo.dataUrl);
        setAttachment(name);
      })
      .catch(handleError);
  }

  function handleImageDelete() {
    handleInputChange('attachment', undefined);
    setAttachment(undefined);
  }

  function handlePlay() {
    if (!record) {
      return;
    }

    const request = new PromptAiParamDTO();

    const promptTemplate = new PromptTemplateDTO();
    promptTemplate.format = record.format;
    promptTemplate.template = record.template;
    promptTemplate.chatTemplate = record.chatTemplate;
    promptTemplate.variables = inputs;

    request.promptTemplate = promptTemplate;

    request.params = parameters as typeof request.params;
    if (enabledApiKey(provider)) {
      if (apiKeyValue) {
        request.params['apiKey'] = apiKeyValue;
      } else if (apiKeyName) {
        request.params['apiKeyName'] = apiKeyName;
      }
    }

    setPlaying(true);
    setAiRequest(request);
  }

  function isPlayable(): boolean {
    return (
      !!parameters &&
      (!inputs || !Object.keys(inputs).includes('input') || !!inputs.input)
    );
  }

  function getServiceUrl(): string {
    return serverUrl + apiPath;
  }

  return (
    <Fragment>
      <StyledStack
        sx={{
          width: { xs: '100%', sm: width },
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'flex-start',
          transition: 'width 0.3s',
        }}
      >
        <Typography variant="subtitle1" color="text.secondary">
          {t('Inputs')}
        </Typography>
        {inputs && Object.keys(inputs).length > 0 && (
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: 'auto 1fr',
              rowGap: 1,
              columnGap: 2,
              alignItems: 'center',
            }}
          >
            {Object.entries(inputs)
              .filter(([k]) => k !== 'attachment')
              .sort(([a], [b]) => a.localeCompare(b))
              .map(([k, v]) => (
                <Fragment key={`input-${k}`}>
                  <FormLabel>{k}</FormLabel>
                  <TinyInput
                    name={`input-${k}`}
                    value={v}
                    required={k === 'input'}
                    error={k === 'input' && !v}
                    multiline
                    fullWidth
                    onChange={(event) =>
                      handleInputChange(k, event.target.value)
                    }
                    slotProps={{
                      input: {
                        size: 'small',
                      },
                    }}
                    sx={{
                      maxWidth: undefined,
                    }}
                  />
                </Fragment>
              ))}
            <FormLabel>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Typography variant="body1">{t('attachment')}</Typography>
                <OptionTooltip
                  title={t('Only image attachment is supported for now.')}
                >
                  <HelpIcon />
                </OptionTooltip>
              </Box>
            </FormLabel>
            <FlexBox>
              <ImagePicker
                onImageSelect={handleImageSelect}
                aria-label="upload attachment"
                size="small"
                color="primary"
                Icon={AttachmentRounded}
              />
              {attachment && (
                <Chip
                  variant="outlined"
                  color="success"
                  onDelete={handleImageDelete}
                  label={attachment}
                />
              )}
            </FlexBox>
          </Box>
        )}
        <LinePlaceholder />
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'row',
            justifyContent: 'flex-end',
            alignItems: { xs: 'flex-start', sm: 'center' },
            gap: 2,
          }}
        >
          <FormControl sx={{ flex: 1 }} size="small">
            <InputLabel id="model-providers-label">
              {t('Select model providers')}
            </InputLabel>
            <Select
              label={t('Select model providers')}
              value={provider}
              onChange={(event: SelectChangeEvent) =>
                handleSelectChange(null, event.target.value)
              }
              displayEmpty
            >
              {modelProviders.map((p) => (
                <MenuItem value={p.provider} key={`options-${p.provider}`}>
                  {p.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <Button
            variant="outlined"
            color={apiKeyName || apiKeyValue ? 'inherit' : 'error'}
            disabled={!enabledApiKey(provider)}
            startIcon={<KeyRounded />}
            onClick={() => setOpenApiKeySetting(true)}
          >
            {t('API Key')}
          </Button>
          <Button
            variant="outlined"
            color={parameters?.modelId ? 'primary' : 'error'}
            disabled={!provider}
            startIcon={<TuneRounded />}
            onClick={() => setModelSetting(true)}
          >
            {t('button:Tune')}
          </Button>
        </Box>
        <LinePlaceholder />
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            mb: -1.5,
          }}
        >
          <Typography variant="subtitle1" color="textSecondary">
            {t('Output')}
          </Typography>
          <IconButton
            disabled={!isPlayable()}
            loading={playing}
            color="primary"
            onClick={handlePlay}
          >
            {output.current || defaultOutputText ? (
              <ReplayCircleFilledRounded fontSize="large" />
            ) : (
              <PlayCircleFilledRounded fontSize="large" />
            )}
          </IconButton>
        </Box>
        <TextareaTypography component="span">
          <ChatContent
            disabled={!playing}
            url={getServiceUrl()}
            body={JSON.stringify(aiRequest)}
            initialData={defaultOutputText}
            onFinish={(result) => {
              output.current = result;
              onPlaySuccess?.(aiRequest, result);
              setPlaying(false);
            }}
            onError={(error) => {
              onPlayFailure?.(aiRequest, error);
              setPlaying(false);
              setAiRequest(undefined);
            }}
          />
        </TextareaTypography>
        <DynamicFlexBox
          sx={{
            justifyContent: 'flex-end',
          }}
        >
          {onExampleSave && output.current && aiRequest && (
            <Button
              size="small"
              variant="outlined"
              color="success"
              startIcon={<IosShareRounded />}
              onClick={() => onExampleSave(aiRequest, output.current)}
            >
              {t('Save as Example', { ns: 'button' })}
            </Button>
          )}
        </DynamicFlexBox>
      </StyledStack>
      <AiApiKeySettings
        defaultKeyName={apiKeyName}
        defaultKeyValue={apiKeyValue}
        keyNames={apiKeyNames}
        open={openApiKeySetting}
        onClose={() => setOpenApiKeySetting(false)}
        onConfirm={(keyName, keyValue) => {
          setApiKeyName(keyName);
          setApiKeyValue(keyValue);
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
}
