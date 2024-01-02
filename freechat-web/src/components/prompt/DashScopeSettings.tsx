/* eslint-disable @typescript-eslint/no-explicit-any */
import { createRef, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { Chip, ChipDelete, DialogContent, DialogTitle, Divider, Drawer, IconButton, Option, Select, Sheet, Slider, Switch, Tooltip, Typography } from "@mui/joy";
import { AddCircleRounded, HelpOutlineRounded } from "@mui/icons-material";
import { CommonBox, OptionCard, TinyInput } from "..";
import { AiModelInfoDTO } from 'freechat-sdk';

function containsKey(parameters: { [key: string]: any } | undefined, key: string): boolean {
  return !!parameters && Object.keys(parameters).includes(key);
}

export default function DashScopeSettings(props: {
  open: boolean,
  models:(AiModelInfoDTO | undefined)[] | undefined,
  onClose: (parameters: { [key: string]: any }) => void,
  defaultParameters?: { [key: string]: any },
}) {
  const { open, models, onClose, defaultParameters } = props;

  const { t } = useTranslation(['prompt']);

  const [model, setModel] = useState<AiModelInfoDTO | undefined>(
    models?.find(modelInfo => modelInfo?.modelId === defaultParameters?.modelId));

  const [topP, setTopP] = useState<number>(defaultParameters?.topP ?? 0.8);
  const [enableTopP, setEnableTopP] = useState(containsKey(defaultParameters, 'topP'));

  const [topK, setTopK] = useState<number>(defaultParameters?.topK ?? 0);
  const [enableTopK, setEnableTopK] = useState(containsKey(defaultParameters, 'topK'));

  const [maxTokens, setMaxTokens] = useState<number>(defaultParameters?.maxTokens ?? 2000);
  const [enableMaxTokens, setEnableMaxTokens] = useState(containsKey(defaultParameters, 'maxTokens'));

  const [enableSearch, setEnableSearch] = useState<boolean>(defaultParameters?.enableSearch ?? false);

  const [seed, setSeed] = useState<number>(defaultParameters?.seed ?? 1234);
  const [enableSeed, setEnableSeed] = useState(containsKey(defaultParameters, 'seed'));

  const [repetitionPenalty, setRepetitionPenalty] = useState<number>(defaultParameters?.repetitionPenalty ?? 1.1);
  const [enableRepetitionPenalty, setEnableRepetitionPenalty] = useState(containsKey(defaultParameters, 'repetitionPenalty'));

  const [temperature, setTemperature] = useState<number>(defaultParameters?.temperature ?? 1);
  const [enableTemperature, setEnableTemperature] = useState(containsKey(defaultParameters, 'temperature'));

  const [stop, setStop] = useState<string[]>(defaultParameters?.stop ?? []);
  const [enableStop, setEnableStop] = useState(containsKey(defaultParameters, 'stop'));
  const [stopWord, setStopWord] = useState<string>();

  const inputRefs = useRef(Array(6).fill(createRef<HTMLInputElement | null>()));

  useEffect(() => {
    setModel(models?.find(modelInfo => modelInfo?.modelId === defaultParameters?.modelId));
  }, [defaultParameters?.modelId, models]);

  function handleSelectChange(_event: React.SyntheticEvent | null, newValue: string | null): void {
    if (newValue && newValue !== model) {
      setModel(models?.find(modelInfo => modelInfo?.modelId === newValue))
    }
  }

  function handleStopWordSubmit(event: React.FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    stopWord && !stop.includes(stopWord) && setStop([...stop, stopWord]);
    setStopWord(undefined);
  }

  function handleStopWordDelete(word: string): void {
    setStop(stop.filter(stopWord => stopWord !== word));
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
    <Drawer
      size="md"
      variant="plain"
      anchor="right"
      open={open}
      onClose={() => handleClose()}
      slotProps={{
        content: {
          sx: {
            mt: 'var(--Header-height)',
            md: 50,
            bgcolor: 'transparent',
            p: { md: 2, sm: 0 },
            boxShadow: 'none',
          },
        },
      }}
    >
      <Sheet
        sx={{
          borderRadius: 'md',
          p: 2,
          display: 'flex',
          flexDirection: 'column',
          gap: 1,
          height: 'calc(100% - var(--Header-height))',
          overflow: 'auto',
        }}
      >
        <DialogTitle>{t('Model Parameters')}</DialogTitle>
        <Divider sx={{ mt: 'auto' }} />

        <DialogContent>
          <OptionCard>
            <CommonBox>
              <Typography>model</Typography>
              <Select
              name="modelName"
              placeholder={<Typography textColor="gray">No model provided</Typography>}
              value={model?.modelId}
              onChange={handleSelectChange}
              sx={{
                ml: 2,
                flex: 1,
              }}
            >
              {models && models.length > 0 ? models?.map(modelInfo => modelInfo && (
                <Option value={modelInfo.modelId} key={`option-${modelInfo.modelId}`}>{modelInfo.name}</Option>
              )) : (
                <Option value="" key='option-unknown'>--No Model--</Option>
              )}
            </Select>
            </CommonBox>
          </OptionCard>
          <Divider sx={{ mt: 'auto', mx: 2 }} />

          <OptionCard>
            <CommonBox>
              <Typography>topP</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={!enableTopP}
                  type="number"
                  slotProps={{
                    input: {
                      ref: inputRefs.current[0],
                      min: 0,
                      max: 1,
                      step: 0.01,
                    },
                  }}
                  value={topP}
                  onChange={(event => setTopP(+event.target.value))}
                />
                <Switch checked={enableTopP} onChange={() => setEnableTopP(!enableTopP)} />
              </CommonBox>
            </CommonBox>
            <Slider
              disabled={!enableTopP}
              value={topP}
              step={0.01}
              min={0}
              max={1}
              valueLabelDisplay="auto"
              onChange={(_event: Event, newValue: number | number[]) => setTopP(newValue as number)}
            />
          </OptionCard>
          <Divider sx={{ mt: 'auto', mx: 2 }} />

          <OptionCard>
            <CommonBox>
              <Typography>topK</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('The size of the sampling candidate set during generation. For example, when the value is 50, only the top 50 tokens with the highest scores in a single generation are included in the random sampling candidate set. The larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. The default value is 0, which means that the top_k strategy is not enabled, and only the top_p strategy is effective.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={!enableTopK}
                  type="number"
                  slotProps={{
                    input: {
                      ref: inputRefs.current[1],
                      min: 0,
                      max: 100,
                      step: 1,
                    },
                  }}
                  value={topK}
                  onChange={(event => setTopK(+event.target.value))}
                />
                <Switch checked={enableTopK} onChange={() => setEnableTopK(!enableTopK)} />
              </CommonBox>
            </CommonBox>
            <Slider
              disabled={!enableTopK}
              value={topK}
              step={1}
              min={0}
              max={100}
              valueLabelDisplay="auto"
              onChange={(_event: Event, newValue: number | number[]) => setTopK(newValue as number)}
            />
          </OptionCard>
          <Divider sx={{ mt: 'auto', mx: 2 }} />

          <OptionCard>
            <CommonBox>
              <Typography>maxTokens</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model\'s context length.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={!enableMaxTokens}
                  type="number"
                  slotProps={{
                    input: {
                      ref: inputRefs.current[2],
                      min: 1000,
                      step: 1000,
                    },
                  }}
                  value={maxTokens}
                  onChange={(event => setMaxTokens(+event.target.value))}
                />
                <Switch checked={enableMaxTokens} onChange={() => setEnableMaxTokens(!enableMaxTokens)} />
              </CommonBox>
            </CommonBox>
          </OptionCard>
          <Divider sx={{ mt: 'auto', mx: 2 }} />

          <OptionCard>
            <CommonBox>
              <Typography>enableSearch</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('Whether to use a search engine for data enhancement.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <Switch checked={enableSearch} onChange={() => setEnableSearch(!enableSearch)} />
              </CommonBox>
            </CommonBox>
          </OptionCard>
          <Divider sx={{ mt: 'auto', mx: 2 }} />

          <OptionCard>
            <CommonBox>
              <Typography>seed</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('The random number seed used when generating, the user controls the randomness of the content generated by the model. seed supports unsigned 64-bit integers, with a default value of 1234. When using seed, the model will try its best to generate the same or similar results, but there is currently no guarantee that the results will be exactly the same every time.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={!enableSeed}
                  type="number"
                  slotProps={{
                    input: {
                      ref: inputRefs.current[3],
                      min: 1,
                      step: 1,
                    },
                  }}
                  value={seed}
                  onChange={(event => setSeed(+event.target.value))}
                />
                <Switch checked={enableSeed} onChange={() => setEnableSeed(!enableSeed)} />
              </CommonBox>
            </CommonBox>
          </OptionCard>
          <Divider sx={{ mt: 'auto', mx: 2 }} />

          <OptionCard>
            <CommonBox>
              <Typography>repetitionPenalty</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('Used to control the repeatability when generating models. Increasing repetition_penalty can reduce the duplication of model generation. 1.0 means no punishment.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={!enableRepetitionPenalty}
                  type="number"
                  slotProps={{
                    input: {
                      ref: inputRefs.current[4],
                      min: 1,
                      step: 0.1,
                    },
                  }}
                  value={repetitionPenalty}
                  onChange={(event => setRepetitionPenalty(+event.target.value))}
                />
                <Switch checked={enableRepetitionPenalty} onChange={() => setEnableRepetitionPenalty(!enableRepetitionPenalty)} />
              </CommonBox>
            </CommonBox>
          </OptionCard>
          <Divider sx={{ mt: 'auto', mx: 2 }} />

          <OptionCard>
            <CommonBox>
              <Typography>temperature</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('Used to adjust the degree of randomness from sampling in the generated model, the value range is [0, 2), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={!enableTemperature}
                  type="number"
                  slotProps={{
                    input: {
                      ref: inputRefs.current[5],
                      min: 0,
                      max: 2,
                      step: 0.1,
                    },
                  }}
                  value={temperature}
                  onChange={(event => setTemperature(+event.target.value))}
                />
                <Switch checked={enableTemperature} onChange={() => setEnableTemperature(!enableTemperature)} />
              </CommonBox>
            </CommonBox>
            <Slider
              disabled={!enableTemperature}
              value={temperature}
              step={0.1}
              min={0}
              max={2}
              valueLabelDisplay="auto"
              onChange={(_event: Event, newValue: number | number[]) => setTemperature(newValue as number)}
            />
          </OptionCard>
          <Divider sx={{ mt: 'auto', mx: 2 }} />

          <OptionCard>
            <CommonBox>
              <Typography>stop</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('Sequences where the API will stop generating further tokens.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <Switch checked={enableStop} onChange={() => setEnableStop(!enableStop)} />
              </CommonBox>
            </CommonBox>
            <CommonBox sx={{ pt: 1 }}>
              {stop && stop.length > 0 && stop.map((word, index) => word && (
                <Chip
                  disabled={!enableStop}
                  variant="outlined"
                  key={`${word}-${index}`}
                  endDecorator={<ChipDelete onDelete={() => handleStopWordDelete(word)} />}
                >
                  {word}
                </Chip>
              ))}
              {(!stop || stop.length < 4) && (stopWord === undefined) && (
                <IconButton
                  disabled={!enableStop}
                  color="primary"
                  onClick={() => setStopWord('')}
                >
                  <AddCircleRounded />
                </IconButton>
              )}
              {(stopWord !== undefined) && (
                <form onSubmit={handleStopWordSubmit}>
                  <TinyInput
                    disabled={!enableStop}
                    type="text"
                    value={stopWord}
                    onChange={(event => setStopWord(event.target.value))}
                  />
                </form>
              )}
            </CommonBox>
          </OptionCard>
        </DialogContent>
      </Sheet>
    </Drawer>
  );
}