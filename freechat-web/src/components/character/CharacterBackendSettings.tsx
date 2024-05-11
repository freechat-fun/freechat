import { Fragment, createRef, forwardRef, useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { Button, Card, CardProps, IconButton, Option, Radio, RadioGroup, Select, Slider, Switch, Typography } from "@mui/joy";
import { ArrowOutwardRounded, DoneRounded, KeyRounded, TuneRounded, UndoRounded } from "@mui/icons-material";
import { AiModelInfoDTO, CharacterBackendDetailsDTO, PromptTaskDTO, PromptTaskDetailsDTO } from "freechat-sdk";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { CommonBox, CommonContainer, CommonGridBox, LinePlaceholder, OptionCard, OptionTooltip, TinyInput } from "..";
import { HelpIcon } from "../icon";
import { extractModelProvider } from "../../libs/template_utils";
import { providers as modelProviders } from "../../configs/model-providers-config";
import { AiApiKeySettings, DashScopeSettings, OpenAISettings } from "../prompt";

type CharacterBackendSettingsProps = CardProps & {
  backend?: CharacterBackendDetailsDTO;
  onSave?: (
    backend: CharacterBackendDetailsDTO,
    promptTask?: PromptTaskDTO,
    redirectToChatPrompt?: boolean,
    redirectHash?: string,
  ) => void;
  onCancel?: () => void;
}

const CharacterBackendSettings = forwardRef<HTMLDivElement, CharacterBackendSettingsProps>((props, ref) => {
  const { t } = useTranslation('character');
  const { aiServiceApi, promptTaskApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const { backend, onSave, onCancel, sx, ...others } = props;

  const [messageWindowSize, setMessageWindowSize] = useState(backend?.messageWindowSize ?? 50);
  const [longTermMemoryWindowSize, setLongTermMemoryWindowSize] = useState(backend?.longTermMemoryWindowSize ?? 5);
  const [initQuota, setInitQuota] = useState(backend?.initQuota ?? 0);
  const [quotaType, setQuotaType] = useState(backend?.quotaType ==='tokens' ? backend?.quotaType : 'messages');
  const [enableQuota, setEnableQuota] = useState(backend?.quotaType === 'messages' || backend?.quotaType === 'tokens');

  const [promptTask, setPromptTask] = useState<PromptTaskDetailsDTO>();
  const [models, setModels] = useState<(AiModelInfoDTO | undefined)[]>([]);
  const [provider, setProvider] = useState('open_ai');
  const [apiKeyName, setApiKeyName] = useState('');
  const [apiKeyValue, setApiKeyValue] = useState('');
  const [apiKeyNames, setApiKeyNames] = useState<(string)[]>([]);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [parameters, setParameters] = useState<{ [key: string]: any; }>({});

  const [modelSetting, setModelSetting] = useState(false);
  const [openApiKeySetting, setOpenApiKeySetting] = useState(false);

  const inputRefs = useRef(Array(3).fill(createRef<HTMLInputElement | null>()));

  const matchingModels = useMemo(() => {
    return provider && models ? models.filter(
      model => model && model.provider === provider && model.type === 'text2chat')
      : [];
  }, [models, provider]);

  const QUOTA_TYPES = [
    {
      label: 'Messages',
      value: 'messages',
    }, {
      label: 'Tokens',
      value: 'tokens',
    }
  ];

  useEffect(() => {
    aiServiceApi?.listAiModelInfo1()
      .then(setModels)
      .catch(handleError);
  }, [aiServiceApi, handleError]);

  useEffect(() => {
    backend?.chatPromptTaskId && promptTaskApi?.getPromptTask(backend?.chatPromptTaskId)
      .then(setPromptTask)
      .catch(handleError);

    setInitQuota(backend?.initQuota ?? 0);
    setQuotaType(backend?.quotaType ==='tokens' ? backend?.quotaType : 'messages');
    setEnableQuota(backend?.quotaType === 'messages' || backend?.quotaType === 'tokens');
  }, [backend?.chatPromptTaskId, backend?.initQuota, backend?.quotaType, handleError, promptTaskApi]);

  useEffect(() => {
    provider && aiServiceApi?.listAiApiKeys(provider)
      .then(resp => setApiKeyNames(resp
        .filter(key => !!key.name && key.enabled)
        .map(key => key.name as string)))
      .catch(handleError);
  }, [aiServiceApi, handleError, provider]);

  useEffect(() => {
    setProvider(extractModelProvider(promptTask?.modelId) ?? 'open_ai');
    setApiKeyName(promptTask?.apiKeyName ?? '');
    setApiKeyValue(promptTask?.apiKeyValue ?? '');
    setParameters({ ...promptTask?.params, modelId: promptTask?.modelId });
  }, [promptTask]);

  function handleModelProviderSelectChange(_event: React.SyntheticEvent | null, newValue: string | null): void {
    if (newValue && newValue !== provider) {
      setProvider(newValue);
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  function handleModelSettings(parameters: { [key: string]: any; }) {
    setParameters(parameters);
    setModelSetting(false);
  }

  function handleSave(redirectToChatPrompt: boolean = false, redirectHash?: string) {
    let editBackend;
    if (backend) {
      editBackend = { ...backend, messageWindowSize, longTermMemoryWindowSize };
    } else {
      editBackend = new CharacterBackendDetailsDTO();
      editBackend.messageWindowSize = messageWindowSize;
      editBackend.longTermMemoryWindowSize = longTermMemoryWindowSize;
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
      <Card ref={ref} sx={{
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
            <OptionTooltip title={t('Set the maximum number of historical messages sent to the model.')}>
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
                onChange={(event => setMessageWindowSize(+event.target.value))} />
            </CommonContainer>
          </CommonContainer>
          <Slider
            value={messageWindowSize}
            min={10}
            max={500}
            step={10}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => setMessageWindowSize(newValue as number)} />
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Long Term Memory Window')}
            </Typography>
            <OptionTooltip title={t('Set the maximum number of long term memory rounds (a round includes a user message and a character reply) sent to the model, 0 to disable. Recalled long-term memory messages will be placed at the top of the historical message window.')}>
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
                onChange={(event => setLongTermMemoryWindowSize(+event.target.value))} />
            </CommonContainer>
          </CommonContainer>
          <Slider
            value={longTermMemoryWindowSize}
            step={5}
            min={0}
            max={30}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => setLongTermMemoryWindowSize(newValue as number)} />
        </OptionCard>

        <OptionCard>
          <CommonContainer>
            <Typography level="title-sm" textColor="neutral">
              {t('Quota Limit')}
            </Typography>
            <OptionTooltip title={t('After reaching the quota limit, users need to use their own API-Key to continue chatting.')}>
              <HelpIcon />
            </OptionTooltip>
            <Switch checked={enableQuota} sx={{ ml: 'auto' }} onChange={() => setEnableQuota(!enableQuota)} />
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
                  label={(<Typography noWrap level="body-xs">{item.label}</Typography>)}
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
                  }} />
              ))}
            </RadioGroup>

            <CommonContainer sx={{ ml: 'auto', justifyContent: 'space-between' }}>
              <TinyInput
                disabled={!enableQuota}
                type="number"
                slotProps={{
                  input: {
                    ref: inputRefs.current[2],
                    min: 0,
                    max: 10000,
                    step: 10,
                  },
                }}
                value={initQuota}
                onChange={(event => setInitQuota(+event.target.value))}
              />
              <Typography sx={{
                display: quotaType === 'tokens' ? 'block' : 'none'
              }}>
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
              placeholder={<Typography textColor="gray">{t('Select model providers')}</Typography>}
              value={provider}
              onChange={handleModelProviderSelectChange}
              sx={{
                flex: 1,
              }}
            >
              {modelProviders.map(p => <Option value={p.provider} key={`options-${p.provider}`}>{p.label}</Option>)}
            </Select>

            <Typography level="title-sm" textColor="neutral">
              {' '}
            </Typography>
            <CommonContainer sx={{
              mt: 2,
              justifyContent: 'space-between',
            }}>
              <Button
                size="sm"
                variant="soft"
                color={apiKeyName || apiKeyValue ? 'neutral' : 'danger'}
                disabled={!provider}
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
          <IconButton onClick={() => onCancel?.()}><UndoRounded /></IconButton>
          <IconButton onClick={() => handleSave(false)}><DoneRounded /></IconButton>
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
        }} />
      <OpenAISettings
        open={modelSetting && provider === 'open_ai'}
        models={matchingModels}
        onClose={handleModelSettings}
        defaultParameters={parameters} />
      <DashScopeSettings
        open={modelSetting && provider === 'dash_scope'}
        models={matchingModels}
        onClose={handleModelSettings}
        defaultParameters={parameters} />
    </Fragment>
  );
});

export default CharacterBackendSettings;
