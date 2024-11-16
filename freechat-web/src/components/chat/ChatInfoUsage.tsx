import { Fragment, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { IconButton, Input, Stack, Tooltip, Typography } from '@mui/joy';
import { CheckRounded, KeyRounded, TuneRounded } from '@mui/icons-material';
import { ChatSessionDTO, MemoryUsageDTO } from 'freechat-sdk';
import { CommonBox, CommonGridBox, ConfirmModal } from '..';
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
      <CommonGridBox>
        <Typography level="title-sm" textColor="neutral">
          {t('My API Key', { ns: 'account' })}
        </Typography>
        <CommonBox sx={{ ml: 'auto' }}>
          <Tooltip
            size="sm"
            sx={{ maxWidth: '20rem' }}
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
              >
                <TuneRounded />
              </IconButton>
            </div>
          </Tooltip>
        </CommonBox>

        <Typography level="title-sm" textColor="neutral">
          {t('Usage')}
        </Typography>
        <Typography level="body-sm" sx={{ ml: 'auto' }}>
          {getUsageLabel()}
        </Typography>
      </CommonGridBox>

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
          startDecorator: <CheckRounded />,
        }}
        onConfirm={() => {
          setApiKeyValue?.(editApiKey ?? '');
          setApiKeySettingOpen(false);
        }}
      >
        <Stack spacing={2}>
          <Typography level="body-sm">
            {t(
              'Your API key is only used for this chat and will not be obtained by anyone else.'
            )}
          </Typography>
          <Input
            type="password"
            placeholder="Paste a key here..."
            startDecorator={<KeyRounded />}
            value={editApiKey}
            onChange={(event) => setEditApiKey(event.target.value)}
            sx={{
              minWidth: '20rem',
            }}
          />
        </Stack>
      </ConfirmModal>
    </Fragment>
  );
}
