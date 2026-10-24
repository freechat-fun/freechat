import { useEffect, useEffectEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { AddCircleRounded, SearchRounded } from '@mui/icons-material';
import {
  Alert,
  Box,
  Button,
  Card,
  CardActionArea,
  CardContent,
  Chip,
  CircularProgress,
  InputAdornment,
  Pagination,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import type { ConnectorDetailsDTO } from 'freechat-sdk';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import ConnectorDialog from '../../components/connector/ConnectorDialog';
import { getDateLabel } from '../../libs/date_utils';
import { normalizeConnectorError } from '../../libs/connector_utils';

export default function Connectors() {
  const { username } = useMetaInfoContext();
  return username ? <ConnectorList key={username} /> : null;
}

function ConnectorList() {
  const { t, i18n } = useTranslation('connector');
  const { connectorApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const navigate = useNavigate();
  const [records, setRecords] = useState<ConnectorDetailsDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorKey, setErrorKey] = useState<string>();
  const [reload, setReload] = useState(0);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const [creating, setCreating] = useState(false);
  const [savedId, setSavedId] = useState<string>();
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
  useEffect(() => {
    if (savedId) navigate(`/w/connector/${encodeURIComponent(savedId)}`);
  }, [navigate, savedId]);

  const query = search.trim().toLocaleLowerCase();
  const filtered = records
    .filter((record) =>
      [record.name, record.transport, record.url].some((value) =>
        value?.toLocaleLowerCase().includes(query)
      )
    )
    .sort(
      (a, b) => (b.modified?.getTime() ?? 0) - (a.modified?.getTime() ?? 0)
    );
  const pages = Math.max(1, Math.ceil(filtered.length / 6));
  const currentPage = Math.min(page, pages);
  return (
    <Stack spacing={3} sx={{ py: 2, maxWidth: 1344 }}>
      <Typography variant="h4">{t('My connectors')}</Typography>
      <Typography color="text.secondary">
        {t(
          'Private remote HTTP/SSE MCP connectors. Only you can manage their configuration and credentials.'
        )}
      </Typography>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        sx={{ justifyContent: 'space-between' }}
      >
        <TextField
          size="small"
          label={t('Search names, URLs and transports')}
          value={search}
          onChange={(event) => {
            setSearch(event.target.value);
            setPage(1);
          }}
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRounded />
                </InputAdornment>
              ),
            },
          }}
          sx={{ width: { sm: 420 } }}
        />
        <Button
          variant="contained"
          startIcon={<AddCircleRounded />}
          sx={{ borderRadius: '20px' }}
          disabled={loading || !!errorKey || records.length >= 32}
          onClick={() => setCreating(true)}
        >
          {t('Add connector')}
        </Button>
      </Stack>
      {records.length >= 32 && (
        <Alert severity="info">
          {t(
            'You have reached the limit of 32 connectors. Delete an unused connector to add another.'
          )}
        </Alert>
      )}
      {loading ? (
        <CircularProgress aria-label={t('Loading connectors')} />
      ) : errorKey ? (
        <Alert
          severity="error"
          action={
            <Button
              onClick={() => {
                setLoading(true);
                setErrorKey(undefined);
                setReload((value) => value + 1);
              }}
            >
              {t('Retry')}
            </Button>
          }
        >
          {t(errorKey)}
        </Alert>
      ) : (
        <>
          {filtered.length === 0 && (
            <Typography>{t('No connectors found.')}</Typography>
          )}
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns:
                'repeat(auto-fill, minmax(min(100%, 320px), 1fr))',
              gap: 3,
            }}
          >
            {filtered
              .slice((currentPage - 1) * 6, currentPage * 6)
              .map((record) => (
                <Card
                  key={record.connectorId}
                  variant="outlined"
                  sx={{ borderRadius: 2 }}
                >
                  <CardActionArea
                    onClick={() =>
                      navigate(
                        `/w/connector/${encodeURIComponent(record.connectorId ?? '')}`
                      )
                    }
                    sx={{ height: '100%' }}
                  >
                    <CardContent>
                      <Typography variant="h6" noWrap>
                        {record.name}
                      </Typography>
                      <Stack
                        direction="row"
                        sx={{ justifyContent: 'space-between', my: 1 }}
                      >
                        <Typography variant="body2" color="text.secondary">
                          {getDateLabel(
                            record.modified ?? new Date(0),
                            i18n.language
                          )}
                        </Typography>
                        <Typography variant="body2" color="warning.main">
                          {t('Private')}
                        </Typography>
                      </Stack>
                      <Stack direction="row" spacing={1} sx={{ my: 1 }}>
                        <Chip
                          label={record.transport?.toUpperCase()}
                          size="small"
                          variant="outlined"
                          color="success"
                        />
                        <Chip
                          label={record.active ? t('Active') : t('Inactive')}
                          size="small"
                          variant="outlined"
                          color={record.active ? 'success' : 'warning'}
                        />
                      </Stack>
                      <Typography
                        variant="body2"
                        sx={{ overflowWrap: 'anywhere' }}
                      >
                        {record.url}
                      </Typography>
                      <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{ mt: 1 }}
                      >
                        {t('{{count}} configured headers', {
                          count: record.headerNames?.length ?? 0,
                        })}
                      </Typography>
                    </CardContent>
                  </CardActionArea>
                </Card>
              ))}
          </Box>
          {pages > 1 && (
            <Pagination
              count={pages}
              page={currentPage}
              onChange={(_, value) => setPage(value)}
              getItemAriaLabel={(type, value) =>
                type === 'page'
                  ? t('Page {{page}}', { page: value })
                  : type === 'previous'
                    ? t('Previous page')
                    : t('Next page')
              }
              sx={{ alignSelf: 'flex-end' }}
            />
          )}
        </>
      )}
      {creating && (
        <ConnectorDialog
          onClose={() => setCreating(false)}
          onSaved={(record) => {
            setCreating(false);
            setRecords((previous) => [...previous, record]);
            setSavedId(record.connectorId);
          }}
        />
      )}
    </Stack>
  );
}
