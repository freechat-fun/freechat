import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { Box, Checkbox, Chip, IconButton, Stack, Table, Typography } from "@mui/joy";
import { AddCircleRounded, ContentCopyRounded, DeleteForeverRounded, DeleteRounded, SaveAltRounded, VisibilityRounded } from "@mui/icons-material";
import { ApiTokenInfoDTO } from "freechat-sdk";
import { formatDateTime, getSecondsBetweenDates } from "../../libs/date_utils";
import { ConfirmModal } from "..";
import { DateTimePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";

export default function ApiTokenPanel() {
  const { t } = useTranslation(['account', 'button']);
  const { accountApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [tokens, setTokens] = useState<Array<ApiTokenInfoDTO>>([]);
  const [tokenText, setTokenText] = useState<string | undefined>();
  const [tokenTextCopied, setTokenTextCopied] = useState(false);
  const [tokenIdToConfirm, setTokenIdToConfirm] = useState<number | undefined>();
  const [tokenExpiresTime, setTokenExpiresTime] = useState<Date | undefined | null>();
  const [tokenNeverExpires, setTokenNeverExpires] = useState(false);
  const [creatingToken, setCreatingToken] = useState(false);


  useEffect(() => {
    getTokens();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [accountApi]);

  function getTokens(): void {
    accountApi?.listTokens()
      .then(setTokens)
      .catch(handleError);
  }

  function handleCreate(): void {
    const duration = getSecondsBetweenDates(new Date(), tokenExpiresTime);
    if (tokenNeverExpires || duration <= 0) {
      accountApi?.createToken()
        .then(() => getTokens())
        .catch(handleError);
    } else {
      accountApi?.createToken1(duration)
        .then(() => getTokens())
        .catch(handleError);
    }
    setCreatingToken(false);
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
        <IconButton
          disabled={tokens.length >= 5}
          color="primary"
          onClick={() => {
            setTokenExpiresTime(new Date(new Date().getTime() + 24 * 60 * 60 * 1000));
            setTokenNeverExpires(false);
            setCreatingToken(true);
          }}
        >
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
                  <td>{formatDateTime(token.issuedAt)}</td>
                  <td>{formatDateTime(token.expiresAt)}</td>
                  <td>
                    <IconButton onClick={() => handleView(token.id)}>
                      <VisibilityRounded />
                    </IconButton>
                    <IconButton onClick={() => handleTryDelete(token.id)}>
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
          startDecorator: <DeleteForeverRounded />
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
          <Chip variant={tokenTextCopied ? 'outlined' : 'plain'} sx={{
            color: tokenTextCopied ? 'gray' : 'transparent',
          }}>
            {t('Copied!')}
          </Chip>
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
          startDecorator: <SaveAltRounded />
        }}
        onConfirm={handleCreate}
      >
        <Stack direction="row" spacing={2} sx={{
          alignItems: 'center',
        }}>
          <DateTimePicker
            label={t('Valid until')}
            ampm={false}
            defaultValue={dayjs(tokenExpiresTime)}
            disabled={tokenNeverExpires}
            onAccept={(value) => setTokenExpiresTime(value?.toDate())}
          />
          <Checkbox
            label={t('Never expires')}
            onChange={(event) => setTokenNeverExpires(event.target.checked)}
          />
        </Stack>
      </ConfirmModal>
    </Stack>
  );
}