import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../context";
import { Box, FormLabel, Grid, IconButton, Input, Stack, Table, Typography } from "@mui/joy";
import { AiApiKeyCreateDTO, AiApiKeyInfoDTO } from "freechat-sdk";
import { AddCircleRounded, RemoveCircleRounded } from "@mui/icons-material";
import { formatDate } from "../../libs/date_utils";
import { ConfirmModal } from "..";

export default function AiApiKeyPanel(props: {
  provider: string,
}) {
  const { provider } = props;
  const { t } = useTranslation(['account', 'button']);
  const { aiServiceApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [keys, setKeys] = useState<Array<AiApiKeyInfoDTO>>([]);
  const [keyIdToConfirm, setKeyIdToConfirm] = useState<number | undefined>();
  const [keyNameToAdd, setKeyNameToAdd] = useState<string | undefined>();
  const [keyTextToAdd, setKeyTextToAdd] = useState<string | undefined>();
  const [addingKey, setAddingKey] = useState(false);

  const labelGridUnits = 3;
  const valueGridUnits = 12 - labelGridUnits;

  useEffect(() => {
    getKeys();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [aiServiceApi]);

  function getKeys(): void {
    aiServiceApi?.listAiApiKeys(provider)
      .then(resp => setKeys(resp.filter(key => !!key.id && key.enabled)))
      .catch(handleError);
  }

  function handleKeyNameChange(event: React.ChangeEvent<HTMLInputElement>): void {
    if (event.target.value !== keyNameToAdd) {
      setKeyNameToAdd(event.target.value);
    }
  }

  function handleKeyTextChange(event: React.ChangeEvent<HTMLInputElement>): void {
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

    aiServiceApi?.addAiApiKey(request)
      .then(resp => resp && getKeys())
      .catch(handleError);
    
    setAddingKey(false);
  }

  function handleRemove(id: string | number | undefined): void {
    id && aiServiceApi?.disableAiApiKey(id as number)
      .then(resp => resp && setKeys(keys.filter(key => key.id !== id)))
      .catch(handleError);
    
    setKeyIdToConfirm(undefined);
  }

  function handleTryRemove(id: number | undefined): void {
    id && setKeyIdToConfirm(id);
  }

  return (
    <Stack spacing={3}>
      <Box sx={{
        display: 'flex',
        justifyContent: 'start',
        alignItems: 'center',
        gap: 3,
      }}>
        <Typography level="title-md">{t('API keys: (maximum of 3 keys allowed)')}</Typography>
        <IconButton
          disabled={keys.length >= 3}
          color="primary"
          onClick={() => setAddingKey(true)}
        >
          <AddCircleRounded />
        </IconButton>
      </Box>
      <Table>
        <thead>
          <tr>
            <th>{t('Name')}</th>
            <th>{t('API Key')}</th>
            <th>{t('Creation Time')}</th>
            <th>{t('Last Used Time')}</th>
            <th>{t('button:Remove')}</th>
          </tr>
        </thead>
        <tbody>
          {keys.map((key, index) => {
              return (
                <tr
                  tabIndex={-1}
                  key={key.token || `unknown-key-${index}`}
                >
                  <td>{key.name}</td>
                  <td>{key.token}</td>
                  <td>{formatDate(key.gmtCreate)}</td>
                  <td>{formatDate(key.gmtUsed)}</td>
                  <td>
                    <IconButton color="warning" onClick={() => handleTryRemove(key.id)}>
                      <RemoveCircleRounded />
                    </IconButton>
                  </td>
                </tr>
              );
            })}
        </tbody>
      </Table>
      <ConfirmModal
        open={!!keyIdToConfirm}
        onClose={() => setKeyIdToConfirm(undefined)}
        obj={keyIdToConfirm || 0}
        dialog={{
          color: 'warning',
          title: t('Please confirm carefully!'),
        }}
        button={{
          color: 'warning',
          text: t('button:Remove'),
        }}
        onConfirm={handleRemove}
      >
        <Typography>
          {keys
            .filter(key => key.id === keyIdToConfirm)
            .map(key => key.token)[0]}
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
        }}
        onConfirm={handleAdd}
      >
        <Grid container spacing={1} alignItems="center">
          <Grid xs={labelGridUnits}><FormLabel>{t('Name')}</FormLabel></Grid>
          <Grid xs={valueGridUnits}>
            <Input
              name="apiKeyName"
              value={keyNameToAdd || keyTextToAdd}
              onChange={handleKeyNameChange}
            />
          </Grid>
          <Grid xs={labelGridUnits}><FormLabel>{t('API Key')}</FormLabel></Grid>
          <Grid xs={valueGridUnits}>
            <Input
              required
              name="apiKeyText"
              value={keyTextToAdd}
              onChange={handleKeyTextChange}
            />
          </Grid>
        </Grid>
      </ConfirmModal>
    </Stack>
  );
}