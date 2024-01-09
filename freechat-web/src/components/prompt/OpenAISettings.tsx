/* eslint-disable @typescript-eslint/no-explicit-any */
import { createRef, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { Chip, ChipDelete, DialogContent, DialogTitle, Divider, Drawer, IconButton, Input, Option, Select, Sheet, Slider, Switch, Tooltip, Typography } from "@mui/joy";
import { AddCircleRounded, HelpOutlineRounded } from "@mui/icons-material";
import { CommonBox, OptionCard, TinyInput } from "..";
import { AiModelInfoDTO } from 'freechat-sdk';

function containsKey(parameters: { [key: string]: any } | undefined, key: string): boolean {
  return !!parameters && Object.keys(parameters).includes(key);
}

export default function OpenAISettings(props: {
  open: boolean,
  models:(AiModelInfoDTO | undefined)[] | undefined,
  onClose: (parameters: { [key: string]: any }) => void,
  defaultParameters?: { [key: string]: any },
}) {
  const { open, models, onClose, defaultParameters } = props;

  const { t } = useTranslation(['prompt']);

  const [model, setModel] = useState<AiModelInfoDTO | undefined>(
    models?.find(modelInfo => modelInfo?.modelId === (defaultParameters?.modelId ?? '[open_ai]gpt-4')));
  
  const [baseUrl, setBaseUrl] = useState(defaultParameters?.baseUrl ?? 'https://api.openai.com/v1');

  const [topP, setTopP] = useState<number>(defaultParameters?.topP ?? 0.8);
  const [enableTopP, setEnableTopP] = useState(containsKey(defaultParameters, 'topP'));

  const [maxTokens, setMaxTokens] = useState<number>(defaultParameters?.maxTokens ?? 2000);
  const [enableMaxTokens, setEnableMaxTokens] = useState(containsKey(defaultParameters, 'maxTokens'));

  const [seed, setSeed] = useState<number>(defaultParameters?.seed ?? 1234);
  const [enableSeed, setEnableSeed] = useState(containsKey(defaultParameters, 'seed'));

  const [presencePenalty, setPresencePenalty] = useState<number>(defaultParameters?.presencePenalty ?? 0);
  const [enablePresencePenalty, setEnablePresencePenalty] = useState(containsKey(defaultParameters, 'presencePenalty'));

  const [frequencyPenalty, setFrequencyPenalty] = useState<number>(defaultParameters?.frequencyPenalty ?? 0);
  const [enableFrequencyPenalty, setEnableFrequencyPenalty] = useState(containsKey(defaultParameters, 'frequencyPenalty'));

  const [temperature, setTemperature] = useState<number>(defaultParameters?.temperature ?? 1);
  const [enableTemperature, setEnableTemperature] = useState(containsKey(defaultParameters, 'temperature'));

  const [stop, setStop] = useState<string[]>(defaultParameters?.stop ?? []);
  const [enableStop, setEnableStop] = useState(containsKey(defaultParameters, 'stop'));
  const [stopWord, setStopWord] = useState<string>();

  const inputRefs = useRef(Array(6).fill(createRef<HTMLInputElement | null>()));

  useEffect(() => {
    setModel(models?.find(modelInfo => modelInfo?.modelId === (defaultParameters?.modelId ?? '[open_ai]gpt-4')));
  }, [defaultParameters?.modelId, models]);

  function handleSelectChange(_event: React.SyntheticEvent | null, newValue: string | null): void {
    if (newValue && newValue !== model?.modelId) {
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

    if (baseUrl) {
      parameters['baseUrl'] = baseUrl;
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
              <Typography>baseUrl</Typography>
              <Input
                type="text"
                value={baseUrl}
                sx={{
                  ml: 0.5,
                  flex: 1,
                }}
                onChange={(event => setBaseUrl(event.target.value))}
              />
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
              <Typography>presencePenalty</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model\'s likelihood to talk about new topics.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={!enablePresencePenalty}
                  type="number"
                  slotProps={{
                    input: {
                      ref: inputRefs.current[4],
                      min: -2,
                      max: 2,
                      step: 0.1,
                    },
                  }}
                  value={presencePenalty}
                  onChange={(event => setPresencePenalty(+event.target.value))}
                />
                <Switch checked={enablePresencePenalty} onChange={() => setEnablePresencePenalty(!enablePresencePenalty)} />
              </CommonBox>
            </CommonBox>
            <Slider
              disabled={!enablePresencePenalty}
              value={presencePenalty}
              step={0.1}
              min={-2}
              max={2}
              valueLabelDisplay="auto"
              onChange={(_event: Event, newValue: number | number[]) => setPresencePenalty(newValue as number)}
            />
          </OptionCard>
          <Divider sx={{ mt: 'auto', mx: 2 }} />

          <OptionCard>
            <CommonBox>
              <Typography>frequencyPenalty</Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model\'s likelihood to repeat the same line verbatim.')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
              <CommonBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={!enableFrequencyPenalty}
                  type="number"
                  slotProps={{
                    input: {
                      ref: inputRefs.current[4],
                      min: -2,
                      max: 2,
                      step: 0.1,
                    },
                  }}
                  value={frequencyPenalty}
                  onChange={(event => setFrequencyPenalty(+event.target.value))}
                />
                <Switch checked={enableFrequencyPenalty} onChange={() => setEnableFrequencyPenalty(!enableFrequencyPenalty)} />
              </CommonBox>
            </CommonBox>
            <Slider
              disabled={!enableFrequencyPenalty}
              value={frequencyPenalty}
              step={0.1}
              min={-2}
              max={2}
              valueLabelDisplay="auto"
              onChange={(_event: Event, newValue: number | number[]) => setFrequencyPenalty(newValue as number)}
            />
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