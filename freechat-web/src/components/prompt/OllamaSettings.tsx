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
import {
  toModelInfo,
  extractModelName,
  extractModelProvider,
} from '../../libs/template_utils';

function containsKey(
  parameters: { [key: string]: any } | undefined,
  key: string
): boolean {
  return parameters !== undefined && Object.keys(parameters).includes(key);
}

export default function OllamaSettings(props: {
  open: boolean;
  onClose: (parameters: { [key: string]: any }) => void;
  defaultParameters?: { [key: string]: any };
}) {
  const { open, onClose, defaultParameters } = props;

  const { t } = useTranslation(['prompt']);

  const [baseUrl, setBaseUrl] = useState(
    extractModelProvider(defaultParameters?.modelId) === 'ollama'
      ? (defaultParameters?.baseUrl ?? defaultBaseURLs.ollama)
      : defaultBaseURLs.ollama
  );
  const [model, setModel] = useState<string>(
    extractModelProvider(defaultParameters?.modelId) === 'ollama'
      ? (defaultParameters?.modelId ?? defaultModels.ollama)
      : defaultModels.ollama
  );

  const [temperature, setTemperature] = useState<number>(
    defaultParameters?.temperature ?? 0.8
  );
  const [enableTemperature, setEnableTemperature] = useState(
    containsKey(defaultParameters, 'temperature')
  );

  const [topK, setTopK] = useState<number>(defaultParameters?.topK ?? 40);
  const [enableTopK, setEnableTopK] = useState(
    containsKey(defaultParameters, 'topK')
  );

  const [topP, setTopP] = useState<number>(defaultParameters?.topP ?? 0.9);
  const [enableTopP, setEnableTopP] = useState(
    containsKey(defaultParameters, 'topP')
  );

  const [repeatPenalty, setRepeatPenalty] = useState<number>(
    defaultParameters?.repeatPenalty ?? 1.1
  );
  const [enableRepeatPenalty, setEnableRepeatPenalty] = useState(
    containsKey(defaultParameters, 'repeatPenalty')
  );

  const [seed, setSeed] = useState<number>(defaultParameters?.seed ?? 0);
  const [enableSeed, setEnableSeed] = useState(
    containsKey(defaultParameters, 'seed')
  );

  const [numPredict, setNumPredict] = useState<number>(
    defaultParameters?.numPredict ?? 128
  );
  const [enableNumPredict, setEnableNumPredict] = useState(
    containsKey(defaultParameters, 'numPredict')
  );

  const [numCtx, setNumCtx] = useState<number>(
    defaultParameters?.numCtx ?? 2048
  );
  const [enableNumCtx, setEnableNumCtx] = useState(
    containsKey(defaultParameters, 'numCtx')
  );

  const [stop, setStop] = useState<string[]>(defaultParameters?.stop ?? []);
  const [enableStop, setEnableStop] = useState(
    containsKey(defaultParameters, 'stop')
  );
  const [stopWord, setStopWord] = useState<string>();

  const inputRefs = useRef(Array(7).fill(createRef<HTMLInputElement | null>()));

  useEffect(() => {
    setBaseUrl(
      extractModelProvider(defaultParameters?.modelId) === 'ollama'
        ? (defaultParameters?.baseUrl ?? defaultBaseURLs.ollama)
        : defaultBaseURLs.ollama
    );
    setModel(
      extractModelProvider(defaultParameters?.modelId) === 'ollama'
        ? (defaultParameters?.modelId ?? defaultModels.ollama)
        : defaultModels.ollama
    );

    setTemperature(defaultParameters?.temperature ?? 0.8);
    setEnableTemperature(containsKey(defaultParameters, 'temperature'));

    setTopK(defaultParameters?.topK ?? 40);
    setEnableTopK(containsKey(defaultParameters, 'topK'));

    setTopP(defaultParameters?.topP ?? 0.9);
    setEnableTopP(containsKey(defaultParameters, 'topP'));

    setRepeatPenalty(defaultParameters?.repeatPenalty ?? 1.1);
    setEnableRepeatPenalty(containsKey(defaultParameters, 'repeatPenalty'));

    setSeed(defaultParameters?.seed ?? 0);
    setEnableSeed(containsKey(defaultParameters, 'seed'));

    setNumPredict(defaultParameters?.numPredict ?? 128);
    setEnableNumPredict(containsKey(defaultParameters, 'numPredict'));

    setNumCtx(defaultParameters?.numCtx ?? 2048);
    setEnableNumCtx(containsKey(defaultParameters, 'numCtx'));

    setStop(defaultParameters?.stop ?? []);
    setEnableStop(containsKey(defaultParameters, 'stop'));
  }, [defaultParameters]);

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

    if (enableTemperature) {
      parameters['temperature'] = temperature;
    }

    if (enableTopK) {
      parameters['topK'] = topK;
    }

    if (enableTopP) {
      parameters['topP'] = topP;
    }

    if (enableRepeatPenalty) {
      parameters['repeatPenalty'] = repeatPenalty;
    }

    if (enableSeed) {
      parameters['seed'] = seed;
    }

    if (enableNumPredict) {
      parameters['numPredict'] = numPredict;
    }

    if (enableNumCtx) {
      parameters['numCtx'] = numCtx;
    }

    if (enableStop) {
      parameters['stop'] = stop;
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
              size="small"
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
                setModel(toModelInfo('ollama', event.target.value, 'text2chat'))
              }
            />
          </DynamicFlexBox>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>temperature</Typography>
            <OptionTooltip
              title={t(
                'The temperature of the model. Increasing the temperature will make the model answer more creatively. (Default: 0.8)'
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
                    ref: inputRefs.current[0],
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
            <Typography>topK</Typography>
            <OptionTooltip
              title={t(
                'Reduces the probability of generating nonsense. A higher value (e.g. 100) will give more diverse answers, while a lower value (e.g. 10) will be more conservative. (Default: 40)'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enableTopK}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[1],
                    min: 10,
                    max: 100,
                    step: 5,
                  },
                }}
                value={topK}
                onChange={(event) => setTopK(+event.target.value)}
              />
              <Switch
                checked={enableTopK}
                onChange={() => setEnableTopK(!enableTopK)}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
          <Slider
            disabled={!enableTopK}
            value={topK}
            step={5}
            min={10}
            max={100}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => setTopK(newValue as number)}
          />
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>topP</Typography>
            <OptionTooltip
              title={t(
                'Works together with topK. A higher value (e.g., 0.95) will lead to more diverse text, while a lower value (e.g., 0.5) will generate more focused and conservative text. (Default: 0.9)'
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
                    ref: inputRefs.current[2],
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
            <Typography>repeatPenalty</Typography>
            <OptionTooltip
              title={t(
                'Sets how strongly to penalize repetitions. A higher value (e.g., 1.5) will penalize repetitions more strongly, while a lower value (e.g., 0.9) will be more lenient. (Default: 1.1)'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enableRepeatPenalty}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[3],
                    min: 0,
                    max: 2,
                    step: 0.1,
                  },
                }}
                value={repeatPenalty}
                onChange={(event) => setRepeatPenalty(+event.target.value)}
              />
              <Switch
                checked={enableRepeatPenalty}
                onChange={() => setEnableRepeatPenalty(!enableRepeatPenalty)}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
          <Slider
            disabled={!enableRepeatPenalty}
            value={repeatPenalty}
            step={0.1}
            min={0}
            max={2}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) =>
              setRepeatPenalty(newValue as number)
            }
          />
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>seed</Typography>
            <OptionTooltip
              title={t(
                'Sets the random number seed to use for generation. Setting this to a specific number will make the model generate the same text for the same prompt. (Default: 0)'
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
                    ref: inputRefs.current[4],
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
            <Typography>numPredict</Typography>
            <OptionTooltip
              title={t(
                'Maximum number of tokens to predict when generating text. (Default: 128, -1 = infinite generation, -2 = fill context)'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enableNumPredict}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[5],
                    min: -2,
                    step: 1,
                  },
                }}
                value={numPredict}
                onChange={(event) => setNumPredict(+event.target.value)}
              />
              <Switch
                checked={enableNumPredict}
                onChange={() => setEnableNumPredict(!enableNumPredict)}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>numCtx</Typography>
            <OptionTooltip
              title={t(
                'Sets the size of the context window used to generate the next token. (Default: 2048)'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
            <DynamicFlexBox sx={{ ml: 'auto' }}>
              <TinyInput
                disabled={!enableNumCtx}
                type="number"
                slotProps={{
                  htmlInput: {
                    ref: inputRefs.current[6],
                    min: 1028,
                    step: 1028,
                  },
                }}
                value={numCtx}
                onChange={(event) => setNumCtx(+event.target.value)}
              />
              <Switch
                checked={enableNumCtx}
                onChange={() => setEnableNumCtx(!enableNumCtx)}
              />
            </DynamicFlexBox>
          </DynamicFlexBox>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <DynamicFlexBox>
            <Typography>stop</Typography>
            <OptionTooltip
              title={t(
                'Sets the stop sequences to use. When this pattern is encountered the LLM will stop generating text and return. Multiple stop patterns may be set by specifying multiple separate stop parameters in a modelfile.'
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
