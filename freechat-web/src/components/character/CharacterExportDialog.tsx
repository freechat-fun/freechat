import { useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Alert,
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  Typography,
} from '@mui/material';
import { useErrorMessageBusContext } from '../../contexts';
import {
  exportCharacter,
  isMissingCharacterDocumentsError,
} from '../../libs/character_utils';
import { normalizeConnectorError } from '../../libs/connector_utils';

type Props = { characterId: number; onClose: () => void };

export default function CharacterExportDialog({ characterId, onClose }: Props) {
  const { t } = useTranslation('connector');
  const { handleError } = useErrorMessageBusContext();
  const [includeConnectors, setIncludeConnectors] = useState(false);
  const [busy, setBusy] = useState(false);
  const busyRef = useRef(false);
  const [pendingExport, setPendingExport] = useState<{
    includeConnectors: boolean;
  }>();
  const [errorKey, setErrorKey] = useState<string>();

  function close() {
    if (busyRef.current) return;
    setPendingExport(undefined);
    setIncludeConnectors(false);
    setErrorKey(undefined);
    onClose();
  }

  async function download(force = false) {
    if (busyRef.current || (force && !pendingExport)) return;
    const requestedConnectors =
      pendingExport?.includeConnectors ?? includeConnectors;
    busyRef.current = true;
    setBusy(true);
    setErrorKey(undefined);
    try {
      await exportCharacter(characterId, requestedConnectors, force);
      setPendingExport(undefined);
      onClose();
    } catch (reason) {
      if (!force && isMissingCharacterDocumentsError(reason)) {
        setPendingExport({ includeConnectors: requestedConnectors });
      } else {
        // A failed forced request requires a new normal attempt, not a loop.
        setPendingExport(undefined);
        const error = normalizeConnectorError(reason);
        setErrorKey(
          error.known
            ? error.messageKey
            : 'Character export failed. Please try again.'
        );
        if (error.code === 401)
          handleError({ code: 401, message: t(error.messageKey) });
      }
    } finally {
      busyRef.current = false;
      setBusy(false);
    }
  }
  return (
    <Dialog open onClose={close} fullWidth maxWidth="sm">
      <DialogTitle>{t('Export character')}</DialogTitle>
      <DialogContent>
        <Typography variant="body2">
          {t(
            'Only published content is transferred. Character and prompt drafts, credentials, user-level Skills and named key settings are not included.'
          )}
        </Typography>
        <FormControlLabel
          control={
            <Checkbox
              checked={includeConnectors}
              disabled={busy || !!pendingExport}
              onChange={(event) => setIncludeConnectors(event.target.checked)}
            />
          }
          label={t('Include connectors (without credentials)')}
        />
        <Alert severity="info">
          {t(
            'Connectors are excluded by default. When selected, only connectors bound to the published character are exported, never credentials.'
          )}
        </Alert>
        <Typography variant="body2" sx={{ mt: 2 }}>
          {t(
            'Imported connectors are private inactive copies owned by the importing user. Review and configure them before use; existing IDs and credentials are never reused across accounts.'
          )}
        </Typography>
        {pendingExport && (
          <Alert severity="warning" sx={{ mt: 2 }}>
            {t(
              'Some knowledge source documents are missing. Continuing will include only the documents that still exist, and the imported knowledge base may be incomplete. Continue?'
            )}
          </Alert>
        )}
        {errorKey && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {t(errorKey)}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        <Button disabled={busy} onClick={close}>
          {t('Cancel')}
        </Button>
        <Button
          disabled={busy}
          variant="contained"
          onClick={() => void download(!!pendingExport)}
        >
          {pendingExport ? t('Continue export') : t('Export')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
