/* eslint-disable @typescript-eslint/no-explicit-any */
import { Fragment, useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { Box, Card, FormLabel, Select, Option, Textarea, Typography, Button, IconButton, Tooltip, Chip, ChipDelete } from "@mui/joy";
import { AttachmentRounded, IosShareRounded, KeyRounded, PlayCircleFilledRounded, ReplayCircleFilledRounded, TuneRounded } from "@mui/icons-material";
import { CommonContainer, LinePlaceholder, TextareaTypography, ChatContent, ImagePicker, CommonBox } from "../../components"
import { AiApiKeySettings, DashScopeSettings, OpenAISettings } from ".";
import { defaultModels, providers as modelProviders } from "../../configs/model-providers-config";
import { PromptAiParamDTO, PromptDetailsDTO, PromptTemplateDTO, AiModelInfoDTO, LlmResultDTO } from "freechat-sdk";
import { extractModelProvider, extractVariables } from "../../libs/template_utils";
import { HelpIcon } from "../icon";
import { getCompressedImage } from "../../libs/ui_utils";

type PromptRunnerProps = {
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
};

export default function PromptRunner(props: PromptRunnerProps) {
  const { apiPath, record, defaultVariables, defaultParameters, defaultOutputText, minWidth, maxWidth, onPlayFailure, onPlaySuccess, onExampleSave } = props;
  const { t } = useTranslation();
  const { aiServiceApi, serverUrl } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [provider, setProvider] = useState<string | undefined>(extractModelProvider(defaultParameters?.modelId) ?? 'open_ai');
  const [inputs, setInputs] = useState(defaultVariables ?? extractVariables(record));
  const [attachment, setAttachment] = useState<string>();
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

  const matchingModels = useMemo(() => {
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
  }, [models, provider, record]);

  const initParameters = useCallback((
    initialParameters: { [key: string]: any } | undefined,
    initialProvider: string,
    initialModels: (AiModelInfoDTO | undefined)[]
  ): { [key: string]: any } | undefined => {
    if (extractModelProvider(initialParameters?.modelId) === initialProvider) {
      return {...initialParameters};
    } else {
      const defaultModelName = initialProvider === 'dash_scope' ? defaultModels.dash_scope : defaultModels.open_ai;
      const defaultModel = initialModels?.find(modelInfo => modelInfo?.modelId === defaultModelName);
      if (defaultModel) {
        const initialParameters: { [key: string]: any } = {};
        initialParameters['modelId'] = defaultModel.modelId;
        if (initialProvider === 'open_ai') {
          initialParameters['baseUrl'] = 'https://api.openai.com/v1';
        }
        return initialParameters;
      }
    }
    return undefined;
  }, []);

  useEffect(() => {
    aiServiceApi?.listAiModelInfo1()
      .then(setModels)
      .catch(handleError);
  }, [aiServiceApi, handleError]);

  useEffect(() => {
    provider && aiServiceApi?.listAiApiKeys(provider)
      .then(resp => setApiKeyNames(resp
        .filter(key => !!key.name && key.enabled)
        .map(key => key.name)))
      .catch(handleError);
  }, [aiServiceApi, handleError, provider]);

  useEffect(() => {
    setProvider(extractModelProvider(defaultParameters?.modelId) ?? 'open_ai');
    setApiKeyName(defaultParameters?.apiKeyName);
    setApiKeyValue(defaultParameters?.apiKey);

    const initialProvider = extractModelProvider(defaultParameters?.modelId) ?? 'open_ai';
    
    const initialParameters = initParameters(defaultParameters, initialProvider, models);

    if (initialParameters) {
      setParameters(initialParameters);
    }
  }, [defaultParameters, initParameters, models]);

  useEffect(() => {
    setInputs({...defaultVariables});
  }, [defaultVariables]);

  useEffect(() => {
    setWidth(maxWidth || '50%');
  }, [maxWidth]);

  function handleInputChange(key: string, value: string | undefined): void {
    if (inputs && value !== inputs[key]) {
      const newInputs: { [key: string]: any } = {};
      Object.entries(inputs).filter(([k]) => k !== key).forEach(([k, v]) => newInputs[k] = v);
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

  function handleImageSelect(file: Blob, name: string) {
    getCompressedImage(file)
      .then(imageInfo => {
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
    if (apiKeyValue) {
      request.params['apiKey'] = apiKeyValue;
    } else if (apiKeyName) {
      request.params['apiKeyName'] = apiKeyName;
    }

    setPlaying(true);
    setAiRequest(request);
  }

  function isPlayable(): boolean {
    return !!parameters && (
      !inputs ||
      !Object.keys(inputs).includes('input') ||
      !!inputs.input);
  }

  function getServiceUrl(): string {
    return serverUrl + apiPath;
  }

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
            {Object.entries(inputs)
              .filter(([k]) => k !== 'attachment')
              .sort(([a], [b]) => a.localeCompare(b))
              .map(([k, v]) => (
                <Fragment key={`input-${k}`}>
                  <FormLabel>{k}</FormLabel>
                  <Textarea
                    name={`input-${k}`}
                    value={v}
                    required={k === 'input'}
                    color={(k === 'input' && !v) ? 'danger' : "neutral"}
                    onChange={(event) => handleInputChange(k, event.target.value)}
                  />
                </Fragment>
            ))}
            <FormLabel>
              <Typography endDecorator={(
                <Tooltip title={t('Only image attachment is supported for now.')}>
                  <HelpIcon />
                </Tooltip>
              )}>
                {t('attachment')}
              </Typography>
            </FormLabel>
            <CommonBox>
              <ImagePicker
                onImageSelect={handleImageSelect}
                aria-label="upload attachment"
                size="sm"
                color="neutral"
                Icon={AttachmentRounded}
              />
              {attachment && (
                <Chip
                  variant="outlined"
                  color="success"
                  endDecorator={<ChipDelete onDelete={handleImageDelete} />}
                >
                  {attachment}
                </Chip>
              )}
            </CommonBox>
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
            color={apiKeyName || apiKeyValue ? 'neutral' : 'danger'}
            disabled={!provider}
            startDecorator={<KeyRounded />}
            onClick={() => setOpenApiKeySetting(true)}
          >
            {t('API Key')}
          </Button>
          <Button
            variant="soft"
            color={parameters?.modelId ? 'primary' : 'danger'}
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
              disabled={!isPlayable()}
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
            onFinish={result => {
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
        <CommonContainer sx={{
          justifyContent: 'flex-end',
        }}>
          {onExampleSave && output.current && aiRequest && (
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
        </CommonContainer>
      </Card>
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
      <OpenAISettings
        open={modelSetting && provider === 'open_ai'}
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
    </Fragment>
    
  );
}