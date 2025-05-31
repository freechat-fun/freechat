import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  FormControl,
  FormLabel,
  Select,
  MenuItem,
  Typography,
  IconButton,
  Stack,
  SelectChangeEvent,
} from '@mui/material';
import { CheckRounded, CloseRounded, KeyRounded } from '@mui/icons-material';
import { ConfirmModal, TinyInput } from '../../components';

type AiApiKeySettingsProps = {
  defaultKeyName: string | undefined;
  defaultKeyValue: string | undefined;
  keyNames: (string | undefined)[] | undefined;
  open: boolean;
  onClose: () => void;
  onConfirm: (
    keyName: string | undefined,
    keyValue: string | undefined
  ) => void;
};

export default function AiApiKeySettings(props: AiApiKeySettingsProps) {
  const {
    defaultKeyName,
    defaultKeyValue,
    keyNames,
    open,
    onClose,
    onConfirm,
  } = props;
  const { t } = useTranslation('prompt');

  const [apiKeyName, setApiKeyName] = useState(defaultKeyName ?? '');
  const [apiKeyValue, setApiKeyValue] = useState(defaultKeyValue ?? '');

  const selectRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setApiKeyName(defaultKeyName ?? '');
  }, [defaultKeyName]);

  useEffect(() => {
    setApiKeyValue(defaultKeyValue ?? '');
  }, [defaultKeyValue]);

  function handleClose(
    _event: React.MouseEvent<HTMLButtonElement>,
    reason: string
  ): void {
    if (reason === 'backdropClick') {
      return;
    }
    onClose();
  }

  function handleConfirm(): void {
    onConfirm(apiKeyName, apiKeyValue);
  }

  function handleSelectChange(event: SelectChangeEvent<string>): void {
    const newValue = event.target.value;
    if (newValue !== apiKeyName && newValue !== 'No API Key') {
      setApiKeyName(newValue);
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
        startIcon: <CheckRounded />,
      }}
      onConfirm={handleConfirm}
    >
      <Stack spacing={2}>
        <FormControl fullWidth size="small" sx={{ gap: 1 }}>
          <FormLabel>{t('Select a key')}</FormLabel>
          <Select
            displayEmpty
            ref={selectRef}
            value={apiKeyName || 'No API Key'}
            onChange={handleSelectChange}
            renderValue={(value) => (
              <Typography color="text.secondary">
                {value === 'No API Key' ? '--No API Key--' : value}
              </Typography>
            )}
            slotProps={{
              input: {
                sx: { fontSize: 'small' },
              },
            }}
            endAdornment={
              apiKeyName && (
                <IconButton
                  size="small"
                  onClick={(e) => {
                    e.stopPropagation();
                    setApiKeyName('');
                    selectRef.current?.focus();
                  }}
                  sx={{ mr: 1 }}
                >
                  <CloseRounded fontSize="small" />
                </IconButton>
              )
            }
          >
            {keyNames && keyNames.length > 0 ? (
              keyNames?.map(
                (keyName) =>
                  keyName && (
                    <MenuItem value={keyName} key={`option-${keyName}`}>
                      {keyName}
                    </MenuItem>
                  )
              )
            ) : (
              <MenuItem value="No API Key" key="option-unknown">
                --No API Key--
              </MenuItem>
            )}
          </Select>
        </FormControl>
        <FormControl fullWidth size="small" sx={{ gap: 1 }}>
          <FormLabel>{t('or you can use a temporary key')}</FormLabel>
          <TinyInput
            type="password"
            placeholder="Paste a key here..."
            value={apiKeyValue}
            onChange={handleValueChange}
            slotProps={{
              input: {
                startAdornment: (
                  <KeyRounded sx={{ mr: 1, color: 'action.active' }} />
                ),
              },
            }}
            sx={{
              minWidth: '20rem',
            }}
          />
        </FormControl>
      </Stack>
    </ConfirmModal>
  );
}
