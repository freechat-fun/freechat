import { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  Box,
  FormLabel,
  IconButton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  Paper,
} from '@mui/material';
import { AiApiKeyCreateDTO, AiApiKeyInfoDTO } from 'freechat-sdk';
import {
  AddCircleRounded,
  BookmarkAddRounded,
  DeleteForeverRounded,
  RemoveCircleRounded,
} from '@mui/icons-material';
import { formatDateTime } from '../../libs/date_utils';
import { ConfirmModal, TinyInput } from '..';

export default function AiApiKeyPanel(props: { provider: string }) {
  const { provider } = props;
  const { t } = useTranslation(['account', 'button']);
  const { aiServiceApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [keys, setKeys] = useState<Array<AiApiKeyInfoDTO>>([]);
  const [keyIdToConfirm, setKeyIdToConfirm] = useState<number>();
  const [keyNameToAdd, setKeyNameToAdd] = useState<string>();
  const [keyTextToAdd, setKeyTextToAdd] = useState<string>();
  const [addingKey, setAddingKey] = useState(false);

  const getKeys = useCallback(() => {
    aiServiceApi
      ?.listAiApiKeys(provider)
      .then((resp) => setKeys(resp.filter((key) => !!key.id && key.enabled)))
      .catch(handleError);
  }, [aiServiceApi, handleError, provider]);

  useEffect(() => {
    getKeys();
  }, [getKeys]);

  function handleKeyNameChange(
    event: React.ChangeEvent<HTMLInputElement>
  ): void {
    if (event.target.value !== keyNameToAdd) {
      setKeyNameToAdd(event.target.value);
    }
  }

  function handleKeyTextChange(
    event: React.ChangeEvent<HTMLInputElement>
  ): void {
    if (event.target.value !== keyTextToAdd) {
      setKeyTextToAdd(event.target.value);
    }
  }

  function handleAdd(): void {
    if (!keyTextToAdd) {
      return;
    }

    const request = new AiApiKeyCreateDTO();
    request.name = keyNameToAdd || keyTextToAdd;
    request.token = keyTextToAdd;
    request.provider = provider;
    request.enabled = true;

    aiServiceApi
      ?.addAiApiKey(request)
      .then((resp) => resp && getKeys())
      .catch(handleError);

    setAddingKey(false);
  }

  function handleRemove(id: string | number | undefined): void {
    if (id) {
      aiServiceApi
        ?.disableAiApiKey(id as number)
        .then((resp) => resp && setKeys(keys.filter((key) => key.id !== id)))
        .catch(handleError);
    }

    setKeyIdToConfirm(undefined);
  }

  function handleTryRemove(id: number | undefined): void {
    if (id) {
      setKeyIdToConfirm(id);
    }
  }

  return (
    <Stack spacing={3}>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'start',
          alignItems: 'center',
          gap: 3,
        }}
      >
        <Typography variant="h6">
          {t('API keys: (maximum of 3 keys allowed)')}
        </Typography>
        <IconButton
          disabled={keys.length >= 3}
          color="primary"
          onClick={() => setAddingKey(true)}
        >
          <AddCircleRounded />
        </IconButton>
      </Box>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>{t('Name')}</TableCell>
              <TableCell>{t('API Key')}</TableCell>
              <TableCell>{t('Creation Time')}</TableCell>
              <TableCell>{t('Last Used Time')}</TableCell>
              <TableCell>{t('button:Remove')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {keys.map((key, index) => (
              <TableRow key={key.token || `unknown-key-${index}`}>
                <TableCell>{key.name}</TableCell>
                <TableCell>{key.token}</TableCell>
                <TableCell>{formatDateTime(key.gmtCreate)}</TableCell>
                <TableCell>{formatDateTime(key.gmtUsed)}</TableCell>
                <TableCell>
                  <IconButton onClick={() => handleTryRemove(key.id)}>
                    <RemoveCircleRounded />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <ConfirmModal
        open={!!keyIdToConfirm}
        onClose={() => setKeyIdToConfirm(undefined)}
        obj={keyIdToConfirm || 0}
        dialog={{
          color: 'error',
          title: t('Please confirm carefully!'),
        }}
        button={{
          color: 'error',
          text: t('button:Remove'),
          startIcon: <DeleteForeverRounded />,
        }}
        onConfirm={handleRemove}
      >
        <Typography>
          {
            keys
              .filter((key) => key.id === keyIdToConfirm)
              .map((key) => key.token)[0]
          }
        </Typography>
      </ConfirmModal>
      <ConfirmModal
        open={addingKey}
        onClose={() => setAddingKey(false)}
        dialog={{
          title: t('Add API Key'),
        }}
        button={{
          text: t('button:Add'),
          startIcon: <BookmarkAddRounded />,
        }}
        onConfirm={handleAdd}
      >
        <Box
          sx={{
            flex: 1,
            display: 'grid',
            gridTemplateColumns: 'auto 1fr',
            alignItems: 'center',
            gap: 2,
          }}
        >
          <FormLabel>{t('Name')}</FormLabel>
          <TinyInput
            name="apiKeyName"
            value={keyNameToAdd || keyTextToAdd}
            onChange={handleKeyNameChange}
            fullWidth
          />
          <FormLabel>{t('API Key')}</FormLabel>
          <TinyInput
            required
            name="apiKeyText"
            value={keyTextToAdd}
            onChange={handleKeyTextChange}
            sx={{
              minWidth: '16rem',
            }}
            fullWidth
          />
        </Box>
      </ConfirmModal>
    </Stack>
  );
}
