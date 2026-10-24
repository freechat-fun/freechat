import { useEffect, useEffectEvent, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import {
  ArrowBackRounded,
  DeleteOutlineRounded,
  EditRounded,
  RefreshRounded,
  CableRounded,
} from '@mui/icons-material';
import {
  Alert,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import type { ConnectorDetailsDTO, ConnectorTestDTO } from 'freechat-sdk';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import ConnectorDialog from '../../components/connector/ConnectorDialog';
import { normalizeConnectorError } from '../../libs/connector_utils';

export default function ConnectorInfo() {
  const { id } = useParams();
  const { username } = useMetaInfoContext();
  return username && id ? (
    <ConnectorDetails key={`${username}:${id}`} id={id} />
  ) : null;
}

function ConnectorDetails({ id }: { id: string }) {
  const { t } = useTranslation('connector');
  const navigate = useNavigate();
  const { connectorApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const [record, setRecord] = useState<ConnectorDetailsDTO>();
  const [loading, setLoading] = useState(true);
  const [loadErrorKey, setLoadErrorKey] = useState<string>();
  const [errorKey, setErrorKey] = useState<string>();
  const [reload, setReload] = useState(0);
  const [editing, setEditing] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [busy, setBusy] = useState(false);
  const [result, setResult] = useState<ConnectorTestDTO>();
  const pending = useRef(false);
  const mounted = useRef(true);
  const reportLoad = useEffectEvent((reason: unknown) => {
    const error = normalizeConnectorError(reason);
    setLoadErrorKey(error.messageKey);
    if (error.code === 401)
      handleError({ code: 401, message: t(error.messageKey) });
  });
  useEffect(() => {
    mounted.current = true;
    return () => {
      mounted.current = false;
    };
  }, []);
  useEffect(() => {
    if (!connectorApi) return;
    let active = true;
    connectorApi
      .getConnector(id)
      .then((response) => {
        if (active) setRecord(response);
      })
      .catch((reason: unknown) => {
        if (active) reportLoad(reason);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [connectorApi, id, reload]);

  function reloadRecord() {
    setLoading(true);
    setLoadErrorKey(undefined);
    setErrorKey(undefined);
    setResult(undefined);
    setReload((value) => value + 1);
  }
  async function run(action: 'test' | 'delete') {
    if (!connectorApi || !record?.revision || pending.current) return;
    pending.current = true;
    setBusy(true);
    setErrorKey(undefined);
    setResult(undefined);
    try {
      if (action === 'test') {
        const response = await connectorApi.testConnector(id, record.revision);
        if (mounted.current) setResult(response);
      } else {
        await connectorApi.deleteConnector(id, record.revision);
        if (mounted.current) navigate('/w/connectors');
      }
    } catch (reason) {
      if (!mounted.current) return;
      const error = normalizeConnectorError(reason);
      setErrorKey(error.messageKey);
      if (error.code === 401)
        handleError({ code: 401, message: t(error.messageKey) });
    } finally {
      pending.current = false;
      if (mounted.current) setBusy(false);
    }
  }

  if (loading)
    return (
      <CircularProgress aria-label={t('Loading connector')} sx={{ m: 3 }} />
    );
  if (loadErrorKey || !record)
    return (
      <Alert
        severity="error"
        sx={{ my: 2 }}
        action={<Button onClick={reloadRecord}>{t('Retry')}</Button>}
      >
        {t(loadErrorKey ?? 'This connector is no longer available.')}
      </Alert>
    );
  return (
    <Stack spacing={2} sx={{ py: 2 }}>
      <Stack
        direction="row"
        useFlexGap
        sx={{ flexWrap: 'wrap', gap: 1, alignItems: 'center' }}
      >
        <Button
          startIcon={<ArrowBackRounded />}
          onClick={() => navigate('/w/connectors')}
        >
          {t('My connectors')}
        </Button>
        <Typography variant="h4" sx={{ flex: 1, overflowWrap: 'anywhere' }}>
          {record.name}
        </Typography>
        <Button
          startIcon={<RefreshRounded />}
          disabled={busy}
          onClick={reloadRecord}
        >
          {t('Reload')}
        </Button>
        <Button
          startIcon={<EditRounded />}
          variant="contained"
          disabled={busy}
          onClick={() => {
            setErrorKey(undefined);
            setEditing(true);
          }}
        >
          {t('Edit')}
        </Button>
        <Button
          startIcon={<DeleteOutlineRounded />}
          color="error"
          disabled={busy}
          onClick={() => {
            setErrorKey(undefined);
            setDeleting(true);
          }}
        >
          {t('Delete')}
        </Button>
      </Stack>
      <Stack direction="row" spacing={1}>
        <Chip label={t('Private')} size="small" variant="outlined" />
        <Chip
          label={record.transport?.toUpperCase()}
          size="small"
          color="success"
          variant="outlined"
        />
        <Chip
          label={record.active ? t('Active') : t('Inactive')}
          size="small"
          color={record.active ? 'success' : 'warning'}
          variant="outlined"
        />
      </Stack>
      {!record.active && (
        <Alert severity="warning">
          {t(
            'Inactive connectors must be reviewed and configured before use. Enable Active only after review.'
          )}
        </Alert>
      )}
      <TextField
        label="URL"
        value={record.url ?? ''}
        slotProps={{ input: { readOnly: true } }}
      />
      <Typography>
        {t('Timeout')}: {record.timeout ?? 60000} ms
      </Typography>
      <Typography
        variant="body2"
        color="text.secondary"
        sx={{ overflowWrap: 'anywhere' }}
      >
        {t('Revision')}: {record.revision}
      </Typography>
      <Typography variant="h6">{t('Headers')}</Typography>
      <Typography variant="body2" color="text.secondary">
        {t(
          'Saved values are encrypted on the server and never returned. Replace or delete them explicitly. At most 16 headers, 16 KiB total.'
        )}
      </Typography>
      {record.headerNames?.length ? (
        Array.from(record.headerNames).map((name) => (
          <TextField
            key={name}
            label={name}
            type="password"
            value=""
            placeholder="••••••••"
            autoComplete="off"
            slotProps={{ input: { readOnly: true } }}
          />
        ))
      ) : (
        <Typography variant="body2">{t('No configured headers.')}</Typography>
      )}
      <Divider />
      <Typography variant="body2">
        {t(
          'Saving only stores configuration. Use Test connection after saving to initialize and list tools; it never calls a tool.'
        )}
      </Typography>
      <Alert severity="warning">
        {t(
          'Remote tools can have side effects. Ownership is not a sandbox; review the server before enabling it on a character.'
        )}
      </Alert>
      <Button
        startIcon={busy ? <CircularProgress size={18} /> : <CableRounded />}
        disabled={busy || !record.revision}
        onClick={() => void run('test')}
        sx={{ alignSelf: 'flex-start' }}
      >
        {busy ? t('Working...') : t('Test connection')}
      </Button>
      {errorKey && !deleting && <Alert severity="error">{t(errorKey)}</Alert>}
      {result && (
        <Stack spacing={1}>
          <Alert severity="success">
            {t(
              'Connection succeeded. {{count}} tools reported; no tools were invoked.',
              { count: result.tools?.length ?? 0 }
            )}
          </Alert>
          <Typography variant="body2" color="text.secondary">
            {t(
              'Tool names and descriptions are untrusted server text. Showing at most 32 tools and 1000 characters per description.'
            )}
          </Typography>
          {(result.tools ?? []).slice(0, 32).map((tool, index) => (
            <Stack
              key={index}
              sx={{ p: 2, border: 1, borderColor: 'divider', borderRadius: 2 }}
            >
              <Typography variant="subtitle1" sx={{ overflowWrap: 'anywhere' }}>
                {tool.name?.slice(0, 160)}
              </Typography>
              <Typography
                variant="body2"
                sx={{ whiteSpace: 'pre-wrap', overflowWrap: 'anywhere' }}
              >
                {tool.description?.slice(0, 1000)}
              </Typography>
            </Stack>
          ))}
        </Stack>
      )}
      {editing && (
        <ConnectorDialog
          record={record}
          onClose={() => setEditing(false)}
          onSaved={(response) => {
            setRecord(response);
            setEditing(false);
            setResult(undefined);
            setErrorKey(undefined);
          }}
        />
      )}
      <Dialog
        open={deleting}
        onClose={() => !pending.current && setDeleting(false)}
      >
        <DialogTitle>{t('Delete this private connector?')}</DialogTitle>
        <DialogContent>
          <Typography>
            {t(
              'Characters using this connector will lose access. This cannot be undone.'
            )}
          </Typography>
          {errorKey && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {t(errorKey)}
            </Alert>
          )}
        </DialogContent>
        <DialogActions>
          <Button disabled={busy} onClick={() => setDeleting(false)}>
            {t('Cancel')}
          </Button>
          <Button
            color="error"
            disabled={busy}
            onClick={() => void run('delete')}
          >
            {t('Delete')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
