import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from '@mui/material';
import { ConnectorWriteDTO } from 'freechat-sdk';
import type { ConnectorDetailsDTO } from 'freechat-sdk';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import { RouterBlocker } from '..';
import {
  changeConnectorMode,
  connectorDraft,
  connectorOriginChanged,
  normalizeConnectorError,
  parseConnectorJson,
  validateConnectorDraft,
} from '../../libs/connector_utils';
import ConnectorEditor from './ConnectorEditor';

type Props = {
  record?: ConnectorDetailsDTO;
  onClose: () => void;
  onSaved: (record: ConnectorDetailsDTO) => void;
};

export default function ConnectorDialog({ record, onClose, onSaved }: Props) {
  const { t } = useTranslation('connector');
  const { connectorApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const [draft, setDraft] = useState(() => connectorDraft(record));
  const [json, setJson] = useState('');
  const [mode, setMode] = useState<'form' | 'json'>('form');
  const [source, setSource] = useState<'form' | 'json'>('form');
  const [errorKey, setErrorKey] = useState<string>();
  const [busy, setBusy] = useState(false);
  const saving = useRef(false);
  const mounted = useRef(true);
  useEffect(() => {
    mounted.current = true;
    return () => {
      mounted.current = false;
    };
  }, []);

  function switchMode(next: 'form' | 'json') {
    if (next === mode) return;
    const result = changeConnectorMode(next, draft, json, source);
    setDraft(result.draft);
    setJson(result.json);
    setMode(next);
    setErrorKey(undefined);
  }

  async function save() {
    if (!connectorApi || saving.current) return;
    setErrorKey(undefined);
    try {
      const current =
        source === 'json' ? parseConnectorJson(json, record) : draft;
      const request = Object.assign(
        new ConnectorWriteDTO(),
        validateConnectorDraft(current, record)
      );
      if (record && (!record.connectorId || !record.revision)) return;
      saving.current = true;
      setBusy(true);
      const result = record
        ? await connectorApi.updateConnector(record.connectorId!, request)
        : await connectorApi.createConnector(request);
      if (mounted.current) onSaved(result);
    } catch (reason) {
      if (!mounted.current) return;
      const error = normalizeConnectorError(reason);
      setErrorKey(error.messageKey);
      if (error.code === 401)
        handleError({ code: 401, message: t(error.messageKey) });
    } finally {
      saving.current = false;
      if (mounted.current) setBusy(false);
    }
  }

  return (
    <>
      <Dialog
        open
        fullWidth
        maxWidth="md"
        onClose={() => !saving.current && onClose()}
      >
        <DialogTitle>
          {record ? t('Edit connector') : t('Add connector')}
        </DialogTitle>
        <DialogContent>
          {record?.active === false && (
            <Alert severity="warning" sx={{ mb: 2 }}>
              {t(
                'Inactive connectors must be reviewed and configured before use. Enable Active only after review.'
              )}
            </Alert>
          )}
          {errorKey && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {t(errorKey)}
            </Alert>
          )}
          {mode === 'form' && source === 'json' && (
            <Alert severity="info" sx={{ mb: 2 }}>
              {t(
                'The JSON draft is preserved. Editing the form replaces it; validation runs only when you save.'
              )}
            </Alert>
          )}
          <ConnectorEditor
            draft={draft}
            json={json}
            mode={mode}
            disabled={busy}
            originChanged={
              mode === 'form' &&
              draft.headers.some((header) => header.value === null) &&
              connectorOriginChanged(draft.url, record)
            }
            onChange={(value) => {
              setDraft(value);
              setSource('form');
              setErrorKey(undefined);
            }}
            onJsonChange={(value) => {
              setJson(value);
              setSource('json');
              setErrorKey(undefined);
            }}
            onModeChange={switchMode}
          />
        </DialogContent>
        <DialogActions>
          <Button disabled={busy} onClick={onClose}>
            {t('Cancel')}
          </Button>
          <Button
            variant="contained"
            disabled={busy || !connectorApi}
            onClick={() => void save()}
          >
            {busy ? t('Saving...') : t('Save')}
          </Button>
        </DialogActions>
      </Dialog>
      <RouterBlocker
        when
        title={t('Leave connector editor?')}
        message={t(
          'Leave and discard this connector draft? New credentials are not stored in your browser.'
        )}
      />
    </>
  );
}
