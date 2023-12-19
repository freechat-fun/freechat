import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../context";
import { Box, IconButton, Stack, Table, Typography } from "@mui/joy";
import { AddCircleRounded, DeleteRounded, VisibilityRounded } from "@mui/icons-material";
import { ApiTokenInfoDTO } from "freechat-sdk";
import { formatDate } from "../../libs/date_utils";
import { ConfirmModal } from "..";

export default function ApiTokenPanel() {
  const { t } = useTranslation(['account', 'button']);
  const { accountApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const [tokens, setTokens] = useState<Array<ApiTokenInfoDTO>>([]);
  const [, setTokenText] = useState<string | undefined>();
  const [tokenIdToConfirm, setTokenIdToConfirm] = useState<number | undefined>();

  useEffect(() => {
    getTokens();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [accountApi]);

  function getTokens(): void {
    accountApi?.listTokens()
      .then(resp => {console.log(resp); setTokens(resp)})
      .catch(handleError);
  }

  function handleView(id: number | undefined): void {
    id && accountApi?.getTokenById(id)
      .then(resp => setTokenText(resp))
      .catch(handleError);
  }

  function handleDelete(id: string | number): void {
    id && accountApi?.disableTokenById(id as number)
      .then(resp => resp && setTokens(tokens.filter(token => token.id !== id)))
      .catch(handleError);
    
    setTokenIdToConfirm(undefined);
  }

  function handleTryDelete(id: number | undefined): void {
    id && setTokenIdToConfirm(id);
  }

  return (
    <Stack spacing={3}>
      <Box sx={{
        display: 'flex',
        justifyContent: 'start',
        alignItems: 'center',
        gap: 3,
      }}>
        <Typography level="title-md">{t('API tokens: (maximum of 5 tokens allowed)')}</Typography>
        <IconButton color="primary">
          <AddCircleRounded />
        </IconButton>
      </Box>
      <Table>
        <thead>
          <tr>
            <th>{t('Token')}</th>
            <th>{t('Creation Time')}</th>
            <th>{t('Expiration Time')}</th>
            <th>{t('Actions')}</th>
          </tr>
        </thead>
        <tbody>
          {tokens.map((token, index) => {
              return (
                <tr
                  tabIndex={-1}
                  key={token.token || `unknown-token-${index}`}
                >
                  <td>{token.token}</td>
                  <td>{formatDate(token.issuedAt)}</td>
                  <td>{formatDate(token.expiresAt)}</td>
                  <td>
                    <IconButton onClick={() => handleView(token.id)}>
                      <VisibilityRounded />
                    </IconButton>
                    <IconButton color="danger" onClick={() => handleTryDelete(token.id)}>
                      <DeleteRounded />
                    </IconButton>
                  </td>
                </tr>
              );
            })}
        </tbody>
      </Table>
      <ConfirmModal
        open={!!tokenIdToConfirm}
        onClose={() => setTokenIdToConfirm(undefined)}
        obj={tokenIdToConfirm || 0}
        button={{
          color: 'danger',
          text: t('button:Delete'),
        }}
        onConfirm={handleDelete}
      >
        <Typography sx={{textDecoration: 'line-through'}}>
          {tokens
            .filter(token => token.id === tokenIdToConfirm)
            .map(token => token.token)[0]}
        </Typography>
      </ConfirmModal>
    </Stack>
  );
}