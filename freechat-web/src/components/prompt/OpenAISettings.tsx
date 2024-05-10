/* eslint-disable @typescript-eslint/no-explicit-any */
import { createRef, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { Chip, ChipDelete, DialogContent, DialogTitle, Divider, IconButton, Input, Option, Select, Slider, Switch, Typography } from "@mui/joy";
import { AddCircleRounded } from "@mui/icons-material";
import { CommonContainer, OptionCard, OptionTooltip, Sidedrawer, TinyInput } from "..";
import { AiModelInfoDTO } from 'freechat-sdk';
import { HelpIcon } from "../icon";
import { defaultModels } from "../../configs/model-providers-config";

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
    models?.find(modelInfo => modelInfo?.modelId === (defaultParameters?.modelId ?? defaultModels.open_ai)));
  
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
    setModel(models?.find(modelInfo => modelInfo?.modelId === (defaultParameters?.modelId ?? defaultModels.open_ai)));
    setBaseUrl(defaultParameters?.baseUrl ?? 'https://api.openai.com/v1');

    setTopP(defaultParameters?.topP ?? 0.8);
    setEnableTopP(containsKey(defaultParameters, 'topP'));

    setMaxTokens(defaultParameters?.maxTokens ?? 2000);
    setEnableMaxTokens(containsKey(defaultParameters, 'maxTokens'));

    setSeed(defaultParameters?.seed ?? 1234);
    setEnableSeed(containsKey(defaultParameters, 'seed'));

    setPresencePenalty(defaultParameters?.presencePenalty ?? 0);
    setEnablePresencePenalty(containsKey(defaultParameters, 'presencePenalty'));

    setFrequencyPenalty(defaultParameters?.frequencyPenalty ?? 0);
    setEnableFrequencyPenalty(containsKey(defaultParameters, 'frequencyPenalty'));

    setTemperature(defaultParameters?.temperature ?? 1);
    setEnableTemperature(containsKey(defaultParameters, 'temperature'));

    setStop(defaultParameters?.stop ?? []);
    setEnableStop(containsKey(defaultParameters, 'stop'));
  }, [defaultParameters, models]);

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
    <Sidedrawer open={open} onClose={() => handleClose()}>
      <DialogTitle>{t('Model Parameters')}</DialogTitle>
      <Divider sx={{ mt: 'auto' }} />

      <DialogContent>
        <OptionCard>
          <CommonContainer>
            <Typography>model</Typography>
            <Select
              name="modelName"
              placeholder={<Typography textColor="gray">No model provided</Typography>}
              value={model?.modelId}
              onChange={handleSelectChange}
              sx={{
                ml: 2,
                flex: 1,
            }}>
              {models && models.length > 0 ? models?.map(modelInfo => modelInfo && (
                <Option value={modelInfo.modelId} key={`option-${modelInfo.modelId}`}>{modelInfo.name}</Option>
              )) : (
                <Option value="" key='option-unknown'>--No Model--</Option>
              )}
            </Select>
          </CommonContainer>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <CommonContainer>
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
          </CommonContainer>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>topP</Typography>
            <OptionTooltip title={t('Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.')}>
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
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
            </CommonContainer>
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
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>maxTokens</Typography>
            <OptionTooltip size="sm" title={t('The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model\'s context length.')}>
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
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
            </CommonContainer>
          </CommonContainer>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>seed</Typography>
            <OptionTooltip title={t('The random number seed used when generating, the user controls the randomness of the content generated by the model. seed supports unsigned 64-bit integers, with a default value of 1234. When using seed, the model will try its best to generate the same or similar results, but there is currently no guarantee that the results will be exactly the same every time.')}>
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
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
            </CommonContainer>
          </CommonContainer>
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>presencePenalty</Typography>
            <OptionTooltip title={t('Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model\'s likelihood to talk about new topics.')}>
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
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
            </CommonContainer>
          </CommonContainer>
          <Slider
            disabled={!enablePresencePenalty}
            value={presencePenalty}
            step={0.1}
            min={-2}
            max={2}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => setPresencePenalty(newValue as number)}
          />
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>frequencyPenalty</Typography>
            <OptionTooltip title={t('Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model\'s likelihood to repeat the same line verbatim.')}>
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
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
            </CommonContainer>
          </CommonContainer>
          <Slider
            disabled={!enableFrequencyPenalty}
            value={frequencyPenalty}
            step={0.1}
            min={-2}
            max={2}
            valueLabelDisplay="auto"
            onChange={(_event, newValue) => setFrequencyPenalty(newValue as number)}
          />
        </OptionCard>
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>temperature</Typography>
            <OptionTooltip title={t('Used to adjust the degree of randomness from sampling in the generated model, the value range is [0, 2), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.')}>
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
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
            </CommonContainer>
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
        <Divider sx={{ mt: 'auto', mx: 2 }} />

        <OptionCard>
          <CommonContainer>
            <Typography>stop</Typography>
            <OptionTooltip title={t('Sequences where the API will stop generating further tokens.')}>
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
              <Switch checked={enableStop} onChange={() => setEnableStop(!enableStop)} />
            </CommonContainer>
          </CommonContainer>
          <CommonContainer sx={{ pt: 1 }}>
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
          </CommonContainer>
        </OptionCard>
      </DialogContent>
    </Sidedrawer>
  );
}