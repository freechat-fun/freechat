import { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { IconButton, Input, Stack, Typography } from "@mui/joy";
import { ChatSessionDTO, MemoryUsageDTO } from "freechat-sdk";
import { CommonBox, CommonGridBox, ConfirmModal } from "..";
import { CheckRounded, KeyRounded, TuneRounded } from "@mui/icons-material";

type ChatInfoUsageProps = {
  session?: ChatSessionDTO;
  memoryUsage?: MemoryUsageDTO;
  apiKeyValue?: string;
  setApiKeyValue?: (key: string) => void;
}

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
    if (session?.context?.apiKeyName ||
      session?.context?.apiKeyValue ||
      session?.context?.quotaType === 'none'
    ) {
      return '-';
    }

    const usage = (session?.context?.quotaType == 'messages' ? memoryUsage?.messageUsage : memoryUsage?.tokenUsage) ?? 0;
    return `${usage}/${session?.context?.quota}`;
  }

  return (
    <Fragment>
      <CommonGridBox>
        <Typography level="title-sm" textColor="neutral">
          {t('My API Key')}
        </Typography>
        <CommonBox sx={{ ml: 'auto' }}>
          <IconButton onClick={() => setApiKeySettingOpen(true)}>
            <TuneRounded />
          </IconButton>
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
        onClose={(reason) => reason !== 'backdropClick' && setApiKeySettingOpen(false)}
        dialog={{
          title: t('Set API Key'),
        }}
        button={{
          text: t('button:Confirm'),
          startDecorator: <CheckRounded />
        }}
        onConfirm={() => {
          setApiKeyValue?.(editApiKey ?? '');
          setApiKeySettingOpen(false);
        }}
      >
        <Stack spacing={2}>
          <Typography level="body-sm">
            {t('Your API key is only used for this chat and will not be obtained by anyone else.')}
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
  )
}