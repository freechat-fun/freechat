import { useTranslation } from 'react-i18next';
import { AddRounded, DeleteOutlineRounded } from '@mui/icons-material';
import {
  Alert,
  Button,
  Checkbox,
  FormControlLabel,
  IconButton,
  MenuItem,
  Stack,
  TextField,
  ToggleButton,
  ToggleButtonGroup,
  Typography,
} from '@mui/material';
import type { ConnectorDraft } from '../../libs/connector_utils';

type Props = {
  draft: ConnectorDraft;
  json: string;
  mode: 'form' | 'json';
  disabled: boolean;
  originChanged: boolean;
  onChange: (draft: ConnectorDraft) => void;
  onJsonChange: (json: string) => void;
  onModeChange: (mode: 'form' | 'json') => void;
};

export default function ConnectorEditor({
  draft,
  json,
  mode,
  disabled,
  originChanged,
  onChange,
  onJsonChange,
  onModeChange,
}: Props) {
  const { t } = useTranslation('connector');
  function headerChange(
    index: number,
    patch: Partial<ConnectorDraft['headers'][number]>
  ) {
    onChange({
      ...draft,
      headers: draft.headers.map((header, i) =>
        i === index ? { ...header, ...patch } : header
      ),
    });
  }
  return (
    <Stack spacing={2} sx={{ pt: 1 }}>
      <ToggleButtonGroup
        exclusive
        size="small"
        value={mode}
        disabled={disabled}
        onChange={(_, value: 'form' | 'json' | null) =>
          value && onModeChange(value)
        }
        aria-label={t('Entry mode')}
      >
        <ToggleButton value="form">{t('Form')}</ToggleButton>
        <ToggleButton value="json">JSON</ToggleButton>
      </ToggleButtonGroup>
      <Typography variant="body2" color="text.secondary">
        {t(
          'Saving only stores configuration. Use Test connection after saving to initialize and list tools; it never calls a tool.'
        )}
      </Typography>
      {originChanged && (
        <Alert severity="warning">
          {t(
            'The URL origin changed. Replace or delete every saved header before saving.'
          )}
        </Alert>
      )}
      {mode === 'json' ? (
        <>
          <Alert severity="info">
            {t(
              'Enter one object with name, transport, url, optional timeout, headers and active, or one mcpServers entry with type http or sse. Unknown fields and STDIO are rejected.'
            )}
          </Alert>
          <TextField
            label={t('Connector JSON')}
            multiline
            minRows={12}
            maxRows={24}
            value={json}
            disabled={disabled}
            onChange={(event) => onJsonChange(event.target.value)}
            autoComplete="off"
            slotProps={{
              htmlInput: { spellCheck: false },
              input: { sx: { fontFamily: 'monospace' } },
            }}
            helperText={t(
              'null keeps a saved value. Only existing saved header names may use null. Omitting a saved header deletes it. JSON contains any new credentials you enter; do not share it.'
            )}
          />
        </>
      ) : (
        <>
          <TextField
            label={t('Name')}
            value={draft.name}
            disabled={disabled}
            required
            onChange={(event) =>
              onChange({ ...draft, name: event.target.value })
            }
          />
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField
              label={t('Transport')}
              select
              value={draft.transport}
              disabled={disabled}
              fullWidth
              onChange={(event) =>
                onChange({ ...draft, transport: event.target.value })
              }
            >
              {draft.transport !== 'http' && draft.transport !== 'sse' && (
                <MenuItem value={draft.transport} disabled>
                  {draft.transport}
                </MenuItem>
              )}
              <MenuItem value="http">HTTP</MenuItem>
              <MenuItem value="sse">SSE</MenuItem>
            </TextField>
            <TextField
              label={t('Timeout (milliseconds, optional)')}
              value={draft.timeout}
              disabled={disabled}
              fullWidth
              slotProps={{ htmlInput: { inputMode: 'numeric' } }}
              onChange={(event) =>
                onChange({ ...draft, timeout: event.target.value })
              }
              helperText={t('Default 60000 ms; minimum 1000, maximum 120000.')}
            />
          </Stack>
          <TextField
            label="URL"
            value={draft.url}
            disabled={disabled}
            required
            autoComplete="off"
            onChange={(event) =>
              onChange({ ...draft, url: event.target.value })
            }
            helperText={t(
              'HTTP without headers is supported. Headers require HTTPS. No credentials in the URL, query parameters or fragments.'
            )}
          />
          <Typography variant="subtitle1">{t('Headers')}</Typography>
          <Typography variant="body2" color="text.secondary">
            {t(
              'Saved values are encrypted on the server and never returned. Replace or delete them explicitly. At most 16 headers, 16 KiB total.'
            )}
          </Typography>
          {draft.headers.map((header, index) => (
            <Stack
              key={index}
              direction={{ xs: 'column', sm: 'row' }}
              spacing={1}
              sx={{ alignItems: { sm: 'flex-start' } }}
            >
              <TextField
                label={t('Header name')}
                value={header.name}
                disabled={disabled}
                fullWidth
                size="small"
                slotProps={{ input: { readOnly: header.value === null } }}
                onChange={(event) =>
                  headerChange(index, { name: event.target.value })
                }
              />
              <TextField
                label={
                  header.value === null ? t('Saved value') : t('Header value')
                }
                type="password"
                value={header.value ?? ''}
                placeholder={header.value === null ? '••••••••' : ''}
                disabled={disabled}
                fullWidth
                size="small"
                autoComplete="new-password"
                slotProps={{ input: { readOnly: header.value === null } }}
                onChange={(event) =>
                  headerChange(index, { value: event.target.value })
                }
              />
              {header.value === null && (
                <Button
                  disabled={disabled}
                  onClick={() => headerChange(index, { value: '' })}
                >
                  {t('Replace')}
                </Button>
              )}
              <IconButton
                disabled={disabled}
                aria-label={t('Delete header {{number}}', {
                  number: index + 1,
                })}
                onClick={() =>
                  onChange({
                    ...draft,
                    headers: draft.headers.filter((_, i) => i !== index),
                  })
                }
              >
                <DeleteOutlineRounded />
              </IconButton>
            </Stack>
          ))}
          <Button
            startIcon={<AddRounded />}
            disabled={disabled || draft.headers.length >= 16}
            sx={{ alignSelf: 'flex-start' }}
            onClick={() =>
              onChange({
                ...draft,
                headers: [...draft.headers, { name: '', value: '' }],
              })
            }
          >
            {t('Add header')}
          </Button>
          <FormControlLabel
            label={t('Active')}
            control={
              <Checkbox
                checked={draft.active}
                disabled={disabled}
                onChange={(event) =>
                  onChange({ ...draft, active: event.target.checked })
                }
              />
            }
          />
        </>
      )}
    </Stack>
  );
}
