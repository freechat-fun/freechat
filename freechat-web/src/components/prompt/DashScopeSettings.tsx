/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Chip,
  DialogContent,
  DialogTitle,
  Divider,
  FormControl,
  IconButton,
  MenuItem,
  Select,
  SelectChangeEvent,
  Slider,
  Switch,
  Typography,
} from '@mui/material';
import { AddCircleRounded, TransitEnterexitRounded } from '@mui/icons-material';
import { AiModelInfoDTO } from 'freechat-sdk';
import { defaultModels } from '../../configs/model-providers-config';
import { InputAdornment } from '@mui/material';
import {
  CommonContainer,
  OptionCard,
  OptionTooltip,
  Sidedrawer,
  TinyInput,
} from '..';
import { HelpIcon } from '../icon';

function containsKey(
  parameters: { [key: string]: any } | undefined,
  key: string
): boolean {
  return !!parameters && Object.keys(parameters).includes(key);
}

export default function DashScopeSettings(props: {
  open: boolean;
  models: (AiModelInfoDTO | undefined)[] | undefined;
  onClose: (parameters: { [key: string]: any }) => void;
  defaultParameters?: { [key: string]: any };
}) {
  const { open, models, onClose, defaultParameters } = props;

  const { t } = useTranslation(['prompt']);

  const [model, setModel] = useState<AiModelInfoDTO | undefined>(
    models?.find(
      (modelInfo) =>
        modelInfo?.modelId ===
        (defaultParameters?.modelId ?? defaultModels.dash_scope)
    )
  );

  const [topP, setTopP] = useState<number>(defaultParameters?.topP ?? 0.8);
  const [enableTopP, setEnableTopP] = useState(
    containsKey(defaultParameters, 'topP')
  );

  const [topK, setTopK] = useState<number>(defaultParameters?.topK ?? 0);
  const [enableTopK, setEnableTopK] = useState(
    containsKey(defaultParameters, 'topK')
  );

  const [maxTokens, setMaxTokens] = useState<number>(
    defaultParameters?.maxTokens ?? 2000
  );
  const [enableMaxTokens, setEnableMaxTokens] = useState(
    containsKey(defaultParameters, 'maxTokens')
  );

  const [enableSearch, setEnableSearch] = useState<boolean>(
    defaultParameters?.enableSearch ?? false
  );

  const [seed, setSeed] = useState<number>(defaultParameters?.seed ?? 1234);
  const [enableSeed, setEnableSeed] = useState(
    containsKey(defaultParameters, 'seed')
  );

  const [repetitionPenalty, setRepetitionPenalty] = useState<number>(
    defaultParameters?.repetitionPenalty ?? 1.1
  );
  const [enableRepetitionPenalty, setEnableRepetitionPenalty] = useState(
    containsKey(defaultParameters, 'repetitionPenalty')
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

  const [modelId, setModelId] = useState<string>(
    models?.find(
      (modelInfo) =>
        modelInfo?.modelId ===
        (defaultParameters?.modelId ?? defaultModels.azure_open_ai)
    )?.modelId ?? ''
  );

  useEffect(() => {
    setModel(
      models?.find(
        (modelInfo) =>
          modelInfo?.modelId ===
          (defaultParameters?.modelId ?? defaultModels.dash_scope)
      )
    );

    setTopP(defaultParameters?.topP ?? 0.8);
    setEnableTopP(containsKey(defaultParameters, 'topP'));

    setTopK(defaultParameters?.topK ?? 0);
    setEnableTopK(containsKey(defaultParameters, 'topK'));

    setMaxTokens(defaultParameters?.maxTokens ?? 2000);
    setEnableMaxTokens(containsKey(defaultParameters, 'maxTokens'));

    setEnableSearch(defaultParameters?.enableSearch ?? false);

    setSeed(defaultParameters?.seed ?? 1234);
    setEnableSeed(containsKey(defaultParameters, 'seed'));

    setRepetitionPenalty(defaultParameters?.repetitionPenalty ?? 1.1);
    setEnableRepetitionPenalty(
      containsKey(defaultParameters, 'repetitionPenalty')
    );

    setTemperature(defaultParameters?.temperature ?? 1);
    setEnableTemperature(containsKey(defaultParameters, 'temperature'));

    setStop(defaultParameters?.stop ?? []);
    setEnableStop(containsKey(defaultParameters, 'stop'));

    setModelId(
      models?.find(
        (modelInfo) =>
          modelInfo?.modelId ===
          (defaultParameters?.modelId ?? defaultModels.azure_open_ai)
      )?.modelId ?? ''
    );
  }, [defaultParameters, models]);

  function handleSelectChange(event: SelectChangeEvent<string>): void {
    const newValue = event.target.value;
    if (newValue && newValue !== model?.modelId) {
      setModel(models?.find((modelInfo) => modelInfo?.modelId === newValue));
    }
    setModelId(newValue);
  }

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

    if (model) {
      parameters['modelId'] = model.modelId;
    }

    if (enableTopP) {
      parameters['topP'] = topP;
    }

    if (enableTopK) {
      parameters['topK'] = topK;
    }

    if (enableMaxTokens) {
      parameters['maxTokens'] = maxTokens;
    }

    parameters['enableSearch'] = enableSearch;

    if (enableSeed) {
      parameters['seed'] = seed;
    }

    if (enableRepetitionPenalty) {
      parameters['repetitionPenalty'] = repetitionPenalty;
    }

    if (enableTemperature) {
      parameters['temperature'] = temperature;
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
          <CommonContainer>
            <Typography>model</Typography>
            <FormControl size="small" sx={{ flex: 1 }}>
              <Select
                name="modelName"
                value={modelId}
                onChange={handleSelectChange}
                displayEmpty
                sx={{
                  ml: 2,
                  flex: 1,
                }}
              >
                <MenuItem value="" disabled>
                  <Typography color="text.secondary">
                    No model provided
                  </Typography>
                </MenuItem>
                {models && models.length > 0 ? (
                  models?.map(
                    (modelInfo) =>
                      modelInfo && (
                        <MenuItem
                          value={modelInfo.modelId}
                          key={`option-${modelInfo.modelId}`}
                        >
                          {modelInfo.name}
                        </MenuItem>
                      )
                  )
                ) : (
                  <MenuItem value="" disabled>
                    --No Model--
                  </MenuItem>
                )}
              </Select>
            </FormControl>
          </CommonContainer>
        </OptionCard>
        <Divider sx={{ my: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>topP</Typography>
            <OptionTooltip
              title={t(
                'Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.'
              )}
            >
              <IconButton size="small">
                <HelpIcon />
              </IconButton>
            </OptionTooltip>
            <Box
              sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 1 }}
            >
              <TinyInput
                disabled={!enableTopP}
                type="number"
                slotProps={{
                  htmlInput: {
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
            </Box>
          </CommonContainer>
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
        <Divider sx={{ my: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>topK</Typography>
            <OptionTooltip
              title={t(
                'The size of the sampling candidate set during generation. For example, when the value is 50, only the top 50 tokens with the highest scores in a single generation are included in the random sampling candidate set. The larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. The default value is 0, which means that the top_k strategy is not enabled, and only the top_p strategy is effective.'
              )}
            >
              <IconButton size="small">
                <HelpIcon />
              </IconButton>
            </OptionTooltip>
            <Box
              sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 1 }}
            >
              <TinyInput
                disabled={!enableTopK}
                type="number"
                slotProps={{
                  htmlInput: {
                    min: 0,
                    max: 100,
                    step: 1,
                  },
                }}
                value={topK}
                onChange={(event) => setTopK(+event.target.value)}
              />
              <Switch
                checked={enableTopK}
                onChange={() => setEnableTopK(!enableTopK)}
              />
            </Box>
          </CommonContainer>
          <Slider
            disabled={!enableTopK}
            value={topK}
            step={1}
            min={0}
            max={100}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => setTopK(newValue as number)}
          />
        </OptionCard>
        <Divider sx={{ my: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>maxTokens</Typography>
            <OptionTooltip
              title={t(
                "The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model's context length."
              )}
            >
              <IconButton size="small">
                <HelpIcon />
              </IconButton>
            </OptionTooltip>
            <Box
              sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 1 }}
            >
              <TinyInput
                disabled={!enableMaxTokens}
                type="number"
                slotProps={{
                  htmlInput: {
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
            </Box>
          </CommonContainer>
        </OptionCard>
        <Divider sx={{ my: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>enableSearch</Typography>
            <OptionTooltip
              title={t('Whether to use a search engine for data enhancement.')}
            >
              <IconButton size="small">
                <HelpIcon />
              </IconButton>
            </OptionTooltip>
            <Box sx={{ ml: 'auto' }}>
              <Switch
                checked={enableSearch}
                onChange={() => setEnableSearch(!enableSearch)}
              />
            </Box>
          </CommonContainer>
        </OptionCard>
        <Divider sx={{ my: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>seed</Typography>
            <OptionTooltip
              title={t(
                'The random number seed used when generating, the user controls the randomness of the content generated by the model. seed supports unsigned 64-bit integers, with a default value of 1234. When using seed, the model will try its best to generate the same or similar results, but there is currently no guarantee that the results will be exactly the same every time.'
              )}
            >
              <IconButton size="small">
                <HelpIcon />
              </IconButton>
            </OptionTooltip>
            <Box
              sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 1 }}
            >
              <TinyInput
                disabled={!enableSeed}
                type="number"
                slotProps={{
                  htmlInput: {
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
            </Box>
          </CommonContainer>
        </OptionCard>
        <Divider sx={{ my: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>repetitionPenalty</Typography>
            <OptionTooltip
              title={t(
                'Used to control the repeatability when generating models. Increasing repetition_penalty can reduce the duplication of model generation. 1.0 means no punishment.'
              )}
            >
              <IconButton size="small">
                <HelpIcon />
              </IconButton>
            </OptionTooltip>
            <Box
              sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 1 }}
            >
              <TinyInput
                disabled={!enableRepetitionPenalty}
                type="number"
                slotProps={{
                  htmlInput: {
                    min: 1,
                    step: 0.1,
                  },
                }}
                value={repetitionPenalty}
                onChange={(event) => setRepetitionPenalty(+event.target.value)}
              />
              <Switch
                checked={enableRepetitionPenalty}
                onChange={() =>
                  setEnableRepetitionPenalty(!enableRepetitionPenalty)
                }
              />
            </Box>
          </CommonContainer>
        </OptionCard>
        <Divider sx={{ my: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>temperature</Typography>
            <OptionTooltip
              title={t(
                'Used to adjust the degree of randomness from sampling in the generated model, the value range is [0, 2), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.'
              )}
            >
              <IconButton size="small">
                <HelpIcon />
              </IconButton>
            </OptionTooltip>
            <Box
              sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 1 }}
            >
              <TinyInput
                disabled={!enableTemperature}
                type="number"
                slotProps={{
                  htmlInput: {
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
            </Box>
          </CommonContainer>
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
        <Divider sx={{ my: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>stop</Typography>
            <OptionTooltip
              title={t(
                'Sequences where the API will stop generating further tokens.'
              )}
            >
              <IconButton size="small">
                <HelpIcon />
              </IconButton>
            </OptionTooltip>
            <Box sx={{ ml: 'auto' }}>
              <Switch
                checked={enableStop}
                onChange={() => setEnableStop(!enableStop)}
              />
            </Box>
          </CommonContainer>
          <Box sx={{ pt: 1, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {stop?.map(
              (word, index) =>
                word && (
                  <Chip
                    disabled={!enableStop}
                    variant="outlined"
                    key={`${word}-${index}`}
                    onDelete={() => handleStopWordDelete(word)}
                    label={word}
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
