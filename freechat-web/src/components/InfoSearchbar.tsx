import { Fragment, useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useErrorMessageBusContext, useFreeChatApiContext } from '../contexts';
import SearchIcon from '@mui/icons-material/Search';
import {
  Box,
  TextField,
  Select,
  MenuItem,
  Chip,
  Button,
  FormControl,
  InputLabel,
  SelectChangeEvent,
  Checkbox,
  ListItemText,
  SxProps,
  Theme,
} from '@mui/material';
import { providers as modelProviders } from '../configs/model-providers-config';
import { AiModelInfoDTO } from 'freechat-sdk';

export default function InfoSearchbar(props: {
  onSearch: (text: string | undefined, modelIds: string[] | undefined) => void;
  enableModelSelect?: boolean;
  sx?: SxProps<Theme>;
}) {
  const { onSearch, enableModelSelect = true, sx } = props;
  const { t } = useTranslation('button');
  const { aiServiceApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [text, setText] = useState('');
  const [providers, setProviders] = useState<string[]>([]);
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

  function handleSelectChange(event: SelectChangeEvent<string[]>): void {
    const newValue = event.target.value as string[];
    if (newValue !== providers) {
      setProviders(newValue);
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
          borderRadius: 1,
          ...sx,
        }}
      >
        <TextField
          name="text"
          type="text"
          value={text}
          size="small"
          onChange={handleInputChange}
          placeholder={t('Search title, description, content and more')}
          slotProps={{
            input: {
              startAdornment: (
                <SearchIcon sx={{ color: 'action.active', mr: 1 }} />
              ),
              sx: {
                borderRadius: '6px',
              },
            },
          }}
          sx={{
            flex: 1,
          }}
        />
        {enableModelSelect && (
          <Fragment>
            <FormControl sx={{ minWidth: '14rem' }} size="small">
              <InputLabel id="model-providers-label">
                {t('Select model providers')}
              </InputLabel>
              <Select
                label={t('Select model providers')}
                multiple
                value={providers}
                onChange={handleSelectChange}
                renderValue={(selected) => (
                  <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                    {selected.map((value) => (
                      <Chip
                        key={value}
                        label={
                          modelProviders.find((p) => p.provider === value)
                            ?.label || value
                        }
                        size="small"
                      />
                    ))}
                  </Box>
                )}
              >
                {modelProviders.map((p) => (
                  <MenuItem key={p.provider} value={p.provider}>
                    <Checkbox
                      size="small"
                      checked={providers.includes(p.provider)}
                    />
                    <ListItemText
                      primary={p.label}
                      sx={{ fontSize: 'small' }}
                    />
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Fragment>
        )}
        <Button size="small" type="submit" variant="contained" color="primary">
          {t('Search')}
        </Button>
      </Box>
    </form>
  );
}
