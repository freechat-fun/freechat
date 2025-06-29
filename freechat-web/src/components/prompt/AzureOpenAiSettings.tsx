/* eslint-disable @typescript-eslint/no-explicit-any */
import { createRef, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Chip,
  DialogContent,
  DialogTitle,
  Divider,
  IconButton,
  InputAdornment,
  Slider,
  Switch,
  Typography,
} from '@mui/material';
import { AddCircleRounded, TransitEnterexitRounded } from '@mui/icons-material';
import {
  DynamicFlexBox,
  OptionCard,
  OptionTooltip,
  Sidedrawer,
  TinyInput,
} from '..';
import { HelpIcon } from '../icon';
import {
  defaultBaseURLs,
  defaultModels,
} from '../../configs/model-providers-config';
import { useMetaInfoContext } from '../../contexts';
import {
  extractModelName,
  extractModelProvider,
  toModelInfo,
} from '../../libs/template_utils';

function containsKey(
  parameters: { [key: string]: any } | undefined,
  key: string
): boolean {
  return parameters !== undefined && Object.keys(parameters).includes(key);
}

export default function AzureOpenAiSettings(props: {
  open: boolean;
  onClose: (parameters: { [key: string]: any }) => void;
  defaultParameters?: { [key: string]: any };
}) {
  const { open, onClose, defaultParameters } = props;

  const { t } = useTranslation(['prompt']);
  const { username } = useMetaInfoContext();

  const [baseUrl, setBaseUrl] = useState(
    extractModelProvider(defaultParameters?.modelId) === 'azure_open_ai'
      ? (defaultParameters?.baseUrl ?? defaultBaseURLs.azure_open_ai)
      : defaultBaseURLs.azure_open_ai
  );
  const [model, setModel] = useState<string>(
    extractModelProvider(defaultParameters?.modelId) === 'azure_open_ai'
      ? (defaultParameters?.modelId ?? defaultModels.azure_open_ai)
      : defaultModels.azure_open_ai
  );

  const [topP, setTopP] = useState<number>(defaultParameters?.topP ?? 0.8);
  const [enableTopP, setEnableTopP] = useState(
    containsKey(defaultParameters, 'topP')
  );

  const [maxTokens, setMaxTokens] = useState<number>(
    defaultParameters?.maxTokens ?? 2000
  );
  const [enableMaxTokens, setEnableMaxTokens] = useState(
    containsKey(defaultParameters, 'maxTokens')
  );

  const [seed, setSeed] = useState<number>(defaultParameters?.seed ?? 1234);
  const [enableSeed, setEnableSeed] = useState(
    containsKey(defaultParameters, 'seed')
  );

  const [presencePenalty, setPresencePenalty] = useState<number>(
    defaultParameters?.presencePenalty ?? 0
  );
  const [enablePresencePenalty, setEnablePresencePenalty] = useState(
    containsKey(defaultParameters, 'presencePenalty')
  );

  const [frequencyPenalty, setFrequencyPenalty] = useState<number>(
    defaultParameters?.frequencyPenalty ?? 0
  );
  const [enableFrequencyPenalty, setEnableFrequencyPenalty] = useState(
    containsKey(defaultParameters, 'frequencyPenalty')
  );

  const [temperature, setTemperature] = useState<number>(
    defaultParameters?.temperature ?? 1
  );
  const [enableTemperature, setEnableTemperature] = useState(
    containsKey(defaultParameters, 'temperature')
  );

  const [stop, setStop] = useState<string[]>(defaultParameters?.stop ?? []);
  const [enableStop, setEnableStop] = useState(
    containsKey(defaultParameters, 'stop')
  );
  const [stopWord, setStopWord] = useState<string>();

  const inputRefs = useRef(Array(6).fill(createRef<HTMLInputElement | null>()));

  useEffect(() => {
    setBaseUrl(
      extractModelProvider(defaultParameters?.modelId) === 'azure_open_ai'
        ? (defaultParameters?.baseUrl ?? defaultBaseURLs.azure_open_ai)
        : defaultBaseURLs.azure_open_ai
    );
    setModel(
      extractModelProvider(defaultParameters?.modelId) === 'azure_open_ai'
        ? (defaultParameters?.modelId ?? defaultModels.azure_open_ai)
        : defaultModels.azure_open_ai
    );

    setTopP(defaultParameters?.topP ?? 0.8);
    setEnableTopP(containsKey(defaultParameters, 'topP'));

    setMaxTokens(defaultParameters?.maxTokens ?? 2000);
    setEnableMaxTokens(containsKey(defaultParameters, 'maxTokens'));

    setSeed(defaultParameters?.seed ?? 1234);
    setEnableSeed(containsKey(defaultParameters, 'seed'));

    setPresencePenalty(defaultParameters?.presencePenalty ?? 0);
    setEnablePresencePenalty(containsKey(defaultParameters, 'presencePenalty'));

    setFrequencyPenalty(defaultParameters?.frequencyPenalty ?? 0);
    setEnableFrequencyPenalty(
      containsKey(defaultParameters, 'frequencyPenalty')
    );

    setTemperature(defaultParameters?.temperature ?? 1);
    setEnableTemperature(containsKey(defaultParameters, 'temperature'));

    setStop(defaultParameters?.stop ?? []);
    setEnableStop(containsKey(defaultParameters, 'stop'));
  }, [defaultParameters, model]);

  function handleStopWordSubmit(
    event: React.FormEvent<HTMLFormElement> | undefined
  ): void {
    if (event) {
      event.preventDefault();
    }
    if (stopWord && !stop.includes(stopWord)) {
      setStop([...stop, stopWord]);
    }
    setStopWord(undefined);
  }

  function handleStopWordDelete(word: string): void {
    setStop(stop.filter((stopWord) => stopWord !== word));
  }

  function handleClose(): void {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const parameters: { [key: string]: any } = {};

    if (baseUrl) {
      parameters['baseUrl'] = baseUrl;
    }

    if (model) {
      parameters['modelId'] = model;
    }

    if (enableTopP) {
      parameters['topP'] = topP;
    }

    if (enableMaxTokens) {
      parameters['maxTokens'] = maxTokens;
    }

    if (enableSeed) {
      parameters['seed'] = seed;
    }

    if (enablePresencePenalty) {
      parameters['presencePenalty'] = presencePenalty;
    }

    if (enableFrequencyPenalty) {
      parameters['frequencyPenalty'] = frequencyPenalty;
    }

    if (enableTemperature) {
      parameters['temperature'] = temperature;
    }

    if (enableStop) {
      parameters['stop'] = stop;
    }

    if (username) {
      parameters['user'] = username;
    }

    onClose(parameters);
  }

  return (
    <Sidedrawer open={open} onClose={() => handleClose()}>
      <DialogTitle variant="subtitle1" sx={{ m: 0, p: 0, fontWeight: 'bold' }}>
        {t('Model Parameters')}
      </DialogTitle>
      <Divider sx={{ mt: 0 }} />

      <DialogContent sx={{ p: 0 }}>
        <OptionCard>
          <DynamicFlexBox>
            <Typography>baseUrl</Typography>
            <TinyInput
              type="text"
              value={baseUrl}
              slotProps={{
                input: {
                  size: 'small',
                },
              }}
              sx={{
                ml: 0.5,
                maxWidth: undefined,
                flex: 1,
              }}
              onChange={(event) => setBaseUrl(event.target.value)}
            />
          </DynamicFlexBox>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />
        <OptionCard>
          <DynamicFlexBox>
            <Typography>model</Typography>
            <TinyInput
              type="text"
              value={extractModelName(model)}
              slotProps={{
                input: {
                  size: 'small',
                },
              }}
              sx={{
                ml: 2,
                maxWidth: undefined,
                flex: 1,
              }}
              onChange={(event) =>
                setModel(
                  toModelInfo('azure_open_ai', event.target.value, 'text2chat')
                )
              }
            />
          </DynamicFlexBox>
        </OptionCard>

        <OptionCard>
          <DynamicFlexBox>
            <Typography>topP</Typography>
            <OptionTooltip
              title={t(
                'Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enableTopP}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[0],
                    min: 0,
                    max: 1,
                    step: 0.01,
                  },
                }}
                value={topP}
                onChange={(event) => setTopP(+event.target.value)}
              />
              <Switch
                checked={enableTopP}
                onChange={() => setEnableTopP(!enableTopP)}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
          <Slider
            disabled={!enableTopP}
            value={topP}
            step={0.01}
            min={0}
            max={1}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => setTopP(newValue as number)}
          />
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>maxTokens</Typography>
            <OptionTooltip
              title={t(
                "The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model's context length."
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enableMaxTokens}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[2],
                    min: 1000,
                    step: 1000,
                  },
                }}
                value={maxTokens}
                onChange={(event) => setMaxTokens(+event.target.value)}
              />
              <Switch
                checked={enableMaxTokens}
                onChange={() => setEnableMaxTokens(!enableMaxTokens)}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>seed</Typography>
            <OptionTooltip
              title={t(
                'The random number seed used when generating, the user controls the randomness of the content generated by the model. seed supports unsigned 64-bit integers, with a default value of 1234. When using seed, the model will try its best to generate the same or similar results, but there is currently no guarantee that the results will be exactly the same every time.'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enableSeed}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[3],
                    min: 1,
                    step: 1,
                  },
                }}
                value={seed}
                onChange={(event) => setSeed(+event.target.value)}
              />
              <Switch
                checked={enableSeed}
                onChange={() => setEnableSeed(!enableSeed)}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>presencePenalty</Typography>
            <OptionTooltip
              title={t(
                "Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model's likelihood to talk about new topics."
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enablePresencePenalty}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[4],
                    min: -2,
                    max: 2,
                    step: 0.1,
                  },
                }}
                value={presencePenalty}
                onChange={(event) => setPresencePenalty(+event.target.value)}
              />
              <Switch
                checked={enablePresencePenalty}
                onChange={() => {
                  setEnablePresencePenalty(!enablePresencePenalty);
                }}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
          <Slider
            disabled={!enablePresencePenalty}
            value={presencePenalty}
            step={0.1}
            min={-2}
            max={2}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => {
              setPresencePenalty(newValue as number);
            }}
          />
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>frequencyPenalty</Typography>
            <OptionTooltip
              title={t(
                "Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model's likelihood to repeat the same line verbatim."
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enableFrequencyPenalty}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[4],
                    min: -2,
                    max: 2,
                    step: 0.1,
                  },
                }}
                value={frequencyPenalty}
                onChange={(event) => setFrequencyPenalty(+event.target.value)}
              />
              <Switch
                checked={enableFrequencyPenalty}
                onChange={() => {
                  setEnableFrequencyPenalty(!enableFrequencyPenalty);
                }}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
          <Slider
            disabled={!enableFrequencyPenalty}
            value={frequencyPenalty}
            step={0.1}
            min={-2}
            max={2}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => {
              setFrequencyPenalty(newValue as number);
            }}
          />
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>temperature</Typography>
            <OptionTooltip
              title={t(
                'Used to adjust the degree of randomness from sampling in the generated model, the value range is [0, 2), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enableTemperature}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[5],
                    min: 0,
                    max: 2,
                    step: 0.1,
                  },
                }}
                value={temperature}
                onChange={(event) => setTemperature(+event.target.value)}
              />
              <Switch
                checked={enableTemperature}
                onChange={() => setEnableTemperature(!enableTemperature)}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
          <Slider
            disabled={!enableTemperature}
            value={temperature}
            step={0.1}
            min={0}
            max={2}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => setTemperature(newValue as number)}
          />
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>stop</Typography>
            <OptionTooltip
              title={t(
                'Sequences where the API will stop generating further tokens.'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <Switch
                checked={enableStop}
                onChange={() => setEnableStop(!enableStop)}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
          <Box sx={{ pt: 1 }}>
            {stop &&
              stop.length > 0 &&
              stop.map(
                (word, index) =>
                  word && (
                    <Chip
                      disabled={!enableStop}
                      variant="outlined"
                      key={`${word}-${index}`}
                      onDelete={() => handleStopWordDelete(word)}
                      label={word}
                      sx={{ m: 0.5 }}
                    />
                  )
              )}
            {(!stop || stop.length < 4) && stopWord === undefined && (
              <IconButton
                disabled={!enableStop}
                color="primary"
                onClick={() => setStopWord('')}
              >
                <AddCircleRounded />
              </IconButton>
            )}
            {stopWord !== undefined && (
              <form onSubmit={handleStopWordSubmit}>
                <TinyInput
                  disabled={!enableStop}
                  type="text"
                  value={stopWord}
                  onChange={(event) => setStopWord(event.target.value)}
                  onBlur={() => handleStopWordSubmit(undefined)}
                  slotProps={{
                    input: {
                      endAdornment: (
                        <InputAdornment position="end">
                          <TransitEnterexitRounded fontSize="small" />
                        </InputAdornment>
                      ),
                    },
                  }}
                />
              </form>
            )}
          </Box>
        </OptionCard>
      </DialogContent>
    </Sidedrawer>
  );
}
