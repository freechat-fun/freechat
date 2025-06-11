import { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  Box,
  Checkbox,
  Chip,
  FormControlLabel,
  IconButton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import {
  AddCircleRounded,
  ContentCopyRounded,
  DeleteForeverRounded,
  DeleteRounded,
  SaveAltRounded,
  VisibilityRounded,
} from '@mui/icons-material';
import { ApiTokenInfoDTO } from 'freechat-sdk';
import { formatDateTime, getSecondsBetweenDates } from '../../libs/date_utils';
import { ConfirmModal } from '..';
import { DateTimePicker } from '@mui/x-date-pickers';
import dayjs from 'dayjs';

export default function ApiTokenPanel() {
  const { t } = useTranslation(['account', 'button']);
  const { accountApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [tokens, setTokens] = useState<Array<ApiTokenInfoDTO>>([]);
  const [tokenText, setTokenText] = useState<string>();
  const [tokenTextCopied, setTokenTextCopied] = useState(false);
  const [tokenIdToConfirm, setTokenIdToConfirm] = useState<number>();
  const [tokenExpiresTime, setTokenExpiresTime] = useState<Date | null>();
  const [tokenNeverExpires, setTokenNeverExpires] = useState(false);
  const [creatingToken, setCreatingToken] = useState(false);

  const getTokens = useCallback(() => {
    accountApi?.listTokens().then(setTokens).catch(handleError);
  }, [accountApi, handleError]);

  useEffect(() => {
    getTokens();
  }, [getTokens]);

  function handleCreate(): void {
    const duration = getSecondsBetweenDates(new Date(), tokenExpiresTime);
    if (tokenNeverExpires || duration <= 0) {
      accountApi
        ?.createToken1()
        .then(() => getTokens())
        .catch(handleError);
    } else {
      accountApi
        ?.createToken(duration)
        .then(() => getTokens())
        .catch(handleError);
    }
    setCreatingToken(false);
  }

  function handleView(id: number | undefined): void {
    if (id) {
      accountApi
        ?.getTokenById(id)
        .then((resp) => setTokenText(resp))
        .catch(handleError);
    }
  }

  function handleViewClose(): void {
    setTokenText(undefined);
    setTokenTextCopied(false);
  }

  function handleDelete(id: string | number | undefined): void {
    if (id) {
      accountApi
        ?.disableTokenById(id as number)
        .then(
          (resp) => resp && setTokens(tokens.filter((token) => token.id !== id))
        )
        .catch(handleError);
    }

    setTokenIdToConfirm(undefined);
  }

  function handleTryDelete(id: number | undefined): void {
    if (id) {
      setTokenIdToConfirm(id);
    }
  }

  return (
    <Stack spacing={3}>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'flex-start',
          alignItems: 'center',
          gap: 3,
        }}
      >
        <Typography variant="h6">
          {t('API tokens: (maximum of 5 tokens allowed)')}
        </Typography>
        <IconButton
          disabled={tokens.length >= 5}
          color="primary"
          onClick={() => {
            setTokenExpiresTime(
              new Date(new Date().getTime() + 24 * 60 * 60 * 1000)
            );
            setTokenNeverExpires(false);
            setCreatingToken(true);
          }}
        >
          <AddCircleRounded />
        </IconButton>
      </Box>
      <TableContainer sx={{ borderRadius: '6px' }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell sx={{ backgroundColor: 'action.hover' }}>
                {t('Token')}
              </TableCell>
              <TableCell sx={{ backgroundColor: 'action.hover' }}>
                {t('Creation Time')}
              </TableCell>
              <TableCell sx={{ backgroundColor: 'action.hover' }}>
                {t('Expiration Time')}
              </TableCell>
              <TableCell sx={{ backgroundColor: 'action.hover' }}>
                {t('Actions')}
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {tokens.map((token, index) => (
              <TableRow key={token.token || `unknown-token-${index}`}>
                <TableCell>{token.token}</TableCell>
                <TableCell>{formatDateTime(token.issuedAt)}</TableCell>
                <TableCell>{formatDateTime(token.expiresAt)}</TableCell>
                <TableCell>
                  <IconButton onClick={() => handleView(token.id)}>
                    <VisibilityRounded />
                  </IconButton>
                  <IconButton onClick={() => handleTryDelete(token.id)}>
                    <DeleteRounded />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <ConfirmModal
        open={!!tokenIdToConfirm}
        onClose={() => setTokenIdToConfirm(undefined)}
        obj={tokenIdToConfirm || 0}
        dialog={{
          color: 'error',
          title: t('Please confirm carefully!'),
        }}
        button={{
          color: 'error',
          text: t('button:Delete'),
          startIcon: <DeleteForeverRounded />,
        }}
        onConfirm={handleDelete}
      >
        <Typography sx={{ textDecoration: 'line-through' }}>
          {
            tokens
              .filter((token) => token.id === tokenIdToConfirm)
              .map((token) => token.token)[0]
          }
        </Typography>
      </ConfirmModal>
      <ConfirmModal
        open={!!tokenText}
        onClose={handleViewClose}
        obj={tokenText || ''}
        onConfirm={handleViewClose}
        dialog={{
          color: 'success',
          title: t('Token Information'),
        }}
      >
        <Box
          sx={{
            mx: 2,
            display: 'flex',
            flexDirection: 'column',
          }}
        >
          <Box
            sx={{
              mt: 2,
              gap: 1.5,
              display: 'flex',
              alignItems: 'center',
            }}
          >
            <Typography>{tokenText}</Typography>
            <IconButton
              onClick={() => {
                if (tokenText) {
                  navigator?.clipboard
                    ?.writeText(tokenText)
                    .then(() => setTokenTextCopied(true))
                    .catch(handleError);
                }
              }}
            >
              <ContentCopyRounded />
            </IconButton>
          </Box>
          <Chip
            variant={tokenTextCopied ? 'outlined' : 'filled'}
            sx={{
              color: tokenTextCopied ? 'text.secondary' : 'transparent',
              display: tokenTextCopied ? 'inherit' : 'none',
            }}
            label={t('Copied!')}
          />
        </Box>
      </ConfirmModal>
      <ConfirmModal
        open={creatingToken}
        onClose={() => setCreatingToken(false)}
        dialog={{
          title: t('Create Token'),
        }}
        button={{
          text: t('button:Create'),
          startIcon: <SaveAltRounded />,
        }}
        onConfirm={handleCreate}
      >
        <Stack
          direction="row"
          spacing={2}
          sx={{
            alignItems: 'center',
          }}
        >
          <DateTimePicker
            label={t('Valid until')}
            ampm={false}
            defaultValue={dayjs(tokenExpiresTime)}
            disabled={tokenNeverExpires}
            onAccept={(value) => setTokenExpiresTime(value?.toDate())}
          />
          <FormControlLabel
            control={
              <Checkbox
                checked={tokenNeverExpires}
                onChange={(event) => setTokenNeverExpires(event.target.checked)}
              />
            }
            label={t('Never expires')}
          />
        </Stack>
      </ConfirmModal>
    </Stack>
  );
}
