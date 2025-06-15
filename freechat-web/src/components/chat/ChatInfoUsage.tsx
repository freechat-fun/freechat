import { Fragment, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  IconButton,
  TextField,
  Stack,
  Typography,
  InputAdornment,
} from '@mui/material';
import { CheckRounded, KeyRounded, TuneRounded } from '@mui/icons-material';
import { ChatSessionDTO, MemoryUsageDTO } from 'freechat-sdk';
import { FlexBox, GridBox, ConfirmModal, OptionTooltip } from '..';
import { providers as modelProviders } from '../../configs/model-providers-config';

type ChatInfoUsageProps = {
  session?: ChatSessionDTO;
  memoryUsage?: MemoryUsageDTO;
  apiKeyValue?: string;
  setApiKeyValue?: (key: string) => void;
};

export default function ChatInfoUsage({
  session,
  memoryUsage,
  apiKeyValue,
  setApiKeyValue,
}: ChatInfoUsageProps) {
  const { t } = useTranslation('chat');

  const [editApiKey, setEditApiKey] = useState(apiKeyValue);
  const [apiKeySettingOpen, setApiKeySettingOpen] = useState(false);

  useEffect(() => {
    setEditApiKey(apiKeyValue);
  }, [apiKeyValue]);

  function getUsageLabel(): string {
    if (
      session?.context?.apiKeyName ||
      session?.context?.apiKeyValue ||
      session?.context?.quotaType === 'none' ||
      !session?.context?.quota
    ) {
      return '-';
    }

    const usage =
      (session?.context?.quotaType == 'messages'
        ? memoryUsage?.messageUsage
        : memoryUsage?.tokenUsage) ?? 0;
    return `${usage}/${session?.context?.quota}`;
  }

  function getProviderLabel(): string {
    if (!session?.provider) {
      return '';
    }

    const providerLabel =
      modelProviders
        .filter((provider) => provider.provider === session.provider)
        .map((provider) => provider.label) ?? session.provider;

    return ` - ${providerLabel}`;
  }

  return (
    <Fragment>
      <GridBox>
        <Typography variant="subtitle1" color="text.secondary">
          {t('My API Key', { ns: 'account' })}
        </Typography>
        <FlexBox sx={{ ml: 'auto' }}>
          <OptionTooltip
            title={
              session?.isCustomizedApiKeyEnabled
                ? t('Customize API key for the chat')
                : t('The character does not support custom API key')
            }
          >
            <div>
              <IconButton
                disabled={!session?.isCustomizedApiKeyEnabled}
                onClick={() => setApiKeySettingOpen(true)}
                size="small"
              >
                <TuneRounded />
              </IconButton>
            </div>
          </OptionTooltip>
        </FlexBox>

        <Typography variant="subtitle1" color="text.secondary">
          {t('Usage')}
        </Typography>
        <Typography variant="body2" sx={{ ml: 'auto' }}>
          {getUsageLabel()}
        </Typography>
      </GridBox>

      <ConfirmModal
        open={apiKeySettingOpen}
        onClose={(reason) =>
          reason !== 'backdropClick' && setApiKeySettingOpen(false)
        }
        dialog={{
          title: `${t('Set API Key', { ns: 'prompt' })}${getProviderLabel()}`,
        }}
        button={{
          text: t('button:Confirm'),
          startIcon: <CheckRounded />,
        }}
        onConfirm={() => {
          setApiKeyValue?.(editApiKey ?? '');
          setApiKeySettingOpen(false);
        }}
      >
        <Stack spacing={2} sx={{ mx: 2 }}>
          <Typography variant="body2">
            {t(
              'Your API key is only used for this chat and will not be obtained by anyone else.'
            )}
          </Typography>
          <TextField
            type="password"
            placeholder="Paste a key here..."
            value={editApiKey}
            onChange={(event) => setEditApiKey(event.target.value)}
            slotProps={{
              input: {
                startAdornment: (
                  <InputAdornment position="start">
                    <KeyRounded />
                  </InputAdornment>
                ),
              },
            }}
            size="small"
            sx={{ minWidth: '20rem' }}
          />
        </Stack>
      </ConfirmModal>
    </Fragment>
  );
}
