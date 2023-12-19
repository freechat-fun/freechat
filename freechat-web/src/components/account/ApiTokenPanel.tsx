import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../context";
import { Box, IconButton, Stack, Table, Typography } from "@mui/joy";
import { AddCircleRounded, ContentCopyRounded, DeleteRounded, VisibilityRounded } from "@mui/icons-material";
import { ApiTokenInfoDTO } from "freechat-sdk";
import { formatDate } from "../../libs/date_utils";
import { ConfirmModal } from "..";

export default function ApiTokenPanel() {
  const { t } = useTranslation(['account', 'button']);
  const { accountApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const [tokens, setTokens] = useState<Array<ApiTokenInfoDTO>>([]);
  const [tokenText, setTokenText] = useState<string | undefined>();
  const [tokenTextCopied, setTokenTextCopied] = useState(false);
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

  function handleViewClose(): void {
    setTokenText(undefined);
    setTokenTextCopied(false);
  }

  function handleDelete(id: string | number | undefined): void {
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
        <IconButton color="primary" disabled={tokens.length >= 5}>
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
        dialog={{
          color: 'danger',
          title: t('Please confirm carefully!'),
        }}
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
        <Box sx={{
          m: 2,
          display: 'flex',
          flexDirection: 'column',
        }}>
          <Box sx={{
            mt: 2,
            gap: 1.5,
            display: 'flex',
            alignItems: 'center',
          }}>
            <Typography>
              {tokenText}
            </Typography>
            <IconButton onClick={() => {
              tokenText && navigator?.clipboard?.writeText(tokenText)
                .then(() => setTokenTextCopied(true))
                .catch(handleError);
            }}>
              <ContentCopyRounded />
            </IconButton>
          </Box>
          <Typography sx={{
            fontStyle: 'italic',
            color: tokenTextCopied ? 'gray' : 'transparent',
          }}>
            {t('Copied!')}
          </Typography>
        </Box>
      </ConfirmModal>
    </Stack>
  );
}