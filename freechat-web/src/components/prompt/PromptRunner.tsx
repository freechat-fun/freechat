/* eslint-disable @typescript-eslint/no-explicit-any */
import { Fragment, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { Box, Card, FormLabel, Select, Option, Textarea, Typography, Button, Input, FormControl, IconButton, SelectStaticProps, Stack, Divider } from "@mui/joy";
import { CheckRounded, CloseRounded, IosShareRounded, KeyRounded, PlayCircleFilledRounded, ReplayCircleFilledRounded, TuneRounded } from "@mui/icons-material";
import { CommonContainer, ConfirmModal, LinePlaceholder, TextareaTypography, ChatContent } from "../../components"
import { DashScopeSettings, OpenAISettings } from ".";
import { providers as modelProviders } from "../../configs/model-providers-config";
import { PromptAiParamDTO, PromptDetailsDTO, PromptTemplateDTO, AiModelInfoDTO, LlmResultDTO } from "freechat-sdk";
import { extractModelProvider, extractVariables } from "../../libs/template_utils";

function AiApiKeySetting(props: {
  defaultKeyName: string | undefined,
  defaultKeyValue: string | undefined,
  keyNames: (string | undefined)[] | undefined,
  open: boolean,
  onClose: () => void,
  onConfirm: (keyName: string | undefined, keyValue: string | undefined) => void;
}) {
  const { defaultKeyName, defaultKeyValue, keyNames, open, onClose, onConfirm } = props;
  const { t } = useTranslation('prompt');

  const [apiKeyName, setApiKeyName] = useState(defaultKeyName ?? '');
  const [apiKeyValue, setApiKeyValue] = useState(defaultKeyValue ?? '');

  const action: SelectStaticProps['action'] = useRef(null);

  function handleClose(_event: React.MouseEvent<HTMLButtonElement>, reason: string): void {
    if (reason === 'backdropClick') {
      return;
    }
    onClose();
  }

  function handleConfirm(): void {
    onConfirm(apiKeyName, apiKeyValue);
  }

  function handleSelectChange(_event: React.SyntheticEvent | null, newValue: string | null): void {
    if (newValue !== apiKeyName && newValue !== 'No API Key') {
      setApiKeyName(newValue ?? '');
    }
  }

  function handleValueChange(event: React.ChangeEvent<HTMLInputElement>): void {
    if (event.target.value !== apiKeyValue) {
      setApiKeyValue(event.target.value);
    }
  }

  return (
    <ConfirmModal
        open={open}
        onClose={handleClose}
        dialog={{
          title: t('Set API Key'),
        }}
        button={{
          text: t('button:Confirm'),
          startDecorator: <CheckRounded />
        }}
        onConfirm={handleConfirm}
      >
        <Stack spacing={2}>
          <FormControl>
            <FormLabel>{t('Select a key')}</FormLabel>
            <Select
              action={action}
              name="apiKeyName"
              placeholder={<Typography textColor="gray">No API Key</Typography>}
              value={apiKeyName || 'No API Key'}
              onChange={handleSelectChange}
              sx={{
                flex: 1,
              }}
              {...(apiKeyName && {
                // display the button and remove select indicator
                // when user has selected a value
                endDecorator: (
                  <IconButton
                    size="sm"
                    variant="plain"
                    color="neutral"
                    onMouseDown={(event) => {
                      // don't open the popup when clicking on this button
                      event.stopPropagation();
                    }}
                    onClick={() => {
                      setApiKeyName('');
                      action.current?.focusVisible();
                    }}
                  >
                    <CloseRounded fontSize="small"/>
                  </IconButton>
                ),
                indicator: null,
              })}
            >
              {keyNames && keyNames.length > 0 ? keyNames?.map(keyName => keyName && (
                <Option value={keyName} key={`option-${keyName}`}>{keyName}</Option>
              )) : (
                <Option value="No API Key" key='option-unknown'>--No API Key--</Option>
              )}
            </Select>
          </FormControl>
          <FormControl>
            <FormLabel>{t('or you can use a temporary key')}</FormLabel>
            <Input
              type="password"
              placeholder="Paste a key here..."
              startDecorator={<KeyRounded />}
              value={apiKeyValue}
              onChange={handleValueChange}
              sx={{
                minWidth: '20rem',
              }}
            />
          </FormControl>
        </Stack>
      </ConfirmModal>
  );
}

export default function PromptRunner(props: {
  apiPath: string,
  record: PromptDetailsDTO | undefined,
  defaultVariables?: { [key: string]: any },
  defaultParameters?: { [key: string]: any },
  defaultOutputText?: string,
  minWidth?: string,
  maxWidth?: string,
  onPlayFailure?: (request: PromptAiParamDTO | undefined, error: any) => void,
  onPlaySuccess?: (request: PromptAiParamDTO | undefined, response: LlmResultDTO | undefined) => void,
  onExampleSave?: (request: PromptAiParamDTO | undefined, response: LlmResultDTO | undefined) => void,
}) {
  const { apiPath, record, defaultVariables, defaultParameters, defaultOutputText, minWidth, maxWidth, onPlayFailure, onPlaySuccess, onExampleSave } = props;
  const { t } = useTranslation();
  const { aiServiceApi, serverUrl } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [provider, setProvider] = useState<string | undefined>(extractModelProvider(defaultParameters?.modelId) ?? 'open_ai');
  const [inputs, setInputs] = useState(defaultVariables ?? extractVariables(record));
  const [openApiKeySetting, setOpenApiKeySetting] = useState(false);
  const [apiKeyName, setApiKeyName] = useState<string | undefined>(defaultParameters?.apiKeyName);
  const [apiKeyValue, setApiKeyValue] = useState<string | undefined>(defaultParameters?.apiKey);
  const [apiKeyNames, setApiKeyNames] = useState<(string | undefined)[]>([]);
  const [modelSetting, setModelSetting] = useState(false);
  const [models, setModels] = useState<(AiModelInfoDTO | undefined)[]>([]);
  const [aiRequest, setAiRequest] = useState<PromptAiParamDTO>();
  const [playing, setPlaying] = useState(false);
  const [parameters, setParameters] = useState(defaultParameters);

  const output = useRef<LlmResultDTO>();

  const [width, setWidth] = useState(minWidth || '30%');

  useEffect(() => {
    aiServiceApi?.listAiModelInfo1()
      .then(setModels)
      .catch(handleError);
    provider && aiServiceApi?.listAiApiKeys(provider)
      .then(resp => setApiKeyNames(resp
        .filter(key => !!key.name && key.enabled)
        .map(key => key.name)))
      .catch(handleError);
  }, [record, provider, aiServiceApi, handleError]);

  useEffect(() => {
    setProvider(extractModelProvider(defaultParameters?.modelId) ?? 'open_ai');
    setApiKeyName(defaultParameters?.apiKeyName);
    setApiKeyValue(defaultParameters?.apiKey);
    const initialParameters = initParameters(extractModelProvider(defaultParameters?.modelId) ?? 'open_ai');
    if (initialParameters) {
      setParameters(initialParameters);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [defaultParameters]);

  useEffect(() => {
    defaultVariables && setInputs(defaultVariables);
  }, [defaultVariables]);

  useEffect(() => {
    setWidth(maxWidth || '50%');
  }, [maxWidth]);

  useEffect(() => {
    const initialParameters = initParameters(provider ?? 'open_ai');
    if (initialParameters) {
      setParameters(initialParameters);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [provider, models]);

  function handleInputChange(key: string, value: string | undefined): void {
    if (inputs && value !== inputs[key]) {
      const newInputs: { [key: string]: any } = {};
      Object.entries(inputs).forEach(([k, v]) => newInputs[k] = v);
      newInputs[key] = value;
      setInputs(newInputs);
    }
  }


  function handleSelectChange(_event: React.SyntheticEvent | null, newValue: string | null): void {
    if (newValue && newValue !== provider) {
      setProvider(newValue);
    }
  }

  function handleModelSettings(parameters: { [key: string]: any }) {
    setParameters(parameters);
    setModelSetting(false);
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
    if (apiKeyValue) {
      request.params['apiKey'] = apiKeyValue;
    } else if (apiKeyName) {
      request.params['apiKeyName'] = apiKeyName;
    }

    setPlaying(true);
    setAiRequest(request);
  }

  function initParameters(initialProvider: string): { [key: string]: any } | undefined {
    if (extractModelProvider(defaultParameters?.modelId) === initialProvider) {
      return {...defaultParameters};
    } else {
      const defaultModelName = initialProvider === 'dash_scope' ? '[dash_scope]qwen-plus' : '[open_ai]gpt-4';
      const defaultModel = models?.find(modelInfo => modelInfo?.modelId === defaultModelName);
      if (defaultModel) {
        const initialParameters: { [key: string]: any } = {};
        initialParameters['modelId'] = defaultModel.modelId;
        if (initialProvider === 'open_ai') {
          initialParameters['baseUrl'] = 'https://api.openai-proxy.com/v1';
        }
        return initialParameters;
      }
    }
    return undefined;
  }

  function filterModels(): (AiModelInfoDTO | undefined)[] {
    return record && provider && models ? models.filter(model => {
      if (!model || model.provider !== provider) {
        return false;
      }
      switch(record.type) {
        case 'chat': {
          return model.type === 'text2chat';
        }
        case 'string': {
          return model.type === 'text2text';
        }
        default: {
          return false;
        }
      }
    }) : [];
  }

  function getServiceUrl(): string {
    return serverUrl + apiPath;
  }

  const sx = {};

  return (
    <Fragment>
      <Card sx={{
        width: { xs: '100%', sm: width },
        mt: 2,
        p: 2,
        boxShadow: 'sm',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'flex-start',
        transition: 'width 0.3s',
        ...sx
      }}>
        <Typography level="title-sm" textColor="neutral">
          {t('Inputs')}
        </Typography>
        {inputs && Object.keys(inputs).length > 0 && (
          <Box sx={{
            display: 'grid',
            gridTemplateColumns: 'auto 1fr',
            gap: 1,
          }}>
            {Object.entries(inputs).map(([k, v]) => (
              <Fragment key={`input-${k}`}>
                <FormLabel>{k}</FormLabel>
                <Textarea
                  name={`input-${k}`}
                  value={v}
                  onChange={(event) => handleInputChange(k, event.target.value)}
                />
              </Fragment>
            ))}
          </Box>
        )}
        <LinePlaceholder />
        <Typography level="title-sm" textColor="neutral">
          {t('Select model providers')}
        </Typography>
        <Box sx={{
          display: 'flex',
          flexDirection: 'row',
          justifyContent: 'flex-end',
          alignItems:  { xs: 'flex-start', sm: 'center' },
          gap: 2,
        }}>
          <Select
            placeholder={<Typography textColor="gray">{t('Select model providers')}</Typography>}
            value={provider}
            onChange={handleSelectChange}
            sx={{
              flex: 1,
            }}
          >
            {modelProviders.map(p => <Option value={p.provider} key={`options-${p.provider}`}>{p.label}</Option>)}
          </Select>
          <Button
            variant="soft"
            color={!apiKeyName && !apiKeyValue ? 'danger' : 'neutral'}
            disabled={!provider}
            startDecorator={<KeyRounded />}
            onClick={() => setOpenApiKeySetting(true)}
          >
            {t('API key')}
          </Button>
          <Button
            variant="soft"
            color={!parameters?.modelId ? 'danger' : 'primary'}
            disabled={!provider}
            startDecorator={<TuneRounded />}
            onClick={() => setModelSetting(true)}
          >
            {t('button:Tune')}
          </Button>
        </Box>
        <LinePlaceholder />
        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
          <Typography level="title-sm" textColor="neutral">
            {t('Output')}
          </Typography>
          {playing ? (
            <Button loading variant="plain" />
          ) : (
            <IconButton
              disabled={!parameters}
              color="primary"
              onClick={handlePlay}
            >
              {output.current || defaultOutputText ? 
                <ReplayCircleFilledRounded fontSize="large" /> : 
                <PlayCircleFilledRounded fontSize="large" />
              }
            </IconButton>
          )}
        </Box>
        <TextareaTypography component="span">
          <ChatContent
            disabled={!playing}
            url={getServiceUrl()}
            body={JSON.stringify(aiRequest)}
            initialData={defaultOutputText}
            onFinish={result => output.current = result}
            onClose={() => {
              onPlaySuccess?.(aiRequest, output.current);
              setPlaying(false);
            }}
            onError={(error) => {
              onPlayFailure?.(aiRequest, error);
              setPlaying(false);
              setAiRequest(undefined);
              handleError(error);
            }}
          />
        </TextareaTypography>
        <CommonContainer sx={{
          justifyContent: onExampleSave ? 'space-between' : 'flex-end',
          alignItems: 'flex-start',
        }}>
          {onExampleSave && output.current && (
            <Button
              size="sm"
              variant="outlined"
              color="success"
              startDecorator={<IosShareRounded />}
              onClick={() => onExampleSave(aiRequest, output.current)}
            >
              {t('Save as Example', {ns: 'button'})}
            </Button>
          )}
          {!playing && output.current?.tokenUsage && (
            <Fragment>
              <Box sx={{
                width: '50%',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'flex-start',
              }}>
                <Typography fontWeight="bold">Token Usage</Typography>
                <Divider sx={{ backgroundColor: 'black' }}/>
                <Box sx={{
                  display: 'grid',
                  gridTemplateColumns: 'auto 1fr',
                }}>
                  <FormLabel>Input: </FormLabel>
                  <Typography sx={{ justifySelf: 'flex-end' }} level="body-xs">{output.current?.tokenUsage.inputTokenCount}</Typography>
                  <FormLabel>Output: </FormLabel>
                  <Typography sx={{ justifySelf: 'flex-end' }} level="body-xs">{output.current?.tokenUsage.outputTokenCount}</Typography>
                  <Box sx={{ gridColumn: '1 / -1' }}>
                    <Divider />
                  </Box>
                  <FormLabel>Total: </FormLabel>
                  <Typography sx={{ justifySelf: 'flex-end' }} level="body-xs">{output.current?.tokenUsage.totalTokenCount}</Typography>
                </Box>
              </Box>
            </Fragment>
          )}
        </CommonContainer>
      </Card>
      <AiApiKeySetting
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
      <OpenAISettings
        open={modelSetting && provider === 'open_ai'}
        models={filterModels()}
        onClose={handleModelSettings}
        defaultParameters={parameters}
      />
      <DashScopeSettings
        open={modelSetting && provider === 'dash_scope'}
        models={filterModels()}
        onClose={handleModelSettings}
        defaultParameters={parameters}
      />
    </Fragment>
    
  );
}