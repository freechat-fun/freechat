import { Fragment, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { Box, Card, FormLabel, Select, Option, Textarea, Typography, Button, Input, FormControl, IconButton, SelectStaticProps, Stack } from "@mui/joy";
import { CheckRounded, CloseRounded, KeyRounded, TuneRounded } from "@mui/icons-material";
import { ConfirmModal, LinePlaceholder } from "../../components"
import { providers as modelProviders } from "../../configs/model-providers-config";
import { PromptDetailsDTO, AiModelInfoDTO } from "freechat-sdk";
import { extractJson } from "../../libs/template_utils";
import { DashScopeSettings } from ".";

function AiApiKeySetting(props: {
  defaultKeyName: string | undefined,
  defaultKeyValue: string | undefined,
  keyNames: (string | undefined)[] | undefined,
  open: boolean,
  onClose: () => void,
  onConfirm: (keyName: string | undefined, keyValue: string | undefined) => void;
}) {
  const { defaultKeyName, defaultKeyValue, keyNames, open, onClose, onConfirm } = props;
  const { t } = useTranslation(['prompt']);

  const [apiKeyName, setApiKeyName] = useState<string | undefined>(defaultKeyName);
  const [apiKeyValue, setApiKeyValue] = useState<string | undefined>(defaultKeyValue);

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
      setApiKeyName(newValue ?? undefined);
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
              value={apiKeyName || (keyNames && keyNames.length > 0) ? keyNames![0] : 'No API Key'}
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
                      setApiKeyName(undefined);
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
  record: PromptDetailsDTO | undefined,
}) {
  const { record } = props;
  const { t } = useTranslation();
  const { aiServiceApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [provider, setProvider] = useState<string | null>();
  const [inputs, setInputs] = useState(record?.inputs ? extractJson(record.inputs) : undefined);
  const [output, setOutput] = useState<string>();
  const [openApiKeySetting, setOpenApiKeySetting] = useState(false);
  const [apiKeyName, setApiKeyName] = useState<string>();
  const [apiKeyValue, setApiKeyValue] = useState<string>();
  const [apiKeyNames, setApiKeyNames] = useState<(string | undefined)[]>([]);
  const [modelSetting, setModelSetting] = useState(false);
  const [models, setModels] = useState<(AiModelInfoDTO | undefined)[]>([]);

  const [width, setWidth] = useState('30%');

  useEffect(() => {
    setInputs(record?.inputs ? extractJson(record.inputs) : undefined);
    setWidth('50%');
    aiServiceApi?.listAiModelInfo1()
      .then(resp => resp && setModels(resp))
      .catch(handleError);
  }, [record, aiServiceApi, handleError]);

  function handleInputChange(key: string, value: string | undefined): void {
    if (inputs && value !== inputs.get(key)) {
      const newInputs = new Map<string, string | undefined>();
      inputs.forEach((v, k) => newInputs.set(k, v));
      newInputs.set(key, value);
      setInputs(newInputs);
    }
  }


  function handleSelectChange(_event: React.SyntheticEvent | null, newValue: string | null): void {
    if (newValue && newValue !== provider) {
      aiServiceApi?.listAiApiKeys(newValue)
        .then(resp => {
          setApiKeyNames(resp
            .filter(key => !!key.name && key.enabled)
            .map(key => key.name));
          setProvider(newValue);
        })
        .catch(handleError);
    }
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
        {inputs && inputs.size > 0 && (
          <Box sx={{
            display: 'grid',
            gridTemplateColumns: 'auto 1fr',
            gap: 1,
          }}>
            {[...inputs].map(([k, v]) => (
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
          flexDirection: { xs: 'column', sm: 'row' },
          justifyContent: 'flex-end',
          alignItems: 'center',
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
            disabled={!provider}
            startDecorator={<TuneRounded />}
            onClick={() => setModelSetting(true)}
          >
            {t('button:Tune')}
          </Button>
        </Box>
        <LinePlaceholder />
        <Typography level="title-sm" textColor="neutral">
          {t('Output')}
        </Typography>
        <Textarea
          readOnly
          minRows={3}
        >
          {output}
        </Textarea>
        <Button disabled={!output} sx={{
          alignSelf: 'flex-end',
        }}>
          {t('Save as Example', {ns: 'button'})}
        </Button>
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
      <DashScopeSettings
        open={modelSetting && provider === 'dash_scope'}
        models={filterModels()}
        onClose={() => setModelSetting(false)}
      />
    </Fragment>
    
  );
}