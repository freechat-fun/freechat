import { useState } from 'react';
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
import { exportCharacter } from '../../libs/character_utils';
import { normalizeConnectorError } from '../../libs/connector_utils';

type Props = { characterId: number; onClose: () => void };

export default function CharacterExportDialog({ characterId, onClose }: Props) {
  const { t } = useTranslation('connector');
  const { handleError } = useErrorMessageBusContext();
  const [includeConnectors, setIncludeConnectors] = useState(false);
  const [busy, setBusy] = useState(false);
  const [errorKey, setErrorKey] = useState<string>();
  async function download() {
    if (busy) return;
    setBusy(true);
    setErrorKey(undefined);
    try {
      await exportCharacter(characterId, includeConnectors);
      onClose();
    } catch (reason) {
      const error = normalizeConnectorError(reason);
      setErrorKey(
        error.known
          ? error.messageKey
          : 'Character export failed. Please try again.'
      );
      if (error.code === 401)
        handleError({ code: 401, message: t(error.messageKey) });
    } finally {
      setBusy(false);
    }
  }
  return (
    <Dialog open onClose={() => !busy && onClose()} fullWidth maxWidth="sm">
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
              disabled={busy}
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
        {errorKey && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {t(errorKey)}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        <Button disabled={busy} onClick={onClose}>
          {t('Cancel')}
        </Button>
        <Button
          disabled={busy}
          variant="contained"
          onClick={() => void download()}
        >
          {t('Export')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
