import { Fragment, useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useErrorMessageBusContext, useFreeChatApiContext } from '../contexts';
import { SearchRounded } from '@mui/icons-material';
import { Box, Input, Select, Option, Chip, Typography, Button } from '@mui/joy';
import { providers as modelProviders } from '../configs/model-providers-config';
import { AiModelInfoDTO } from 'freechat-sdk';

export default function InfoSearchbar(props: {
  onSearch: (text: string | undefined, modelIds: string[] | undefined) => void;
  enableModelSelect?: boolean;
}) {
  const { onSearch, enableModelSelect = true } = props;
  const { t } = useTranslation('button');
  const { aiServiceApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [text, setText] = useState('');
  const [providers, setProviders] = useState<string[] | undefined>();
  const [models, setModels] = useState<AiModelInfoDTO[]>([]);

  const getModels = useCallback(() => {
    aiServiceApi?.listAiModelInfo().then(setModels).catch(handleError);
  }, [aiServiceApi, handleError]);

  useEffect(() => {
    if (enableModelSelect) {
      getModels();
    }
  }, [enableModelSelect, getModels]);

  function getModelIdsByProvider(provider: string): string[] {
    return models
      ?.filter((model) => model.modelId && model.provider === provider)
      .map((model) => model.modelId || '');
  }

  function handleInputChange(event: React.ChangeEvent<HTMLInputElement>): void {
    if (event.target.value !== text) {
      setText(event.target.value);
    }
  }

  function handleSelectChange(
    _event: React.SyntheticEvent | null,
    newValue: Array<string> | null
  ): void {
    if (newValue !== providers) {
      setProviders(newValue || []);
    }
  }

  function handleSubmit(event: React.FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    const modelIds: string[] = [];
    if (enableModelSelect) {
      providers?.forEach((provider) =>
        modelIds.push(...getModelIdsByProvider(provider))
      );
    }
    onSearch(text, modelIds);
  }

  return (
    <form onSubmit={handleSubmit}>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'start',
          alignItems: 'center',
          py: 1,
          pr: 1,
          gap: 3,
          borderTopLeftRadius: 'var(--unstable_actionRadius)',
          borderTopRightRadius: 'var(--unstable_actionRadius)',
        }}
      >
        <Input
          name="text"
          type="text"
          value={text}
          onChange={handleInputChange}
          placeholder={t('Search title, description, content and more')}
          startDecorator={<SearchRounded />}
          sx={{ flex: 1 }}
        />
        {enableModelSelect && (
          <Fragment>
            <Select
              placeholder={
                <Typography textColor="gray">
                  {t('Select model providers')}
                </Typography>
              }
              value={providers}
              multiple
              onChange={handleSelectChange}
              renderValue={(selected) => (
                <Box sx={{ display: 'flex', gap: '0.25rem' }}>
                  {selected.map((selectedOption) => (
                    <Chip
                      variant="soft"
                      color="primary"
                      key={`${selectedOption.id}-${selectedOption.value}`}
                    >
                      {selectedOption.label}
                    </Chip>
                  ))}
                </Box>
              )}
              sx={{
                minWidth: '14rem',
              }}
            >
              {modelProviders.map((p) => (
                <Option value={p.provider} key={`options-${p.provider}`}>
                  {p.label}
                </Option>
              ))}
            </Select>
          </Fragment>
        )}
        <Button type="submit" variant="soft">
          {t('Search')}
        </Button>
      </Box>
    </form>
  );
}
