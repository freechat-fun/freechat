import { useEffect, useEffectEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Checkbox,
  CircularProgress,
  FormControlLabel,
  Stack,
  Typography,
} from '@mui/material';
import type { ConnectorDetailsDTO } from 'freechat-sdk';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import {
  isConnectorId,
  normalizeConnectorError,
} from '../../libs/connector_utils';

type Props = {
  enabled: string[];
  disabled: boolean;
  toolFree: boolean;
  onChange: (ids: string[]) => void;
};

export default function CharacterConnectorSelector(props: Props) {
  const { username } = useMetaInfoContext();
  return username ? <OwnerConnectorSelector key={username} {...props} /> : null;
}

function OwnerConnectorSelector({
  enabled,
  disabled,
  toolFree,
  onChange,
}: Props) {
  const { t } = useTranslation('connector');
  const { connectorApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const [records, setRecords] = useState<ConnectorDetailsDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorKey, setErrorKey] = useState<string>();
  const [reload, setReload] = useState(0);
  const report = useEffectEvent((reason: unknown) => {
    const error = normalizeConnectorError(reason);
    setErrorKey(error.messageKey);
    if (error.code === 401)
      handleError({ code: 401, message: t(error.messageKey) });
  });
  useEffect(() => {
    if (!connectorApi) return;
    let active = true;
    connectorApi
      .listConnectors()
      .then((response) => {
        if (active) setRecords(response);
      })
      .catch((reason: unknown) => {
        if (active) report(reason);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [connectorApi, reload]);

  const available = new Map<string, ConnectorDetailsDTO>();
  for (const record of records)
    if (isConnectorId(record.connectorId))
      available.set(record.connectorId, record);
  const ids = Array.from(new Set([...enabled, ...available.keys()]));
  return (
    <Stack spacing={2}>
      <Typography variant="h6" color="primary">
        {t('Character connectors')}
      </Typography>
      <Typography variant="body2">
        {t(
          'Select up to 8 of your private connectors. No selection means no remote tools. Saved references remain until you remove them.'
        )}
      </Typography>
      <Alert severity="warning">
        {t(
          'Enabling connectors on shared characters allows viewers to invoke the owner’s remote tools. Ownership is not a side-effect sandbox. Only enable servers you trust.'
        )}
      </Alert>
      {toolFree && (
        <Alert severity="warning">
          {t(
            'This character explicitly disables all tools (tools.enabled=[]). Connectors cannot override that setting.'
          )}
        </Alert>
      )}
      <Typography variant="body2" color="text.secondary">
        {t(
          'Imported connectors are private inactive copies owned by the importing user. Review and configure them before use; existing IDs and credentials are never reused across accounts.'
        )}
      </Typography>
      <Stack direction="row" spacing={1}>
        <Button component={Link} to="/w/connectors">
          {t('Manage connectors')}
        </Button>
        <Button
          disabled={loading}
          onClick={() => {
            setLoading(true);
            setErrorKey(undefined);
            setRecords([]);
            setReload((value) => value + 1);
          }}
        >
          {errorKey ? t('Retry') : t('Refresh')}
        </Button>
      </Stack>
      {loading && (
        <CircularProgress size={24} aria-label={t('Loading connectors')} />
      )}
      {errorKey && (
        <Alert severity="error">
          {t(errorKey)}{' '}
          {t(
            'Saved selections have not been changed. You can still remove them.'
          )}
        </Alert>
      )}
      {!loading && !errorKey && ids.length === 0 && (
        <Typography variant="body2">
          {t('No connectors found. Add a connector from My connectors first.')}
        </Typography>
      )}
      <Box role="group" aria-label={t('Character connectors')}>
        {ids.map((id) => {
          const record = available.get(id);
          const selected = enabled.includes(id);
          const unavailable = !loading && !errorKey && !record;
          return (
            <Stack
              key={id}
              direction="row"
              sx={{ alignItems: 'center', justifyContent: 'space-between' }}
            >
              <FormControlLabel
                sx={{ minWidth: 0 }}
                control={
                  <Checkbox
                    checked={selected}
                    disabled={
                      disabled ||
                      (!selected &&
                        (loading ||
                          !!errorKey ||
                          !record?.active ||
                          toolFree ||
                          enabled.length >= 8))
                    }
                    onChange={(event) =>
                      onChange(
                        event.target.checked
                          ? [...enabled, id]
                          : enabled.filter((value) => value !== id)
                      )
                    }
                  />
                }
                label={
                  <Box>
                    <Typography sx={{ overflowWrap: 'anywhere' }}>
                      {record?.name ?? t('Saved connector: {{id}}', { id })}
                    </Typography>
                    {unavailable && (
                      <Typography variant="body2" color="warning.main">
                        {t(
                          'Unavailable connector. Remove this reference if it is no longer needed.'
                        )}
                      </Typography>
                    )}
                    {record?.active === false && (
                      <Typography variant="body2" color="warning.main">
                        {t(
                          'Inactive: review and configure this connector before use.'
                        )}
                      </Typography>
                    )}
                  </Box>
                }
              />
              {record?.active === false && (
                <Button
                  component={Link}
                  to={`/w/connector/${encodeURIComponent(id)}`}
                >
                  {t('Review')}
                </Button>
              )}
            </Stack>
          );
        })}
      </Box>
    </Stack>
  );
}
